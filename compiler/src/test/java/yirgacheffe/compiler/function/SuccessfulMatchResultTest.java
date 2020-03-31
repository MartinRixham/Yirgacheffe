package yirgacheffe.compiler.function;

import org.junit.Test;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SuccessfulMatchResultTest
{
	@Test
	public void testGettingBetterMatchResult() throws Exception
	{
		Coordinate coordinate = new Coordinate(3, 5);
		Variables variables = new LocalVariables(1, new HashMap<>());

		Function function =
			new Function(new ReferenceType(String.class), String.class.getConstructor());

		Arguments arguments =
			new Arguments(coordinate, "method", new Array<>(), variables);

		MatchResult firstResult =
			new SuccessfulMatchResult(
				coordinate,
				"method",
				function,
				arguments,
				1,
				new Array<>());

		MatchResult secondResult =
			new SuccessfulMatchResult(
				coordinate,
				"method",
				function,
				arguments,
				2,
				new Array<>());

		assertEquals(secondResult, firstResult.betterOf(secondResult));
		assertEquals(secondResult, secondResult.betterOf(firstResult));
	}

	@Test
	public void testGettingEqualMatchResult() throws Exception
	{
		Coordinate coordinate = new Coordinate(3, 5);
		Variables variables = new LocalVariables(1, new HashMap<>());

		Function function =
			new Function(new ReferenceType(String.class), String.class.getConstructor());

		Arguments arguments =
			new Arguments(coordinate, "method", new Array<>(), variables);

		MatchResult firstResult =
			new SuccessfulMatchResult(
				coordinate,
				"method",
				function,
				arguments,
				1,
				new Array<>());

		MatchResult secondResult =
			new SuccessfulMatchResult(
				coordinate,
				"method",
				function,
				arguments,
				1,
				new Array<>());

		assertTrue(firstResult.betterOf(secondResult) instanceof AmbiguousMatchResult);
	}

	@Test
	public void testNotBetterThanAmbiguousMatchResult() throws Exception
	{
		Coordinate coordinate = new Coordinate(3, 5);
		Variables variables = new LocalVariables(1, new HashMap<>());

		Function function =
			new Function(new ReferenceType(String.class), String.class.getConstructor());

		Arguments arguments =
			new Arguments(coordinate, "method", new Array<>(), variables);

		MatchResult firstResult =
			new SuccessfulMatchResult(
				coordinate,
				"method",
				function,
				arguments,
				1,
				new Array<>());

		MatchResult secondResult = new AmbiguousMatchResult(coordinate, "method", 1);

		assertEquals(secondResult, firstResult.betterOf(secondResult));
		assertEquals(secondResult, secondResult.betterOf(firstResult));
	}
}
