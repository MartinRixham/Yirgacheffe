package yirgacheffe.compiler.generated;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.type.Type;

import java.util.Set;

public class DefaultConstructor
{
	private String className;

	private Type owner;

	public DefaultConstructor(String className, Type owner)
	{
		this.className = className;
		this.owner = owner;
	}

	public MethodNode generate()
	{
		MethodNode methodNode =
			new MethodNode(
				Opcodes.ACC_PUBLIC,
				"<init>",
				"()V",
				null,
				null);

		methodNode.visitVarInsn(Opcodes.ALOAD, 0);

		methodNode.visitMethodInsn(
			Opcodes.INVOKESPECIAL,
			"java/lang/Object",
			"<init>",
			"()V",
			false);

		Set<Function> methods = owner.reflect().getMethods();

		for (Function method: methods)
		{
			if (method.getName().startsWith("0init_field"))
			{
				methodNode.visitVarInsn(Opcodes.ALOAD, 0);

				methodNode.visitMethodInsn(
					Opcodes.INVOKEVIRTUAL,
					this.className,
					method.getName(),
					"()V",
					false);
			}
		}

		methodNode.visitInsn(Opcodes.RETURN);

		return methodNode;
	}
}
