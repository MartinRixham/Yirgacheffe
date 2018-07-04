package yirgacheffe.repl;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Source;
import yirgacheffe.compiler.type.BytecodeClassLoader;
import yirgacheffe.lang.Array;
import yirgacheffe.parser.YirgacheffeParser;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
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
		System.setErr(new PrintStream(new ByteArrayOutputStream()));
	}

	public void read(InputStream in)
	{
		Array<String> statements = new Array<>();
		Array<String> imports = new Array<>();

		Scanner scanner = new Scanner(in);

		this.out.print(PROMPT);

		while (scanner.hasNextLine())
		{
			YirgacheffeParser.ReplLineContext line = this.parseLine(scanner.nextLine());
			String expression = "\"\"";

			for (YirgacheffeParser.ImportStatementContext importStatement:
				line.importStatement())
			{
				imports.push(this.getText(importStatement));
			}

			for (YirgacheffeParser.StatementContext statement: line.statement())
			{
				statements.push(this.getText(statement));
			}

			if (line.expression() != null)
			{
				expression = this.getText(line.expression());
			}

			EvaluationResult result = this.evaluate(imports, statements, expression);

			if (!result.isSuccessful())
			{
				for (int i = 0; i < line.importStatement().size(); i++)
				{
					imports.pop();
				}

				for (int i = 0; i < line.statement().size(); i++)
				{
					statements.pop();
				}

				this.out.print(this.removeLineNumbers(result.getResult()));
			}
			else if (result.getResult().length() > 0)
			{
				this.out.println(result.getResult());
			}

			this.out.print(PROMPT);
		}
	}

	private String getText(ParserRuleContext line)
	{
		try
		{
			return line.start.getInputStream().getText(
				new Interval(line.start.getStartIndex(), line.stop.getStopIndex()));
		}
		catch (StringIndexOutOfBoundsException e)
		{
			return null;
		}
	}

	private YirgacheffeParser.ReplLineContext parseLine(String line)
	{
		YirgacheffeParser parser = new Source(line).parse();

		return parser.replLine();
	}

	private EvaluationResult evaluate(
		Array<String> imports,
		Array<String> statements,
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

	private String removeLineNumbers(String in)
	{
		String[] errors = in.split("\n");
		StringBuilder out = new StringBuilder();

		for (int i = 0; i < errors.length; i++)
		{
			String[] parts = errors[i].split(" ");
			String[] remaining = new String[parts.length - 2];

			for (int j = 0; j < remaining.length; j++)
			{
				remaining[j] = parts[j + 2];
			}

			out.append(String.join(" ", remaining)).append("\n");
		}

		return out.toString();
	}
}
