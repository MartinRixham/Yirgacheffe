package yirgacheffe.compiler.listener;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.Type.BytecodeClassLoader;
import yirgacheffe.compiler.Type.DeclaredType;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.error.ParseErrorListener;
import yirgacheffe.compiler.Type.Type;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.List;
import java.util.Map;

public class MethodListener extends TypeListener
{
	public MethodListener(
		String directory,
		Map<String, DeclaredType> declaredTypes,
		BytecodeClassLoader classLoader,
		ParseErrorListener errorListener,
		ClassWriter writer)
	{
		super(directory, declaredTypes, classLoader, errorListener, writer);
	}

	@Override
	public void enterInterfaceMethodDeclaration(
		YirgacheffeParser.InterfaceMethodDeclarationContext context)
	{
		if (context.Modifier() == null)
		{
			String descriptor =
				this.getMethodDescriptor(context.parameter(), context.type());

			this.writer.visitMethod(
				Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT,
				context.Identifier().getText(),
				descriptor,
				null,
				null);
		}
		else
		{
			Error error =
				new Error(
					context,
					"Access modifier is not required for interface method declaration.");

			this.errors.add(error);
		}
	}

	@Override
	public void enterClassMethodDeclaration(
		YirgacheffeParser.ClassMethodDeclarationContext context)
	{
		if (context.Modifier() == null)
		{
			String message =
				"Expected public or private access modifier " +
				"at start of method declaration.";

			this.errors.add(new Error(context, message));
		}
		else
		{
			String descriptor =
				this.getMethodDescriptor(context.parameter(), context.type());
			boolean isPrivate = context.Modifier().getText().equals("private");

			this.writer.visitMethod(
				isPrivate ? Opcodes.ACC_PRIVATE : Opcodes.ACC_PUBLIC,
				context.Identifier().getText(),
				descriptor,
				null,
				null);
		}
	}

	private String getMethodDescriptor(
		List<YirgacheffeParser.ParameterContext> parameters,
		YirgacheffeParser.TypeContext returnType)
	{
		return
			this.getParameterDescriptor(parameters) +
				this.getType(returnType).toJVMType();
	}

	protected String getParameterDescriptor(
		List<YirgacheffeParser.ParameterContext> parameterList)
	{
		StringBuilder descriptor = new StringBuilder("(");

		for (YirgacheffeParser.ParameterContext parameter : parameterList)
		{
			YirgacheffeParser.TypeContext typeContext = parameter.type();

			if (typeContext != null)
			{
				Type type = this.getType(typeContext);

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
				new Error(context, "Expected type before argument identifier.");

			this.errors.add(error);
		}
	}
}
