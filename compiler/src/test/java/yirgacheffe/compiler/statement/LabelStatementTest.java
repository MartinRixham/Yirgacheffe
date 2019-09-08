package yirgacheffe.compiler.statement;

import org.junit.Test;
import org.objectweb.asm.Label;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.implementation.NullImplementation;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LabelStatementTest
{
	@Test
	public void testLabel()
	{
		LabelStatement labelStatement = new LabelStatement(new Label());

		assertEquals(new Array<>(), labelStatement.getVariableReads());
		assertEquals(new Array<>(), labelStatement.getVariableWrites());
		assertTrue(labelStatement.getExpression() instanceof Nothing);
		assertTrue(labelStatement.isEmpty());
		assertFalse(labelStatement.getFieldAssignments().contains(""));

		Implementation delegatedInterfaces =
			labelStatement.getDelegatedInterfaces(new HashMap<>(), new NullType());

		assertTrue(delegatedInterfaces instanceof NullImplementation);
	}
}
