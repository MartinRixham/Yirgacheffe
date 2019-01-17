package yirgacheffe.compiler.statement;

import org.junit.Test;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OptimisedStatementTest
{
	@Test
	public void testOptimisedStatement()
	{
		Statement nothing = new DoNothing();
		Statement optimisedStatement = new OptimisedStatement(nothing);

		assertEquals(new Array<>(), optimisedStatement.getVariableReads());
		assertEquals(new Array<>(), optimisedStatement.getVariableWrites());
		assertTrue(optimisedStatement.getExpression() instanceof  Nothing);
		assertTrue(optimisedStatement.isEmpty());
	}
}
