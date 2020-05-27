package yirgacheffe.lang;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public final class Agent
{
	private Agent()
	{
	}

	public static void premain(String agentArgs, Instrumentation inst)
	{
		inst.addTransformer(new ClassFileTransformer()
		{
			@Override
			public byte[] transform(
				ClassLoader classLoader,
				String name,
				Class<?> clazz,
				ProtectionDomain domain,
				byte[] bytecode)
			{
				if (clazz.getTypeParameters().length == 0)
				{
					return bytecode;
				}

				ClassReader reader = new ClassReader(bytecode);
				ClassNode classNode = new ClassNode();

				reader.accept(classNode, 0);

				classNode.fields.add(
					new FieldNode(
						Opcodes.ACC_PROTECTED,
						"0signature",
						"Ljava/lang/String;",
						null,
						null));

				ClassWriter writer =
					new ClassWriter(ClassWriter.COMPUTE_FRAMES);

				classNode.accept(writer);

				return writer.toByteArray();
			}
		});
	}
}
