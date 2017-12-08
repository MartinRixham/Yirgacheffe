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
		String sourceFile = args[0];

		try (InputStream inputStream = new FileInputStream(sourceFile))
		{
			String directory = getDirectory(sourceFile);

			CompilationResult result = new Compiler(directory, inputStream).compile();

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

	private static String getDirectory(String filePath)
	{
		String[] files = filePath.split("/");
		String directory = "";

		for (int i = 0; i < files.length - 1; i++)
		{
			directory += files[i] + "/";
		}

		return directory;
	}
}
