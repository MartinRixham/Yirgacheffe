package yirgacheffe.compiler.expression;

import org.junit.Test;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;

public class NothingTest
{
	@Test
	public void testGettingVariableReads()
	{
		Expression nothing = new Nothing();

		Array<VariableRead> reads = nothing.getVariableReads();

		assertEquals(0, reads.length());
		assertEquals(nothing, nothing.getFirstOperand());
	}
}
