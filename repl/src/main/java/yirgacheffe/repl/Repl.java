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

		Scanner scanner = new Scanner(in);

		this.out.print(PROMPT);

		while (scanner.hasNextLine())
		{
			String line = scanner.nextLine();

			if (line.length() == 0)
			{
				this.out.print(PROMPT);
			}
			else
			{
				if (line.contains("="))
				{
					statements.add(line);

					this.out.print(this.evaluate(statements, "\"\""));
				}
				else
				{
					this.out.println(this.evaluate(statements, line));
				}

				this.out.print(PROMPT);
			}
		}
	}

	private String evaluate(List<String> statements, String expression)
	{
		Source source = new Source(statements, expression);
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

				return (String) evaluate.invoke(instance);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		else
		{
			return result.getErrors();
		}
	}
}
