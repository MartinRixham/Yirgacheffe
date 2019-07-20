package yirgacheffe.compiler.listener;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.statement.Block;
import yirgacheffe.compiler.statement.LabelStatement;
import yirgacheffe.compiler.statement.ParameterDeclaration;
import yirgacheffe.compiler.statement.Statement;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.GenericType;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.lang.Array;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.HashSet;

import java.util.Set;
import java.util.UUID;

public class MethodListener extends FieldDeclarationListener
{
	protected boolean inConstructor = false;

	protected Type returnType = new NullType();

	protected Array<Expression> expressions = new Array<>();

	protected Array<Statement> statements = new Array<>();

	protected MethodNode methodNode;

	private Set<Signature> methods = new HashSet<>();

	private Array<Type> parameters = new Array<>();

	protected Signature signature;

	private boolean isValid = true;

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

		String name = context.signature().Identifier().getText();

		this.classNode.methods.add(
			new MethodNode(
				Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT,
				this.isValid ? name : UUID.randomUUID().toString(),
				this.signature.getDescriptor(),
				this.signature.getSignature(),
				null));
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

		boolean makePrivate = isPrivate || this.checkInterfaceImplementation();

		this.methodNode =
			new MethodNode(
				makePrivate ? Opcodes.ACC_PROTECTED : Opcodes.ACC_PUBLIC,
				this.isValid ? name : UUID.randomUUID().toString(),
				this.signature.getDescriptor(),
				this.signature.getSignature(),
				null);

		this.classNode.methods.add(this.methodNode);
	}

	private boolean checkInterfaceImplementation()
	{
		for (int i = this.interfaceMethods.length() - 1; i >= 0; i--)
		{
			Signature signature = this.interfaceMethods.get(i).getSignature();

			if (signature.isImplementedBy(this.signature))
			{
				this.interfaceMethods.splice(i, 1);

				if (!this.signature.getReturnType()
					.equals(signature.getReturnType()))
				{
					this.createBridge(signature, this.signature);

					return true;
				}
				else if (!signature.equals(this.signature))
				{
					this.createBridge(signature, this.signature);

					return false;
				}
			}
		}

		return false;
	}

	private void createBridge(Signature from, Signature to)
	{
		MethodNode methodNode =
			new MethodNode(
				Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC,
				from.getName(),
				from.getDescriptor(),
				from.getSignature(),
				null);

		this.classNode.methods.add(methodNode);

		Result result = new Result().add(new VarInsnNode(Opcodes.ALOAD, 0));

		Array<Type> parameters = from.getParameters();

		for (int i = 0; i < parameters.length(); i++)
		{
			result = result.add(
				new VarInsnNode(parameters.get(i).getLoadInstruction(), i + 1));

			if (parameters.get(i) instanceof GenericType &&
				!to.getParameters().get(i).equals(new ReferenceType(Object.class)))
			{
				String toType =
					to.getParameters().get(i)
						.toFullyQualifiedType();

				result = result.add(new TypeInsnNode(Opcodes.CHECKCAST, toType));
			}
		}

		String owner;

		if (this.packageName == null)
		{
			owner = this.className;
		}
		else
		{
			owner = this.packageName.replace(".", "/") + "/" + this.className;
		}

		result = result.add(new MethodInsnNode(
			Opcodes.INVOKEVIRTUAL,
			owner,
			to.getName(),
			to.getDescriptor(),
			false));

		Type toType = to.getReturnType();
		Type fromType = from.getReturnType();

		if (!toType.equals(fromType) && fromType.isPrimitive())
		{
			result = result.concat(toType.convertTo(fromType));
		}

		result = result.add(new InsnNode(fromType.getReturnInstruction()));

		for (AbstractInsnNode instruction: result.getInstructions())
		{
			methodNode.instructions.add(instruction);
		}
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
		LocalVariables variables = new LocalVariables(this.constants);
		boolean returns = block.returns();

		Result result = block.compile(variables, this.signature);

		for (AbstractInsnNode instruction: result.getInstructions())
		{
			this.methodNode.instructions.add(instruction);
		}

		for (TryCatchBlockNode tryCatchBlock: result.getTryCatchBlocks())
		{
			this.methodNode.tryCatchBlocks.add(tryCatchBlock);
		}

		if (!this.returnType.equals(PrimitiveType.VOID) && block.isEmpty())
		{
			this.methodNode.visitInsn(this.returnType.getZero());
			this.methodNode.visitInsn(this.returnType.getReturnInstruction());
		}
		else if (!returns && this.returnType.equals(PrimitiveType.VOID))
		{
			this.methodNode.visitInsn(Opcodes.RETURN);
		}
		else if (!returns)
		{
			String message = "Missing return statement.";

			this.errors.push(new Error(coordinate, message));
		}

		this.errors.push(variables.getErrors());
		this.errors.push(result.getErrors());

		this.inConstructor = false;
	}

	@Override
	public void enterSignature(YirgacheffeParser.SignatureContext context)
	{
		this.statements = new Array<>();
		this.isValid = true;
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
			this.isValid = false;
		}
		else
		{
			this.methods.add(this.signature);
		}

		this.parameters = new Array<>();
	}
}
