package yirgacheffe.compiler.expression;

import org.junit.Test;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class NothingTest
{
	@Test
	public void testGettingVariableReads()
	{
		Variables variables = new LocalVariables(1, new HashMap<>());

		Expression nothing = new Nothing();

		nothing.compile(variables);
		Result result = nothing.compileCondition(variables, null, null);

		Array<VariableRead> reads = nothing.getVariableReads();

		assertEquals(0, result.getErrors().length());
		assertFalse(nothing.isCondition(variables));
		assertEquals(0, reads.length());
		assertEquals(0, nothing.getCoordinate().compareTo(new Coordinate(0, 0)));
	}
}
