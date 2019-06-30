package yirgacheffe.compiler.instructions;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.type.PrimitiveType;

import static org.junit.Assert.assertEquals;

public class IntegerInstructionsTest
{
	@Test
	public void testInstructions()
	{
		Instructions instructions = new IntegerInstructions();

		assertEquals(Opcodes.IRETURN, instructions.getReturn());
		assertEquals(Opcodes.ISTORE, instructions.getStore());
		assertEquals(Opcodes.IASTORE, instructions.getArrayStore());
		assertEquals(Opcodes.ILOAD, instructions.getLoad());
		assertEquals(Opcodes.I2L, instructions.convertTo(PrimitiveType.LONG));
		assertEquals(Opcodes.I2D, instructions.convertTo(PrimitiveType.DOUBLE));
		assertEquals(Opcodes.NOP, instructions.convertTo(PrimitiveType.INT));
		assertEquals(Opcodes.ICONST_0, instructions.getZero());
		assertEquals(Opcodes.T_INT, instructions.getType());
	}
}
