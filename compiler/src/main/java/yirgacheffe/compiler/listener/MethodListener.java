package yirgacheffe.compiler.listener;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.statement.Block;
import yirgacheffe.compiler.statement.LabelStatement;
import yirgacheffe.compiler.statement.ParameterDeclaration;
import yirgacheffe.compiler.statement.Statement;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.Variables;
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

	private Set<Signature> methods = new HashSet<>();

	private Array<Type> parameters = new Array<>();

	protected Signature signature;

	public MethodListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void exitInterfaceMethodDeclaration(
		YirgacheffeParser.InterfaceMethodDeclarationContext context)
	{
		if (context.modifier() != null)
		{
			String message =
				"Access modifier is not required for interface method declaration.";

			this.errors.push(new Error(context, message));
		}

		this.writer.visitMethod(
			Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT,
			context.signature().Identifier().getText(),
			this.signature.getDescriptor(),
			null,
			null);
	}

	@Override
	public void exitReturnType(YirgacheffeParser.ReturnTypeContext context)
	{
		this.returnType = this.types.getType(context.type());
	}

	@Override
	public void exitClassMethodDeclaration(
		YirgacheffeParser.ClassMethodDeclarationContext context)
	{
		boolean isPrivate = false;
		String name = null;

		if (context.modifier() == null)
		{
			String message =
				"Expected public or private access modifier " +
				"at start of method declaration.";

			this.errors.push(new Error(context, message));
		}
		else
		{
			isPrivate = context.modifier().Private() != null;
		}

		if (context.signature().Identifier() != null)
		{
			name = context.signature().Identifier().getText();
		}

		this.methodVisitor =
			this.writer.visitMethod(
				isPrivate ? Opcodes.ACC_PROTECTED : Opcodes.ACC_PUBLIC,
				name,
				this.signature.getDescriptor(),
				this.signature.getSignature(),
				null);
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

		this.parameters.push(type);

		String name = context.Identifier().getText();

		this.statements.push(new ParameterDeclaration(name, type));
	}

	@Override
	public void exitFunctionBlock(YirgacheffeParser.FunctionBlockContext context)
	{
		this.statements.unshift(new LabelStatement(this.signature.getLabel()));

		Coordinate coordinate = new Coordinate(context.stop.getLine(), 0);
		Block block = new Block(coordinate, this.statements);
		Variables variables = new Variables();
		boolean returns = block.returns();

		Array<Error> errors =
			block.compile(this.methodVisitor, variables, this.signature);

		if (this.returnType != PrimitiveType.VOID && block.isEmpty())
		{
			this.methodVisitor.visitInsn(this.returnType.getZero());
			this.methodVisitor.visitInsn(this.returnType.getReturnInstruction());
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
		this.errors.push(errors);

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
		this.signature =
			new Signature(
				this.returnType,
				context.Identifier().getSymbol().getText(),
				this.parameters);

		if (this.methods.contains(this.signature))
		{
			String message =
				"Duplicate declaration of method " + this.signature.toString() + ".";

			this.errors.push(new Error(context, message));
		}
		else
		{
			this.methods.add(this.signature);
		}

		this.parameters = new Array<>();
	}
}
