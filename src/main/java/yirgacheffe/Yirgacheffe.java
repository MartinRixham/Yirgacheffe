package yirgacheffe;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;

public final class Yirgacheffe
{
	private String[] sourceTokens;

	private static final int LENGTH_OF_NON_EMPTY_BLOCK = 3;

	public Yirgacheffe(String source)
	{
		this.sourceTokens = source.split("\\s+");
	}

	public CompilationResult compile()
	{
		ClassWriter writer = new ClassWriter(0);

		int access;
		String classType = this.sourceTokens[0];

		if (classType.equals("class"))
		{
			access = Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER;
		}
		else if (classType.equals("interface"))
		{
			access = Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE;
		}
		else
		{
			String error = "Declaration should be of class or interface.";

			return new CompilationResult(new String[] {error});
		}

		String className = this.sourceTokens[1];

		writer.visit(
			Opcodes.V1_5,
			access,
			className,
			null,
			"java/lang/Object",
			null);

		if (this.sourceTokens.length > LENGTH_OF_NON_EMPTY_BLOCK)
		{
			new Block(this.getBlockSource()).compile(writer);
		}

		return new CompilationResult(writer.toByteArray());
	}

	private String getBlockSource()
	{
		List<String> source = new ArrayList<>();
		boolean flag = false;

		for (String token: this.sourceTokens)
		{
			if (token.equals("}"))
			{
				flag = false;
			}

			if (flag)
			{
				source.add(token);
			}

			if (token.equals("{"))
			{
				flag = true;
			}
		}

		return String.join(" ", source);
	}

	public static void main(String[] args) throws Exception
	{
		CompilationResult result = new Yirgacheffe(args[0]).compile();

		if (result.isSuccessful())
		{
			System.out.write(result.getBytecode());
		}
		else
		{
			System.err.println(result.getErrors());
		}
	}
}
