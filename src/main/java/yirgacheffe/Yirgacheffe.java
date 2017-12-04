package yirgacheffe;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public final class Yirgacheffe
{
	private String[] sourceTokens;

	public Yirgacheffe(String source)
	{
		this.sourceTokens = source.split(" ");
	}

	public byte[] compile()
	{
		ClassWriter cw = new ClassWriter(0);

		cw.visit(
			Opcodes.V1_5,
			Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE,
			this.sourceTokens[1],
			null,
			"java/lang/Object",
			new String[0]);

		return cw.toByteArray();
	}

	public static void main(String[] args) throws Exception
	{
		byte[] bytecode = new Yirgacheffe(args[0]).compile();

		System.out.write(bytecode);
	}
}
