package yirgacheffe.repl;

import java.util.Scanner;

public final class Repl
{
	private Scanner scanner = new Scanner(System.in);

	public static void main(String[] args)
	{
		new Repl().read();
	}

	private Repl()
	{
	}

	private void read()
	{
		do
		{
			System.out.print("yirgacheffe> ");

			String line = this.scanner.nextLine();

			System.out.println(line);
		}
		while (this.scanner.hasNextLine());
	}
}
