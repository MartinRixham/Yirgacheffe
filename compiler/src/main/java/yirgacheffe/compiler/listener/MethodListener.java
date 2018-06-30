package yirgacheffe.compiler.listener;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.expression.Variable;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class MethodListener extends TypeListener
{
	protected boolean inConstructor = false;

	protected Type returnType = new NullType();

	protected Stack<Expression> expressions = new Stack<>();

	protected Map<String, Variable> localVariables = new HashMap<>();

	protected MethodVisitor methodVisitor;

	protected boolean hasReturnStatement = false;

	protected Set<String> members = new HashSet<>();

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
		if (this.returnType != PrimitiveType.VOID && this.expressions.empty())
		{
			this.methodVisitor.visitInsn(Opcodes.DCONST_0);
		}

		this.methodVisitor.visitMaxs(0, 0);
		this.methodVisitor.visitInsn(this.returnType.getReturnInstruction());

		this.localVariables = new HashMap<>();

		if (!this.hasReturnStatement && this.returnType != PrimitiveType.VOID)
		{
			String message = "No return statement in method.";

			this.errors.add(new Error(context.stop, message));
		}

		this.hasReturnStatement = false;
		this.inConstructor = false;
	}
}
