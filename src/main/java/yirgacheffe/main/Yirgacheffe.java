package yirgacheffe.main;

import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.CompilationResult;

public abstract class Yirgacheffe
{
	public static void main(String[] args) throws Exception
	{
		CompilationResult result = new Compiler(args[0]).compile();

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
