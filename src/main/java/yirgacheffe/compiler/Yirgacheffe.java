package yirgacheffe.compiler;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
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

		boolean failed = this.firstPass(packages);

		if (failed)
		{
			return;
		}

		this.secondPass(packages);
	}

	private Collection<Package> createPackages(String[] sourceFiles) throws Exception
	{
		Map<String, Package> packages = new HashMap<>();

		for (String sourceFile : sourceFiles)
		{
			byte[] encoded = Files.readAllBytes(Paths.get(sourceFile));
			String source = new String(encoded, "UTF-8");
			String directory = this.getDirectory(sourceFile);

			if (!packages.containsKey(directory))
			{
				packages.put(directory, new Package(this.classLoader));
			}

			packages.get(directory).addCompiler(new Compiler(directory, source));
		}

		return packages.values();
	}

	private boolean firstPass(Collection<Package> packages) throws Exception
	{
		boolean failed = false;

		for (Package pkg: packages)
		{
			failed = failed || pkg.compileClassDeclaration();
		}

		return failed;
	}

	private void secondPass(Collection<Package> packages) throws Exception
	{
		for (Package pkg: packages)
		{
			pkg.compile();
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
