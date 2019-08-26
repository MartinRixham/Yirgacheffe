package yirgacheffe.compiler.statement;

import org.junit.Test;
import org.objectweb.asm.Label;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
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
		assertEquals(0, labelStatement.getFieldAssignments().length());

		Array<Type> delegatedInterfaces =
			labelStatement.getDelegatedInterfaces(new HashMap<>(), new NullType());

		assertEquals(0, delegatedInterfaces.length());
	}
}
