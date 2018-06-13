package yirgacheffe.compiler.listener;

import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ParameterisedType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.Arrays;

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
		if (context.parameter().size() != 1)
		{
			this.addError(context);
		}
		else
		{
			Type parameterType = this.types.getType(context.parameter().get(0).type());
			ReferenceType arrayType = new ReferenceType(yirgacheffe.lang.Array.class);
			Type stringType = new ReferenceType(java.lang.String.class);
			Type argsType =
				new ParameterisedType(arrayType, Arrays.asList(stringType));

			if (!argsType.isAssignableTo(parameterType))
			{
				this.addError(context);
			}
		}

		this.mainMethodName = context.Identifier().getText();

		String descriptor = this.getDescriptor(context.parameter()) + "V";

		this.methodVisitor =
			this.writer.visitMethod(
				Opcodes.ACC_PUBLIC,
				this.mainMethodName,
				descriptor,
				null,
				null);

		this.returnType = PrimitiveType.VOID;
	}

	private void addError(YirgacheffeParser.MainMethodDeclarationContext context)
	{
		String message =
			"Main method must have exactly one parameter of " +
			"type yirgacheffe.lang.Array<java.lang.String>.";

		this.errors.add(new Error(context, message));
	}
}
