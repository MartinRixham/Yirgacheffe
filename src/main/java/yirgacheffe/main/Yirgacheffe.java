package yirgacheffe.main;

import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.CompilationResult;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public final class Yirgacheffe
{
	public static void main(String[] args) throws Exception
	{
		new Yirgacheffe(args[0]);
	}

	private Yirgacheffe(String sourceFile) throws Exception
	{
		try (InputStream inputStream = new FileInputStream(sourceFile))
		{
			String directory = this.getDirectory(sourceFile);

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

	private String getDirectory(String filePath)
	{
		String[] files = filePath.split("/");
		StringBuilder directory = new StringBuilder();

		for (int i = 0; i < files.length - 1; i++)
		{
			directory.append(files[i]).append("/");
		}

		return directory.toString();
	}
}
