package yirgacheffe.compiler;

import yirgacheffe.compiler.type.BytecodeClassLoader;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Yirgacheffe
{
	private String[] sourceFiles;

	private BytecodeClassLoader classLoader = new BytecodeClassLoader();

	public static void main(String[] args) throws Exception
	{
		new Yirgacheffe(args).execute();
	}

	private Yirgacheffe(String[] sourceFiles) throws Exception
	{
		this.sourceFiles = sourceFiles;
	}

	private void execute() throws Exception
	{
		Collection<Package> packages = this.createPackages(this.sourceFiles);

		this.firstPass(packages);
		this.secondPass(packages);
		this.thirdPass(packages);
	}

	private List<Package> createPackages(String[] sourceFiles) throws Exception
	{
		Map<String, List<Compiler>> compilers = new HashMap<>();

		for (String sourceFile : sourceFiles)
		{
			byte[] encoded = Files.readAllBytes(Paths.get(sourceFile));
			String source = new String(encoded, "UTF-8");
			String directory = this.getDirectory(sourceFile);

			if (!compilers.containsKey(directory))
			{
				compilers.put(directory, new ArrayList<>());
			}

			compilers.get(directory).add(new Compiler(sourceFile, source));
		}

		List<Package> packages = new ArrayList<>();

		for (List<Compiler> comp: compilers.values())
		{
			packages.add(new Package(this.classLoader, comp));
		}

		return packages;
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

	private void firstPass(Collection<Package> packages) throws Exception
	{
		for (Package pkg: packages)
		{
			pkg.compileClassDeclaration();
		}
	}

	private void secondPass(Collection<Package> packages) throws Exception
	{
		for (Package pkg: packages)
		{
			pkg.compileInterface();
		}
	}

	private void thirdPass(Collection<Package> packages) throws Exception
	{
		for (Package pkg: packages)
		{
			this.printResult(pkg.compile());
		}
	}

	private void printResult(List<CompilationResult> results) throws Exception
	{
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
