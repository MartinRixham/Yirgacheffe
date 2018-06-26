package yirgacheffe.repl;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Source;
import yirgacheffe.compiler.type.BytecodeClassLoader;
import yirgacheffe.parser.YirgacheffeParser;

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
			YirgacheffeParser.ReplLineContext line = this.parseLine(scanner.nextLine());

			if (line.importStatement() != null)
			{
				imports.add(this.getText(line));

				EvaluationResult result = this.evaluate(imports, statements, "\"\"");

				this.out.print(result.getResult());

				if (!result.isSuccessful())
				{
					imports.remove(imports.size() - 1);
				}
			}
			else if (line.statement() != null)
			{
				statements.add(this.getText(line));

				EvaluationResult result = this.evaluate(imports, statements, "\"\"");

				this.out.print(result.getResult());

				if (!result.isSuccessful())
				{
					statements.remove(statements.size() - 1);
				}
			}
			else if (line.expression() != null)
			{
				EvaluationResult result =
					this.evaluate(imports, statements, this.getText(line));

				this.out.println(result.getResult());
			}

			this.out.print(PROMPT);
		}
	}

	private String getText(ParserRuleContext line)
	{
		return line.start.getInputStream().getText(
			new Interval(line.start.getStartIndex(), line.stop.getStopIndex()));
	}

	private YirgacheffeParser.ReplLineContext parseLine(String line)
	{
		YirgacheffeParser parser = new Source(line).parse();

		return parser.replLine();
	}

	private EvaluationResult evaluate(
		List<String> imports,
		List<String> statements,
		String expression)
	{
		Evaluator evaluator = new Evaluator(imports, statements, expression);
		CompilationResult result = evaluator.compile();

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
