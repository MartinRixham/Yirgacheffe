package yirgacheffe.compiler.listener;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.statement.Block;
import yirgacheffe.compiler.statement.Statement;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.expression.Variable;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.HashSet;

import java.util.Set;

public class MethodListener extends TypeListener
{
	protected boolean inConstructor = false;

	protected Type returnType = new NullType();

	protected Array<Expression> expressions = new Array<>();

	protected Array<Statement> statements = new Array<>();

	protected Block currentBlock = new Block();

	protected MethodVisitor methodVisitor;

	protected boolean hasReturnStatement = false;

	private Set<String> methods = new HashSet<>();

	protected String descriptor;

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

			this.errors.push(new Error(context, message));
		}

		this.returnType = this.types.getType(context.type());
		String descriptor = this.descriptor + this.returnType.toJVMType();

		this.writer.visitMethod(
			Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT,
			context.signature().Identifier().getText(),
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

			this.errors.push(new Error(context, message));
		}
		else
		{
			isPrivate = context.Modifier().getText().equals("private");
		}

		String name = null;

		if (context.signature().Identifier() != null)
		{
			name = context.signature().Identifier().getText();
		}

		this.returnType = this.types.getType(context.type());
		String descriptor = this.descriptor + this.returnType.toJVMType();

		this.methodVisitor =
			this.writer.visitMethod(
				isPrivate ? Opcodes.ACC_PRIVATE : Opcodes.ACC_PUBLIC,
				name,
				descriptor,
				null,
				null);
	}

	@Override
	public void exitParameter(YirgacheffeParser.ParameterContext context)
	{
		Type type = new NullType();

		if (context.type() == null)
		{
			Error error =
				new Error(context, "Expected type before parameter identifier.");

			this.errors.push(error);
		}
		else
		{
			type = this.types.getType(context.type());
		}

		Variable variable =
			new Variable(this.currentBlock.size() + 1, type);

		this.currentBlock.declare(context.Identifier().getText(), variable);
	}

	@Override
	public void exitFunction(YirgacheffeParser.FunctionContext context)
	{
		for (Statement statement: this.statements)
		{
			statement.compile(this.methodVisitor);
		}

		if (this.returnType != PrimitiveType.VOID && this.statements.length() == 0)
		{
			this.methodVisitor.visitInsn(Opcodes.DCONST_0);
		}

		this.methodVisitor.visitMaxs(0, 0);
		this.methodVisitor.visitInsn(this.returnType.getReturnInstruction());

		if (!this.hasReturnStatement && this.returnType != PrimitiveType.VOID)
		{
			String message = "No return statement in method.";

			this.errors.push(new Error(context.stop, message));
		}

		this.currentBlock = new Block();
		this.hasReturnStatement = false;
		this.inConstructor = false;
	}

	@Override
	public void exitSignature(YirgacheffeParser.SignatureContext context)
	{
		Array<String> parameters = new Array<>();
		StringBuilder descriptor = new StringBuilder("(");

		for (YirgacheffeParser.ParameterContext parameter : context.parameter())
		{
			Type type = this.types.getType(parameter.type());

			descriptor.append(type.toJVMType());
			parameters.push(type.toString());
		}

		descriptor.append(")");

		this.descriptor = descriptor.toString();

		String signature = context.Identifier() + this.descriptor;

		if (this.methods.contains(signature))
		{
			String message =
				"Duplicate declaration of method " +
				context.Identifier() + "(" +
				String.join(",", parameters) + ").";

			this.errors.push(new Error(context, message));
		}
		else
		{
			this.methods.add(signature);
		}

		this.statements = new Array<>();
	}
}
