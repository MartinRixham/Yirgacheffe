package yirgacheffe.compiler.instructions;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.type.PrimitiveType;

import static org.junit.Assert.assertEquals;

public class LongIntegerInstructionsTest
{
	@Test
	public void testInstructions()
	{
		Instructions instructions = new LongIntegerInstructions();

		assertEquals(Opcodes.LRETURN, instructions.getReturn());
		assertEquals(Opcodes.LSTORE, instructions.getStore());
		assertEquals(Opcodes.LASTORE, instructions.getArrayStore());
		assertEquals(Opcodes.LLOAD, instructions.getLoad());
		assertEquals(Opcodes.L2I, instructions.convertTo(PrimitiveType.INT));
		assertEquals(Opcodes.L2D, instructions.convertTo(PrimitiveType.DOUBLE));
		assertEquals(Opcodes.NOP, instructions.convertTo(PrimitiveType.LONG));
		assertEquals(Opcodes.LCONST_0, instructions.getZero());
		assertEquals(Opcodes.T_LONG, instructions.getType());
	}
}
