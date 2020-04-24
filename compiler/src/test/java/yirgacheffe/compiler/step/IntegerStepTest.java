package yirgacheffe.compiler.step;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;

public class IntegerStepTest
{
	@Test
	public void testConvertType()
	{
		Stepable step = new IntegerStep();

		Array<AbstractInsnNode> instructions = step.convertType().getInstructions();

		assertEquals(0, instructions.length());
	}

	@Test
	public void testDuplicate()
	{
		Stepable step = new IntegerStep();

		Array<AbstractInsnNode> instructions = step.duplicate().getInstructions();

		assertEquals(1, instructions.length());
		assertEquals(Opcodes.DUP, instructions.get(0).getOpcode());
	}

	@Test
	public void testIncrementByOne()
	{
		Stepable step = new IntegerStep();

		Array<AbstractInsnNode> instructions = step.stepOne(true).getInstructions();

		assertEquals(2, instructions.length());
		assertEquals(Opcodes.ICONST_1, instructions.get(0).getOpcode());
		assertEquals(Opcodes.IADD, instructions.get(1).getOpcode());
	}

	@Test
	public void testDecrementByOne()
	{
		Stepable step = new IntegerStep();

		Array<AbstractInsnNode> instructions = step.stepOne(1, false).getInstructions();

		assertEquals(1, instructions.length());
		assertEquals(Opcodes.IINC, instructions.get(0).getOpcode());
	}

	@Test
	public void testStore()
	{
		Stepable step = new IntegerStep();

		Array<AbstractInsnNode> instructions = step.store(1).getInstructions();

		assertEquals(1, instructions.length());
		assertEquals(Opcodes.ISTORE, instructions.get(0).getOpcode());
	}
}
