package yirgacheffe.compiler;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Package
{
	private List<Compiler> compilers = new ArrayList<>();

	private Map<String, Type> declaredTypes = new HashMap<>();

	public Package()
	{
	}

	public void addCompiler(Compiler compiler)
	{
		this.compilers.add(compiler);
	}

	public boolean compileClassDeclaration() throws Exception
	{
		boolean failed = false;

		for (Compiler compiler: this.compilers)
		{
			CompilationResult result =
				compiler.compileClassDeclaration(this.declaredTypes);

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
			CompilationResult result = compiler.compile(this.declaredTypes);

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
