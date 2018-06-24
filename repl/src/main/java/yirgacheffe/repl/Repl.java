package yirgacheffe.repl;

import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.type.BytecodeClassLoader;

import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class Repl
{
	private PrintStream out;

	private static final String PROMPT = "yirgacheffe> ";

	public static void main(String[] args)
	{
		new Repl(System.out).read(System.in);
	}

	public Repl(PrintStream out)
	{
		this.out = out;
	}

	public void read(InputStream in)
	{
		List<String> statements = new ArrayList<>();
		List<String> imports = new ArrayList<>();

		Scanner scanner = new Scanner(in);

		this.out.print(PROMPT);

		while (scanner.hasNextLine())
		{
			String line = scanner.nextLine();

			if (line.contains("import"))
			{
				imports.add(line);

				EvaluationResult result = this.evaluate(imports, statements, "\"\"");

				this.out.print(result.getResult());

				if (!result.isSuccessful())
				{
					imports.remove(imports.size() - 1);
				}
			}
			else if (line.contains("="))
			{
				statements.add(line);

				EvaluationResult result = this.evaluate(imports, statements, "\"\"");

				this.out.print(result.getResult());

				if (!result.isSuccessful())
				{
					statements.remove(statements.size() - 1);
				}
			}
			else if (line.length() > 0)
			{
				EvaluationResult result = this.evaluate(imports, statements, line);

				this.out.println(result.getResult());
			}

			this.out.print(PROMPT);
		}
	}

	private EvaluationResult evaluate(
		List<String> imports,
		List<String> statements,
		String expression)
	{
		Source source = new Source(imports, statements, expression);
		CompilationResult result = source.compile();

		if (result.isSuccessful())
		{
			BytecodeClassLoader classLoader = new BytecodeClassLoader();

			classLoader.add("Source", result.getBytecode());

			try
			{
				Class<?> sourceClass = classLoader.loadClass("Source");
				Object instance = sourceClass.getConstructor().newInstance();
				Method evaluate = sourceClass.getMethod("evaluate");

				String evaluated = (String) evaluate.invoke(instance);

				return new EvaluationResult(evaluated, true);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		else
		{
			return new EvaluationResult(result.getErrors(), false);
		}
	}
}
