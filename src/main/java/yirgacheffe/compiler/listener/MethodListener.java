package yirgacheffe.compiler.listener;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.TypeStack;
import yirgacheffe.compiler.type.Variable;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodListener extends TypeListener
{
	protected TypeStack typeStack = new TypeStack();

	protected Map<String, Variable> localVariables = new HashMap<>();

	protected MethodVisitor methodVisitor;

	public MethodListener(
		String sourceFile,
		Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void exitInterfaceMethodDeclaration(
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
	public void exitClassMethodDeclaration(
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

		String name = null;

		if (context.Identifier() != null)
		{
			name = context.Identifier().getText();
		}

		String descriptor =
			this.getMethodDescriptor(context.parameter(), context.type());

		this.methodVisitor =
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

	protected String getParameterDescriptor(
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
		Type type = new NullType();

		if (context.type() == null)
		{
			Error error =
				new Error(context, "Expected type before parameter identifier.");

			this.errors.add(error);
		}
		else
		{
			type = this.types.getType(context.type());
		}

		Variable variable =
			new Variable(this.localVariables.size(), type);

		this.localVariables.put(context.Identifier().getText(), variable);
	}

	@Override
	public void exitMethod(
		YirgacheffeParser.MethodContext context)
	{
		this.methodVisitor.visitInsn(Opcodes.RETURN);

		int maxSize = this.typeStack.reset();
		int localVariablesSize = 1;

		for (Variable variable: this.localVariables.values())
		{
			localVariablesSize += variable.getType().width();
		}

		this.methodVisitor.visitMaxs(maxSize, localVariablesSize);

		this.localVariables = new HashMap<>();
	}
}
