package yirgacheffe.lang;

import java.io.InputStream;
import java.io.PrintStream;

public class IO
{
	public PrintStream getOut()
	{
		return java.lang.System.out;
	}

	public void setOut(PrintStream out)
	{
		java.lang.System.setOut(out);
	}

	public InputStream getin()
	{
		return java.lang.System.in;
	}

	public void setIn(InputStream in)
	{
		java.lang.System.setIn(in);
	}

	public PrintStream getErr()
	{
		return java.lang.System.err;
	}

	public void setErr(PrintStream err)
	{
		java.lang.System.setErr(err);
	}
}
