package yirgacheffe.compiler.statement;

import org.junit.Test;
import org.objectweb.asm.Label;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LabelStatementTest
{
	@Test
	public void testLabel()
	{
		LabelStatement labelStatement = new LabelStatement(new Label());

		assertTrue(labelStatement.getFirstOperand() instanceof Nothing);
		assertEquals(new Array<>(), labelStatement.getVariableReads());
		assertEquals(new Array<>(), labelStatement.getVariableWrites());
		assertTrue(labelStatement.getExpression() instanceof Nothing);
		assertTrue(labelStatement.isEmpty());
	}
}
