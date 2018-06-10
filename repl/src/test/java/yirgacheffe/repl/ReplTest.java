package yirgacheffe.repl;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class ReplTest
{
	@Test
	public void TestPrompt()
	{
		PrintStream originalOut = System.out;
		ByteArrayOutputStream spyOut = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(spyOut);

		System.setOut(out);

		Repl.main(null);

		assertEquals("yirgacheffe> ", spyOut.toString());

		System.setOut(originalOut);
	}
}
