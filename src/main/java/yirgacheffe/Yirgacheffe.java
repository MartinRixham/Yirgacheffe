package yirgacheffe;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public final class Yirgacheffe
{
	private String[] sourceTokens;

	public Yirgacheffe(String source)
	{
		this.sourceTokens = source.split("\\s+");
	}

	public byte[] compile()
	{
		ClassWriter writer = new ClassWriter(0);

		int access =
			this.sourceTokens[0].equals("class") ?
				Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER :
				Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE;

		String className = this.sourceTokens[1];

		writer.visit(
			Opcodes.V1_5,
			access,
			className,
			null,
			"java/lang/Object",
			null);

		if (sourceTokens.length > 4)
		{
			new Block("").compile(writer);
		}

		return writer.toByteArray();
	}

	public static void main(String[] args) throws Exception
	{
		byte[] bytecode = new Yirgacheffe(args[0]).compile();

		System.out.write(bytecode);
	}
}
