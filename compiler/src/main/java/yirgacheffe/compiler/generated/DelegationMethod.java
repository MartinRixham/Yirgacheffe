package yirgacheffe.compiler.generated;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;
import yirgacheffe.lang.Bootstrap;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class DelegationMethod
{
	private String owner;

	private Function method;

	public DelegationMethod(String owner, Function method)
	{
		this.owner = owner;
		this.method = method;
	}

	public MethodNode generate()
	{
		MethodNode methodNode =
			new MethodNode(
				Opcodes.ACC_PUBLIC,
				this.method.getName(),
				this.method.getSignature().getDescriptor(),
				this.method.getSignature().getSignature(),
				null);

		methodNode.visitVarInsn(Opcodes.ALOAD, 0);

		methodNode.visitFieldInsn(
			Opcodes.GETFIELD,
			this.owner,
			"0delegate",
			"Ljava/lang/Object;");

		methodNode.visitTypeInsn(
			Opcodes.CHECKCAST,
			this.method.getOwner().toFullyQualifiedType());

		Array<Type> parameters = this.method.getParameterTypes();

		for (int i = 0; i < parameters.length(); i++)
		{
			Type parameter = parameters.get(i);

			methodNode.visitVarInsn(parameter.getLoadInstruction(), i + 1);
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
			"(" + this.method.getOwner().toJVMType() +
				this.method.getSignature().getDescriptor().substring(1);

		methodNode.visitInvokeDynamicInsn(
			this.method.getName(),
			descriptor,
			bootstrapMethod);

		methodNode.visitInsn(this.method.getReturnType().getReturnInstruction());

		return methodNode;
	}
}
