package yirgacheffe.compiler.expression;

import org.junit.Test;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class OptimisedExpressionTest
{
	@Test
	public void testGettingVariableReads()
	{
		Variables variables = new LocalVariables(new HashMap<>());
		Nothing nothing = new Nothing();

		Expression optimisedExpression = new OptimisedExpression(nothing);

		Result result = optimisedExpression.compileCondition(variables, null, null);

		Array<VariableRead> reads = optimisedExpression.getVariableReads();

		assertEquals(PrimitiveType.VOID, optimisedExpression.getType(variables));
		assertEquals(0, result.getErrors().length());
		assertFalse(optimisedExpression.isCondition(variables));
		assertEquals(0, reads.length());
		assertEquals(nothing.hashCode(), optimisedExpression.hashCode());
	}
}
