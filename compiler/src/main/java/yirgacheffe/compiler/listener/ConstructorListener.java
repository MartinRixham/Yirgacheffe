package yirgacheffe.compiler.listener;

import org.antlr.v4.runtime.Token;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;
import yirgacheffe.lang.Bootstrap;
import yirgacheffe.parser.YirgacheffeParser;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.UUID;

public class ConstructorListener extends MainMethodListener
{
	public ConstructorListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void enterConstructorDeclaration(
		YirgacheffeParser.ConstructorDeclarationContext context)
	{
		this.returnType = PrimitiveType.VOID;
	}

	@Override
	public void exitConstructorDeclaration(
		YirgacheffeParser.ConstructorDeclarationContext context)
	{
		this.hasConstructor = true;
		this.inConstructor = true;

		YirgacheffeParser.SignatureContext signature = context.signature();
		String className = new Array<>(this.className.split("/")).pop();

		boolean isValid = true;

		if (!signature.Identifier().getText().equals(className))
		{
			String message =
				"Constructor of incorrect type " + signature.Identifier().getText() +
					": expected " + className + ".";

			Token token = signature.Identifier().getSymbol();

			this.errors.push(new Error(token, message));

			isValid = false;
		}

		boolean isPrivate = false;

		if (!this.inEnumeration && context.modifier() == null)
		{
			String message =
				"Expected public or private access modifier " +
					"at start of constructor declaration.";

			this.errors.push(new Error(context, message));
		}

		if (this.inEnumeration &&
			context.modifier() != null &&
			context.modifier().Public() != null)
		{
			String message =
				"Enumeration constructor cannot be public.";

			this.errors.push(new Error(context, message));
		}

		if (context.modifier() != null)
		{
			isPrivate = context.modifier().Private() != null;
		}

		this.methodNode =
			new MethodNode(
				isPrivate ? Opcodes.ACC_PRIVATE : Opcodes.ACC_PUBLIC,
				isValid ? "<init>" : UUID.randomUUID().toString(),
				this.signature.getDescriptor(),
				this.signature.getSignature(),
				null);

		this.classNode.methods.add(this.methodNode);

		if (!isValid)
		{
			return;
		}

		InsnList instructions = new InsnList();

		instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

		instructions.add(new MethodInsnNode(
			Opcodes.INVOKESPECIAL,
			"java/lang/Object",
			"<init>",
			"()V",
			false));

		instructions.add(this.createInitialiserCalls(context));
		instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

		for (int i = 0; i < this.signature.getParameters().length(); i++)
		{
			Type parameter = this.signature.getParameters().get(i);

			instructions.add(new VarInsnNode(parameter.getLoadInstruction(), i + 1));
		}

		MethodType methodType =
			MethodType.methodType(
				CallSite.class,
				MethodHandles.Lookup.class,
				String.class,
				MethodType.class);

		Handle bootstrapMethod =
			new Handle(
				Opcodes.H_INVOKESTATIC,
				Bootstrap.class.getName().replace(".", "/"),
				"bootstrapPrivate",
				methodType.toMethodDescriptorString(),
				false);

		String descriptor =
			"(L" + this.className + ";" +
			this.signature.getDescriptor().substring(1);

		instructions.add(new InvokeDynamicInsnNode("0this", descriptor, bootstrapMethod));
		instructions.add(new InsnNode(Opcodes.RETURN));

		methodNode.instructions.add(instructions);

		this.methodNode =
			new MethodNode(
				Opcodes.ACC_PRIVATE,
				"0this",
				this.signature.getDescriptor(),
				this.signature.getSignature(),
				null);

		this.classNode.methods.add(this.methodNode);

		if (signature.parameter().size() == 0)
		{
			this.hasDefaultConstructor = true;
		}
	}

	private InsnList createInitialiserCalls(
		YirgacheffeParser.ConstructorDeclarationContext context)
	{
		InsnList instructions = new InsnList();

		String initialiserPrefix = "0init_field";
		Class<?> reflectionClass = this.thisType.reflectionClass();

		Method[] methods = reflectionClass.getDeclaredMethods();

		for (Method method: methods)
		{
			if (method.getName().startsWith(initialiserPrefix))
			{
				instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

				instructions.add(new MethodInsnNode(
					Opcodes.INVOKEVIRTUAL,
					this.className,
					method.getName(),
					"()V",
					false));
			}
		}

		return instructions;
	}
}
