package yirgacheffe.compiler.instructions;

import org.junit.Test;
import org.objectweb.asm.Opcodes;

import static org.junit.Assert.assertEquals;

public class IntegerInstructionsTest
{
	@Test
	public void testInstructions()
	{
		Instructions instructions = new IntegerInstructions();

		assertEquals(Opcodes.IRETURN, instructions.getReturn());
		assertEquals(Opcodes.ISTORE, instructions.getStore());
		assertEquals(Opcodes.ILOAD, instructions.getLoad());
		assertEquals(Opcodes.D2I, instructions.getTypeConversion());
		assertEquals(Opcodes.ICONST_0, instructions.getZero());
	}
}
