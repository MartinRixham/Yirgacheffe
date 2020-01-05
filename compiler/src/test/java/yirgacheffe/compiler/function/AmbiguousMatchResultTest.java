package yirgacheffe.compiler.function;

import org.junit.Test;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.variables.Variables;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AmbiguousMatchResultTest
{
	@Test
	public void testAmbiguousMatchResult()
	{
		Variables variables = new LocalVariables(1, new HashMap<>());
		MatchResult matchResult = new AmbiguousMatchResult(1);

		assertEquals("", matchResult.getName());
		assertEquals(0, matchResult.getParameterTypes().length());
		assertEquals(0, matchResult.getMismatchedParameters().length());
		assertTrue(matchResult.getReturnType() instanceof NullType);
		assertEquals(matchResult, matchResult.betterOf(matchResult));

		Result result = matchResult.compileArguments(variables);

		assertEquals(0, result.getErrors().length());
		assertEquals(0, result.getInstructions().length());
	}

	@Test
	public void testGettingBetterMatchResult()
	{
		MatchResult firstResult = new AmbiguousMatchResult(1);
		MatchResult secondResult = new AmbiguousMatchResult(2);

		assertEquals(secondResult, firstResult.betterOf(secondResult));
		assertEquals(secondResult, secondResult.betterOf(firstResult));
	}

	@Test
	public void testGettingEqualMatchResult()
	{
		MatchResult firstResult = new AmbiguousMatchResult(1);
		MatchResult secondResult = new AmbiguousMatchResult(1);

		assertEquals(firstResult, firstResult.betterOf(secondResult));
		assertEquals(secondResult, secondResult.betterOf(firstResult));
	}
}
