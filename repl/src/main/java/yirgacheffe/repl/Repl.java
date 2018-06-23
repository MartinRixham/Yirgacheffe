package yirgacheffe.repl;

import java.io.InputStream;
import java.io.PrintStream;
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
				this.out.println(line);
				this.out.print(PROMPT);
			}
		}
	}
}
