package yirgacheffe.compiler;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Yirgacheffe
{
	public static void main(String[] args) throws Exception
	{
		for (int i = 0; i < args.length; i++)
		{
			new Yirgacheffe(args);
		}
	}

	private Yirgacheffe(String[] sourceFiles) throws Exception
	{
		List<Compiler> compilers = new ArrayList<>();

		for (String sourceFile : sourceFiles)
		{
			byte[] encoded = Files.readAllBytes(Paths.get(sourceFile));
			String source = new String(encoded, "UTF-8");
			String directory = this.getDirectory(sourceFile);

			compilers.add(new Compiler(directory, source));
		}

		Map<String, Type> importedTypes = new HashMap<>();
		boolean failed = false;

		for (Compiler compiler : compilers)
		{
			CompilationResult result =
				compiler.compileClassDeclaration(importedTypes);

			if (!result.isSuccessful())
			{
				System.err.print(result.getErrors());
				failed = true;
			}
		}

		if (failed)
		{
			return;
		}

		for (Compiler compiler : compilers)
		{
			CompilationResult result = compiler.compile(importedTypes);

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
