package yirgacheffe.compiler.function;

import org.junit.Test;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AmbiguousMatchResultTest
{
	@Test
	public void testAmbiguousMatchResult()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		Variables variables = new LocalVariables(1, new HashMap<>());
		MatchResult matchResult = new AmbiguousMatchResult(coordinate, "method", 1);

		assertEquals("", matchResult.getName());
		assertEquals(0, matchResult.getParameterTypes().length());
		assertTrue(matchResult.getReturnType() instanceof NullType);
		assertEquals(matchResult, matchResult.betterOf(matchResult));

		Array<Error> errors = matchResult.compileArguments(variables).getErrors();

		assertEquals(1, errors.length());
		assertEquals("line 3:5 Ambiguous call to method.", errors.get(0).toString());

		Result result = matchResult.compileArguments(variables);

		assertEquals(0, result.getInstructions().length());
	}

	@Test
	public void testGettingBetterMatchResult()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		MatchResult firstResult = new AmbiguousMatchResult(coordinate, "method", 1);
		MatchResult secondResult = new AmbiguousMatchResult(coordinate, "method", 2);

		assertEquals(secondResult, firstResult.betterOf(secondResult));
		assertEquals(secondResult, secondResult.betterOf(firstResult));
	}

	@Test
	public void testGettingEqualMatchResult()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		MatchResult firstResult = new AmbiguousMatchResult(coordinate, "method", 1);
		MatchResult secondResult = new AmbiguousMatchResult(coordinate, "method", 1);

		assertEquals(firstResult, firstResult.betterOf(secondResult));
		assertEquals(secondResult, secondResult.betterOf(firstResult));
	}
}
