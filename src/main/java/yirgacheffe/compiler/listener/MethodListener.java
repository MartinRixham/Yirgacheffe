package yirgacheffe.compiler.listener;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.Type.BytecodeClassLoader;
import yirgacheffe.compiler.Type.Types;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.error.ParseErrorListener;
import yirgacheffe.compiler.Type.Type;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.List;

public class MethodListener extends TypeListener
{
	public MethodListener(
		String directory,
		Types types,
		BytecodeClassLoader classLoader,
		ParseErrorListener errorListener,
		ClassWriter writer)
	{
		super(directory, types, classLoader, errorListener, writer);
	}

	@Override
	public void enterInterfaceMethodDeclaration(
		YirgacheffeParser.InterfaceMethodDeclarationContext context)
	{
		if (context.Modifier() != null)
		{
			String message =
				"Access modifier is not required for interface method declaration.";

			this.errors.add(new Error(context, message));
		}

		String descriptor =
			this.getMethodDescriptor(context.parameter(), context.type());

		this.writer.visitMethod(
			Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT,
			context.Identifier().getText(),
			descriptor,
			null,
			null);
	}

	@Override
	public void enterClassMethodDeclaration(
		YirgacheffeParser.ClassMethodDeclarationContext context)
	{
		boolean isPrivate = false;

		if (context.Modifier() == null)
		{
			String message =
				"Expected public or private access modifier " +
				"at start of method declaration.";

			this.errors.add(new Error(context, message));
		}
		else
		{
			isPrivate = context.Modifier().getText().equals("private");
		}

		String name = "<init>";

		if (context.Identifier() != null)
		{
			name = context.Identifier().getText();
		}

		String descriptor =
			this.getMethodDescriptor(context.parameter(), context.type());

		this.writer.visitMethod(
			isPrivate ? Opcodes.ACC_PRIVATE : Opcodes.ACC_PUBLIC,
			name,
			descriptor,
			null,
			null);
	}

	private String getMethodDescriptor(
		List<YirgacheffeParser.ParameterContext> parameters,
		YirgacheffeParser.TypeContext returnType)
	{
		return
			this.getParameterDescriptor(parameters) +
				this.types.getType(returnType).toJVMType();
	}

	private String getParameterDescriptor(
		List<YirgacheffeParser.ParameterContext> parameterList)
	{
		StringBuilder descriptor = new StringBuilder("(");

		for (YirgacheffeParser.ParameterContext parameter : parameterList)
		{
			YirgacheffeParser.TypeContext typeContext = parameter.type();

			if (typeContext != null)
			{
				Type type = this.types.getType(typeContext);

				descriptor.append(type.toJVMType());
			}
		}

		descriptor.append(")");

		return descriptor.toString();
	}

	@Override
	public void enterParameter(YirgacheffeParser.ParameterContext context)
	{
		if (context.type() == null)
		{
			Error error =
				new Error(context, "Expected type before parameter identifier.");

			this.errors.add(error);
		}
	}
}
