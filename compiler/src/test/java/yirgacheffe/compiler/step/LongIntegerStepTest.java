package yirgacheffe.compiler.step;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;

public class LongIntegerStepTest
{
	@Test
	public void testConvertType()
	{
		Stepable step = new LongIntegerStep();

		Array<AbstractInsnNode> instructions = step.convertType().getInstructions();

		assertEquals(0, instructions.length());
	}

	@Test
	public void testDuplicate()
	{
		Stepable step = new LongIntegerStep();

		Array<AbstractInsnNode> instructions = step.duplicate().getInstructions();

		assertEquals(1, instructions.length());
		assertEquals(Opcodes.DUP2, instructions.get(0).getOpcode());
	}

	@Test
	public void testIncrementByOne()
	{
		Stepable step = new LongIntegerStep();

		Array<AbstractInsnNode> instructions = step.stepOne(true).getInstructions();

		assertEquals(2, instructions.length());
		assertEquals(Opcodes.LCONST_1, instructions.get(0).getOpcode());
		assertEquals(Opcodes.LADD, instructions.get(1).getOpcode());
	}

	@Test
	public void testDecrementByOne()
	{
		Stepable step = new LongIntegerStep();

		Array<AbstractInsnNode> instructions = step.stepOne(1, false).getInstructions();

		assertEquals(4, instructions.length());
		assertEquals(Opcodes.LLOAD, instructions.get(0).getOpcode());
		assertEquals(Opcodes.LCONST_1, instructions.get(1).getOpcode());
		assertEquals(Opcodes.LSUB, instructions.get(2).getOpcode());
		assertEquals(Opcodes.LSTORE, instructions.get(3).getOpcode());
	}

	@Test
	public void testStore()
	{
		Stepable step = new LongIntegerStep();

		Array<AbstractInsnNode> instructions = step.store(1).getInstructions();

		assertEquals(1, instructions.length());
		assertEquals(Opcodes.LSTORE, instructions.get(0).getOpcode());
	}
}
