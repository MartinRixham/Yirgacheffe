package yirgacheffe.compiler.statement;

import org.junit.Test;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.Num;
import yirgacheffe.compiler.expression.UnaryOperation;

import static org.junit.Assert.assertFalse;

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
	}
}
