package yirgacheffe.compiler.listener;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.assignment.FieldAssignment;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.function.FunctionSignature;
import yirgacheffe.compiler.member.Interface;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.implementation.InterfaceImplementation;
import yirgacheffe.compiler.implementation.NullImplementation;
import yirgacheffe.compiler.member.Property;
import yirgacheffe.compiler.statement.Block;
import yirgacheffe.compiler.statement.LabelStatement;
import yirgacheffe.compiler.statement.ParameterDeclaration;
import yirgacheffe.compiler.statement.Statement;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.lang.Array;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MethodListener extends TypeListener
{
	protected boolean inConstructor = false;

	protected boolean inMethod = false;

	protected Type returnType = new NullType();

	protected Map<String, Object> constants = new HashMap<>();

	protected Array<Expression> expressions = new Array<>();

	protected Array<Statement> statements = new Array<>();

	protected MethodNode methodNode;

	private Map<Signature, Signature> methods = new HashMap<>();

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
		this.inMethod = true;

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

		if (this.inInterface)
		{
			String message = "Method body not permitted for interface method.";

			this.errors.push(new Error(context, message));
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
			Function interfaceMethod = this.interfaceMethods.get(i);
			Signature signature = interfaceMethod.getSignature();

			if (signature.isImplementedBy(this.signature) &&
				this.returnType.isAssignableTo(interfaceMethod.getReturnType()))
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
		Coordinate coordinate = new Coordinate(context);
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

		this.statements.push(new ParameterDeclaration(coordinate, name, type));
	}

	@Override
	public void exitFunctionBlock(YirgacheffeParser.FunctionBlockContext context)
	{
		this.statements.unshift(new LabelStatement(this.signature.getLabel()));

		Coordinate coordinate = new Coordinate(context.stop.getLine(), 0);
		Block block = new Block(coordinate, this.statements);
		int initialIndex = this.inInterface ? 0 : 1;
		LocalVariables variables = new LocalVariables(initialIndex, this.constants);
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

			if (this.inInterface || !this.inConstructor)
			{
				String message = "Missing return statement.";

				this.errors.push(new Error(coordinate, message));
			}
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

			this.checkDelegatedInterfaces(
				block.getDelegatedInterfaces(
					variables.getDelegateTypes(),
					this.thisType));
		}

		this.errors.push(variables.getErrors());
		this.errors.push(result.getErrors());

		this.inConstructor = false;
		this.inMethod = false;
	}

	private void checkFieldInitialisation(
		YirgacheffeParser.FunctionBlockContext context,
		FieldAssignment fieldAssignments)
	{
		String initialiserPrefix = "0init_field";
		Interface members = thisType.reflect();
		Set<Function> methods = members.getMethods();
		Set<String> fieldNames = this.getFieldNames(members.getFields());

		for (Function method: methods)
		{
			if (method.getName().startsWith(initialiserPrefix))
			{
				fieldNames.remove(
					method.getName().substring(initialiserPrefix.length() + 1));
			}
		}

		for (String field: fieldNames.toArray(new String[0]))
		{
			if (fieldAssignments.contains(field))
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

	private void checkDelegatedInterfaces(Implementation delegatedInterfaces)
	{
		if (delegatedInterfaces instanceof NullImplementation)
		{
			delegatedInterfaces = new InterfaceImplementation(new Array<>());
		}

		this.delegatedInterfaces =
			this.delegatedInterfaces.intersect(delegatedInterfaces);
	}

	private Set<String> getFieldNames(Set<Property> fields)
	{
		Set<String> names = new HashSet<>();

		for (Property field: fields)
		{
			if (!field.isStatic())
			{
				names.add(field.getName());
			}
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
		Signature signature =
			new FunctionSignature(
				this.returnType,
				context.Identifier().getSymbol().getText(),
				this.parameters);

		if (this.methods.containsKey(signature))
		{
			String message;

			if (signature.getParameters().equals(
				this.methods.get(signature).getParameters()))
			{
				message =
					"Duplicate declaration of method " + signature.toString() + ".";
			}
			else
			{
				message =
					"Methods " + this.methods.get(signature) + " and " +
						signature + " have the same erasure.";
			}

			this.errors.push(new Error(context, message));
			this.isValid = false;
		}
		else
		{
			this.methods.put(signature, signature);
		}

		this.parameters = new Array<>();
		this.signature = signature;
	}
}
