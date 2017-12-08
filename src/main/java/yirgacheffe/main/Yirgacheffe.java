package yirgacheffe.main;

import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.CompilationResult;

import java.io.FileInputStream;
import java.io.InputStream;

public abstract class Yirgacheffe
{
	public static void main(String[] args) throws Exception
	{
		try (InputStream inputStream = new FileInputStream(args[0]))
		{
			CompilationResult result = new Compiler(inputStream).compile();

			if (result.isSuccessful())
			{
				System.out.write(result.getBytecode());
			}
			else
			{
				System.err.print(result.getErrors());
			}
		}
	}
}
