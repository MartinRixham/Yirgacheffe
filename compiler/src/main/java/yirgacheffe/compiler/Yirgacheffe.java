package yirgacheffe.compiler;

import yirgacheffe.compiler.parallel.GeneratedClass;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.lang.Array;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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

		Array<CompilationResult> results = new Array<>();

		for (Compiler compiler: compilers)
		{
			results.push(compiler.compile(classes));
		}

		for (CompilationResult result: results)
		{
			if (result.isSuccessful())
			{
				Path path = Paths.get(result.getClassFileName());

				Files.write(path, result.getBytecode());

				for (GeneratedClass generatedClass: result.getGeneratedClasses())
				{
					Path generatedPath =
						Paths.get(generatedClass.getClassName() + ".class");

					Files.write(generatedPath, generatedClass.getBytecode());
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
