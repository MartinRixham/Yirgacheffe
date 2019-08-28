package yirgacheffe.compiler.statement;

import org.junit.Test;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.Num;
import yirgacheffe.compiler.expression.UnaryOperation;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.implementation.NullImplementation;
import yirgacheffe.compiler.type.NullType;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AttemptedStatementTest
{
	@Test
	public void testEmptyBranch()
	{
		Coordinate coordinate = new Coordinate(3, 5);

		Statement postincrement =
			new UnaryOperation(coordinate, new Num("1"), false, true);

		Statement attemptedStatement = new AttemptedStatement(postincrement);

		assertFalse(attemptedStatement.returns());
		assertFalse(attemptedStatement.isEmpty());
		assertEquals(0, attemptedStatement.getFieldAssignments().length());

		Implementation delegatedInterfaces =
			attemptedStatement.getDelegatedInterfaces(new HashMap<>(), new NullType());

		assertTrue(delegatedInterfaces instanceof NullImplementation);
	}
}
