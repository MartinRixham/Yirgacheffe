package yirgacheffe.compiler.statement;

import org.junit.Test;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.implementation.NullImplementation;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BranchTest
{
	@Test
	public void testEmptyBranch()
	{
		Expression nothing = new Nothing();
		Branch branch = new Branch(new If(nothing, new DoNothing()));

		Expression expression = branch.getExpression();

		assertTrue(expression instanceof Nothing);
		assertFalse(branch.isEmpty());
		assertFalse(branch.getFieldAssignments().contains(""));

		Implementation delegatedInterfaces =
			branch.getDelegatedInterfaces(new HashMap<>(), new NullType());

		assertTrue(delegatedInterfaces instanceof NullImplementation);
	}

	@Test
	public void testGettingVariableReads()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		VariableRead read = new VariableRead(coordinate, "myVariable");
		VariableWrite write = new VariableWrite(coordinate, "var", read);
		Statement branch = new Branch(new If(read, write));

		Array<VariableRead> reads = branch.getVariableReads();

		assertTrue(reads.indexOf(read) >= 0);

		Array<VariableWrite> writes = branch.getVariableWrites();

		assertTrue(writes.indexOf(write) >= 0);
	}
}
