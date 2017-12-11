package yirgacheffe.compiler.main;

import yirgacheffe.compiler.Type.BytecodeClassLoader;
import yirgacheffe.compiler.Type.DeclaredType;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Package
{
	private Map<String, DeclaredType> declaredTypes = new HashMap<>();

	private BytecodeClassLoader classLoader;

	private List<Compiler> compilers = new ArrayList<>();

	public Package(
		BytecodeClassLoader classLoader,
		List<Compiler> compilers)
	{
		this.classLoader = classLoader;
		this.compilers = compilers;
	}

	public boolean compileClassDeclaration() throws Exception
	{
		boolean failed = false;

		for (Compiler compiler: this.compilers)
		{
			CompilationResult result =
				compiler.compileClassDeclaration(this.declaredTypes, this.classLoader);

			if (!result.isSuccessful())
			{
				System.err.print(result.getErrors());

				failed = true;
			}
		}

		return failed;
	}

	public void compile() throws Exception
	{
		for (Compiler compiler: this.compilers)
		{
			CompilationResult result =
				compiler.compile(this.declaredTypes, this.classLoader);

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
