package yirgacheffe.main;

import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.CompilationResult;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class Yirgacheffe
{
	public static void main(String[] args) throws Exception
	{
		try (InputStream inputStream = new FileInputStream(args[0]))
		{
			CompilationResult result = new Compiler(inputStream).compile();

			if (result.isSuccessful())
			{
				try (OutputStream outputStream =
					new FileOutputStream(result.getClassFileName()))
				{
					outputStream.write(result.getBytecode());
				}
			}
			else
			{
				System.err.print(result.getErrors());
			}
		}
	}
}
