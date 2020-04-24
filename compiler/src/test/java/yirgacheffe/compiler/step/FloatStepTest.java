package yirgacheffe.compiler.step;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.junit.Test;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;

public class FloatStepTest
{
	@Test
	public void testConvertType()
	{
		Stepable step = new FloatStep(PrimitiveType.FLOAT);

		Array<AbstractInsnNode> instructions = step.convertType().getInstructions();

		assertEquals(1, instructions.length());
		assertEquals(Opcodes.F2D, instructions.get(0).getOpcode());
	}

	@Test
	public void testDuplicate()
	{
		Stepable step = new FloatStep(PrimitiveType.FLOAT);

		Array<AbstractInsnNode> instructions = step.duplicate().getInstructions();

		assertEquals(1, instructions.length());
		assertEquals(Opcodes.DUP2, instructions.get(0).getOpcode());
	}

	@Test
	public void testIncrementByOne()
	{
		Stepable step = new FloatStep(PrimitiveType.FLOAT);

		Array<AbstractInsnNode> instructions = step.stepOne(true).getInstructions();

		assertEquals(2, instructions.length());
		assertEquals(Opcodes.DCONST_1, instructions.get(0).getOpcode());
		assertEquals(Opcodes.DADD, instructions.get(1).getOpcode());
	}

	@Test
	public void testDecrementByOne()
	{
		Stepable step = new FloatStep(PrimitiveType.FLOAT);

		Array<AbstractInsnNode> instructions = step.stepOne(1, false).getInstructions();

		assertEquals(4, instructions.length());
		assertEquals(Opcodes.DLOAD, instructions.get(0).getOpcode());
		assertEquals(Opcodes.DCONST_1, instructions.get(1).getOpcode());
		assertEquals(Opcodes.DSUB, instructions.get(2).getOpcode());
		assertEquals(Opcodes.DSTORE, instructions.get(3).getOpcode());
	}

	@Test
	public void testStore()
	{
		Stepable step = new FloatStep(PrimitiveType.FLOAT);

		Array<AbstractInsnNode> instructions = step.store(1).getInstructions();

		assertEquals(1, instructions.length());
		assertEquals(Opcodes.DSTORE, instructions.get(0).getOpcode());
	}
}
