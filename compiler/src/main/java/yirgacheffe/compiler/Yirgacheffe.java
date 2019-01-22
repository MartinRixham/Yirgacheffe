package yirgacheffe.compiler;

import yirgacheffe.compiler.type.Classes;
import yirgacheffe.lang.Array;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class Yirgacheffe
{
	private Array<String> sourceFiles;

	public static void main(String[] args) throws IOException
	{
		new Yirgacheffe(new Array<>(args)).execute();
	}

	private Yirgacheffe(Array<String> sourceFiles)
	{
		this.sourceFiles = sourceFiles;
	}

	private void execute() throws IOException
	{
		Classes classes = new Classes();
		Array<Compiler> compilers = new Array<>();

		for (String sourceFile : this.sourceFiles)
		{
			byte[] encoded = Files.readAllBytes(Paths.get(sourceFile));
			String source = new String(encoded, StandardCharsets.UTF_8);
			Compiler compiler = new Compiler(sourceFile, source);

			compilers.push(compiler);
		}

		/*for (Compiler compiler: compilers)
		{
			compiler.compileClassDeclaration(classes);
		}

		classes.clearCache();*/

		for (Compiler compiler: compilers)
		{
			compiler.compileInterface(classes);
		}

		classes.clearCache();

		Array<CompilationResult> results = new Array<>();

		for (Compiler compiler: compilers)
		{
			results.push(compiler.compile(classes));
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

				for (GeneratedClass generatedClass: result.getGeneratedClasses())
				{
					try (OutputStream outputStream =
						new FileOutputStream(generatedClass.getFileName()))
					{
						outputStream.write(generatedClass.getBytecode());
					}
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
