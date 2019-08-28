package yirgacheffe.compiler.listener;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
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
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.lang.Array;
import yirgacheffe.parser.YirgacheffeParser;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MethodListener extends TypeListener
{
	protected boolean inConstructor = false;

	protected Type returnType = new NullType();

	protected Map<String, Object> constants = new HashMap<>();

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

		Result result = new Result()
			.add(new VarInsnNode(Opcodes.ALOAD, 0));

		Array<Type> parameters = from.getParameters();

		for (int i = 0; i < parameters.length(); i++)
		{
			Type fromType = from.getParameters().get(i);
			Type toType = to.getParameters().get(i);

			result = result
				.add(new VarInsnNode(fromType.getLoadInstruction(), i + 1))
				.concat(fromType.convertTo(toType));
		}

		Type toType = to.getReturnType();
		Type fromType = from.getReturnType();

		result = result
			.add(new MethodInsnNode(
				Opcodes.INVOKEVIRTUAL,
				this.className,
				to.getName(),
				to.getDescriptor(),
				false))
			.concat(toType.convertTo(fromType))
			.add(new InsnNode(fromType.getReturnInstruction()));

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

		if (this.inConstructor)
		{
			this.checkFieldInitialisation(context, block.getFieldAssignments());

			try
			{
				Type thisType = this.classes.loadClass(this.className.replace("/", "."));

				this.checkDelegatedInterfaces(
					block.getDelegatedInterfaces(variables.getDelegateTypes(), thisType));
			}
			catch (ClassNotFoundException | NoClassDefFoundError e)
			{
			}
		}

		this.errors.push(variables.getErrors());
		this.errors.push(result.getErrors());

		this.inConstructor = false;
	}

	private void checkFieldInitialisation(
		YirgacheffeParser.FunctionBlockContext context,
		Array<String> fieldAssignments)
	{
		try
		{
			String initialiserPrefix = "0init_field";
			Type thisType = this.classes.loadClass(this.className.replace("/", "."));
			Class<?> reflectionClass = thisType.reflectionClass();

			Method[] methods = reflectionClass.getDeclaredMethods();

			Set<String> fieldNames =
				this.getFieldNames(reflectionClass.getDeclaredFields());

			for (Method method: methods)
			{
				if (method.getName().startsWith(initialiserPrefix))
				{
					fieldNames.remove(
						method.getName().substring(initialiserPrefix.length() + 1));
				}
			}

			for (String field: fieldAssignments)
			{
				if (field.equals("this"))
				{
					fieldNames = new HashSet<>();
				}
				else
				{
					fieldNames.remove(field);
				}
			}

			for (String field: fieldNames)
			{
				String message =
					"Constructor " + this.signature +
						" does not initialise field '" + field + "'.";

				this.errors.push(new Error(context, message));
			}
		}
		catch (ClassNotFoundException | NoClassDefFoundError e)
		{
		}
	}

	private void checkDelegatedInterfaces(Array<Type> delegatedInterfaces)
	{
		if (this.delegatedInterfaces == null)
		{
			this.delegatedInterfaces = delegatedInterfaces;
		}
		else
		{
			Array<Type> intersection = new Array<>();

			for (Type interfaceType: this.delegatedInterfaces)
			{
				if (delegatedInterfaces.contains(interfaceType))
				{
					intersection.push(interfaceType);
				}
			}

			this.delegatedInterfaces = intersection;
		}
	}

	private Set<String> getFieldNames(Field[] fields)
	{
		Set<String> names = new HashSet<>();

		for (Field field: fields)
		{
			names.add(field.getName());
		}

		return names;
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
