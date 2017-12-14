package yirgacheffe.compiler;

import yirgacheffe.compiler.type.BytecodeClassLoader;
import yirgacheffe.compiler.type.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Package
{
	private Map<String, Type> declaredTypes = new HashMap<>();

	private BytecodeClassLoader classLoader;

	private List<Compiler> compilers = new ArrayList<>();

	public Package(
		BytecodeClassLoader classLoader,
		List<Compiler> compilers)
	{
		this.classLoader = classLoader;
		this.compilers = compilers;
	}

	public void compileClassDeclaration() throws Exception
	{
		for (Compiler compiler: this.compilers)
		{
			compiler.compileClassDeclaration(this.declaredTypes, this.classLoader);
		}
	}

	public void compileInterface() throws Exception
	{
		for (Compiler compiler: this.compilers)
		{
			compiler.compileInterface(this.declaredTypes, this.classLoader);
		}
	}

	public List<CompilationResult> compile() throws Exception
	{
		List<CompilationResult> results = new ArrayList<>();

		for (Compiler compiler: this.compilers)
		{
			results.add(compiler.compile(this.declaredTypes, this.classLoader));
		}

		return results;
	}
}
