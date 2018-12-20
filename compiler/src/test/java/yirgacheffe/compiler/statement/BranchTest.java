package yirgacheffe.compiler.statement;

import org.junit.Test;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BranchTest
{
	@Test
	public void testFirstOperandIsNothing()
	{
		Branch branch = new Branch(new If(new Nothing(), new DoNothing()));

		Expression operand = branch.getFirstOperand();

		assertTrue(operand instanceof Nothing);
	}

	@Test
	public void testGettingVariableReads()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		VariableRead read = new VariableRead("myVariable", coordinate);
		VariableWrite write = new VariableWrite(coordinate, "var", read);
		Statement branch = new Branch(new If(read, write));

		Array<VariableRead> reads = branch.getVariableReads();

		assertTrue(reads.indexOf(read) >= 0);

		Array<VariableWrite> writes = branch.getVariableWrites();

		assertTrue(writes.indexOf(write) >= 0);
		assertEquals(read, branch.getFirstOperand());
	}
}
