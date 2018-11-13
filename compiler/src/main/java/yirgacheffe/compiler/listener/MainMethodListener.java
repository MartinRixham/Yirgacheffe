package yirgacheffe.compiler.listener;

import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ParameterisedType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;
import yirgacheffe.parser.YirgacheffeParser;

public class MainMethodListener extends MethodListener
{
	public MainMethodListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void exitMainMethodDeclaration(
		YirgacheffeParser.MainMethodDeclarationContext context)
	{
		this.checkPrivate(context);

		YirgacheffeParser.SignatureContext signature = context.signature();

		if (signature.parameter().size() != 1)
		{
			this.addParameterError(context);
		}
		else
		{
			Type parameterType = this.types.getType(signature.parameter().get(0).type());
			ReferenceType arrayType = new ReferenceType(yirgacheffe.lang.Array.class);
			Type stringType = new ReferenceType(java.lang.String.class);
			Type argsType =
				new ParameterisedType(arrayType, new Array<>(stringType));

			if (!argsType.isAssignableTo(parameterType))
			{
				this.addParameterError(context);
			}
		}

		this.mainMethodName = signature.Identifier().getText();

		String descriptor = this.descriptor + "V";

		this.methodVisitor =
			this.writer.visitMethod(
				Opcodes.ACC_PUBLIC,
				this.mainMethodName,
				descriptor,
				null,
				null);

		this.returnType = PrimitiveType.VOID;
	}

	private void checkPrivate(YirgacheffeParser.MainMethodDeclarationContext context)
	{
		boolean isPrivate =
			context.modifier() != null && context.modifier().Private() != null;

		if (isPrivate)
		{
			String message = "Main method cannot be private.";

			this.errors.push(new Error(context, message));
		}
	}

	private void addParameterError(YirgacheffeParser.MainMethodDeclarationContext context)
	{
		String message =
			"Main method must have exactly one parameter of " +
			"type yirgacheffe.lang.Array<java.lang.String>.";

		this.errors.push(new Error(context, message));
	}
}
