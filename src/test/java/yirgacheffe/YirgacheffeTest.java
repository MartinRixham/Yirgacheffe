package yirgacheffe;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class YirgacheffeTest
{
	@Test
	public void testYirgacheffe()
	{
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));

		Yirgacheffe.main(new String[0]);

		assertTrue(outContent.toString().length() > 0);

		System.setOut(null);
	}
}
