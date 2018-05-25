package yirgacheffe.compiler;

import yirgacheffe.compiler.type.Classes;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class Yirgacheffe
{
	private String[] sourceFiles;

	public static void main(String[] args) throws Exception
	{
		new Yirgacheffe(args).execute();
	}

	private Yirgacheffe(String[] sourceFiles)
	{
		this.sourceFiles = sourceFiles;
	}

	private void execute() throws Exception
	{
		Classes classes = new Classes();
		List<Compiler> compilers = new ArrayList<>();

		for (String sourceFile : this.sourceFiles)
		{
			byte[] encoded = Files.readAllBytes(Paths.get(sourceFile));
			String source = new String(encoded, "UTF-8");
			Compiler compiler = new Compiler(sourceFile, source);

			compilers.add(compiler);
		}

		for (Compiler compiler: compilers)
		{
			compiler.compileClassDeclaration(classes);
		}

		classes.clearCache();

		for (Compiler compiler: compilers)
		{
			compiler.compileInterface(classes);
		}

		classes.clearCache();

		List<CompilationResult> results = new ArrayList<>();

		for (Compiler compiler: compilers)
		{
			results.add(compiler.compile(classes));
		}

		for (CompilationResult result: results)
		{
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
				System.err.println("Errors in file " + result.getSourceFileName() + ":");
				System.err.print(result.getErrors());
			}
		}
	}
}
