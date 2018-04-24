package yirgacheffe.compiler.listener;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
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
	protected Type returnType = new NullType();

	protected TypeStack typeStack = new TypeStack();

	protected Map<String, Variable> localVariables = new HashMap<>();

	protected MethodVisitor methodVisitor;

	public MethodListener(String sourceFile, Classes classes)
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

		this.returnType = this.types.getType(context.type());

		String descriptor =
			this.getDescriptor(context.parameter()) +
				this.returnType.toJVMType();

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

		this.returnType = this.types.getType(context.type());

		String descriptor =
			this.getDescriptor(context.parameter()) +
				this.returnType.toJVMType();

		this.methodVisitor =
			this.writer.visitMethod(
				isPrivate ? Opcodes.ACC_PRIVATE : Opcodes.ACC_PUBLIC,
				name,
				descriptor,
				null,
				null);
	}

	protected String getDescriptor(List<YirgacheffeParser.ParameterContext> parameters)
	{
		StringBuilder descriptor = new StringBuilder("(");

		for (YirgacheffeParser.ParameterContext parameter : parameters)
		{
			Type type = this.types.getType(parameter.type());

			descriptor.append(type.toJVMType());
		}

		descriptor.append(")");

		return descriptor.toString();
	}

	@Override
	public void exitParameter(YirgacheffeParser.ParameterContext context)
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
			new Variable(this.localVariables.size() + 1, type);

		this.localVariables.put(context.Identifier().getText(), variable);
	}

	@Override
	public void exitFunction(YirgacheffeParser.FunctionContext context)
	{
		int maxSize = this.typeStack.reset();
		int localVariablesSize = 1;

		for (Variable variable: this.localVariables.values())
		{
			localVariablesSize += variable.getType().width();
		}

		if (this.returnType != PrimitiveType.VOID && maxSize == 0)
		{
			this.methodVisitor.visitInsn(Opcodes.DCONST_0);
			this.methodVisitor.visitMaxs(this.returnType.width(), localVariablesSize);
		}
		else
		{
			this.methodVisitor.visitMaxs(maxSize, localVariablesSize);
		}

		this.methodVisitor.visitInsn(this.returnType.getReturnInstruction());

		this.localVariables = new HashMap<>();
	}
}
