package yirgacheffe.compiler.listener;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ParameterisedType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;
import yirgacheffe.parser.YirgacheffeParser;

public class MainMethodListener extends ParallelMethodListener
{
	public MainMethodListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void enterMainMethodDeclaration(
		YirgacheffeParser.MainMethodDeclarationContext context)
	{
		this.returnType = PrimitiveType.VOID;
	}

	@Override
	public void exitMainMethodDeclaration(
		YirgacheffeParser.MainMethodDeclarationContext context)
	{
		this.inMethod = true;

		if (this.inInterface)
		{
			String message = "Method body not permitted for interface method.";

			this.errors.push(new Error(context, message));
		}

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

		this.checkMultipleMainMethods(context);

		this.mainMethodName = signature.Identifier().getText();

		this.methodNode =
			new MethodNode(
				Opcodes.ACC_PUBLIC,
				this.mainMethodName,
				this.signature.getDescriptor(),
				this.signature.getSignature(),
				null);

		this.classNode.methods.add(this.methodNode);
	}

	private void checkMultipleMainMethods(
		YirgacheffeParser.MainMethodDeclarationContext context)
	{
		if (this.mainMethodName != null)
		{
			String message = "Cannot have multiple main methods.";

			this.errors.push(new Error(context, message));
		}
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
