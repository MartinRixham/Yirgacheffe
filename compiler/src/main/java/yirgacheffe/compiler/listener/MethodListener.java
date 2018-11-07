package yirgacheffe.compiler.listener;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.statement.Block;
import yirgacheffe.compiler.statement.Statement;
import yirgacheffe.compiler.statement.StatementResult;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.compiler.statement.VariableDeclaration;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
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

	protected MethodVisitor methodVisitor;

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
			descriptor, null, null);
	}

	@Override
	public void exitClassMethodDeclaration(
		YirgacheffeParser.ClassMethodDeclarationContext context)
	{
		boolean isPrivate = false;
		String name = null;

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

		if (context.signature().Identifier() != null)
		{
			name = context.signature().Identifier().getText();
		}

		this.returnType = this.types.getType(context.type());
		String descriptor = this.descriptor + this.returnType.toJVMType();
		this.methodVisitor = this.writer.visitMethod(
			isPrivate ? Opcodes.ACC_PRIVATE : Opcodes.ACC_PUBLIC,
			name, descriptor, null, null);
	}

	@Override
	public void exitParameter(YirgacheffeParser.ParameterContext context)
	{
		Type type = new NullType();

		if (context.type() == null)
		{
			String message = "Expected type before parameter identifier.";
			Error error = new Error(context, message);

			this.errors.push(error);
		}
		else
		{
			type = this.types.getType(context.type());
		}

		String name = context.Identifier().getText();

		this.statements.push(new VariableDeclaration(name, type));
	}

	@Override
	public void exitFunction(YirgacheffeParser.FunctionContext context)
	{
		Coordinate coordinate = new Coordinate(context.stop.getLine(), 0);
		Block block = new Block(coordinate, this.statements);
		Variables variables = new Variables();
		StatementResult result = block.compile(this.methodVisitor, variables);
		boolean returns = block.returns();

		if (this.returnType != PrimitiveType.VOID && this.statements.length() == 0)
		{
			if (this.returnType.isAssignableTo(PrimitiveType.DOUBLE))
			{
				this.methodVisitor.visitInsn(Opcodes.DCONST_0);
				this.methodVisitor.visitInsn(Opcodes.DRETURN);
			}
			else
			{
				this.methodVisitor.visitInsn(Opcodes.ICONST_0);
				this.methodVisitor.visitInsn(Opcodes.IRETURN);
			}

		}
		else if (!returns && this.returnType == PrimitiveType.VOID)
		{
			this.methodVisitor.visitInsn(Opcodes.RETURN);
		}
		else if (!returns)
		{
			String message = "Missing return statement.";

			this.errors.push(new Error(coordinate, message));
		}

		this.errors.push(variables.getErrors());
		this.errors.push(result.getErrors());

		this.methodVisitor.visitMaxs(0, 0);
		this.inConstructor = false;
	}

	@Override
	public void enterSignature(YirgacheffeParser.SignatureContext context)
	{
		this.statements = new Array<>();
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
	}
}
