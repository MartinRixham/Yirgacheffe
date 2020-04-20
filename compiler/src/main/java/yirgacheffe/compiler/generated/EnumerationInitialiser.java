package yirgacheffe.compiler.generated;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.type.Type;

public class EnumerationInitialiser
{
	private Type owner;

	public EnumerationInitialiser(Type owner)
	{
		this.owner = owner;
	}

	public MethodNode generate()
	{
		MethodNode method =
			new MethodNode(
				Opcodes.ACC_STATIC,
				"<clinit>",
				"()V",
				null,
				null);

		if (this.hasDefault())
		{
			method.visitTypeInsn(Opcodes.NEW, "yirgacheffe/lang/DefaultingHashMap");
			method.visitInsn(Opcodes.DUP);

			method.visitTypeInsn(Opcodes.NEW, this.owner.toFullyQualifiedType());
			method.visitInsn(Opcodes.DUP);

			method.visitMethodInsn(
				Opcodes.INVOKESPECIAL,
				this.owner.toFullyQualifiedType(),
				"<init>",
				"()V",
				false);

			method.visitMethodInsn(
				Opcodes.INVOKESPECIAL,
				"yirgacheffe/lang/DefaultingHashMap",
				"<init>",
				"(Ljava/lang/Object;)V",
				false);
		}
		else
		{
			method.visitTypeInsn(Opcodes.NEW, "java/util/HashMap");
			method.visitInsn(Opcodes.DUP);

			method.visitMethodInsn(
				Opcodes.INVOKESPECIAL,
				"java/util/HashMap",
				"<init>",
				"()V",
				false);
		}

		method.visitFieldInsn(
			Opcodes.PUTSTATIC,
			owner.toFullyQualifiedType(),
			"values",
			"Ljava/util/Map;");

		return method;
	}

	private boolean hasDefault()
	{
		return this.owner.reflect().hasDefaultConstructor();
	}
}
