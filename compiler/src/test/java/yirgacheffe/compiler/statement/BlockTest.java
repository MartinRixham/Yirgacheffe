package yirgacheffe.compiler.statement;

import org.junit.Test;
import yirgacheffe.compiler.expression.Variable;
import yirgacheffe.compiler.type.PrimitiveType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BlockTest
{
	@Test
	public void testEmptyBlock()
	{
		Block block = new Block();

		assertEquals(0, block.size());
		assertFalse(block.isDeclared("thingy"));
	}

	@Test
	public void testBlockWithParent()
	{
		Block parent = new Block();

		parent.declare("thingy", new Variable(1, PrimitiveType.DOUBLE));

		Block block = new Block(parent);

		assertEquals(2, block.size());
		assertTrue(block.isDeclared("thingy"));
	}
}
