package yirgacheffe.compiler.instructions;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.type.PrimitiveType;

import static org.junit.Assert.assertEquals;

public class FloatInstructionsTest
{
	@Test
	public void testInstructions()
	{
		Instructions instructions = new FloatInstructions();

		assertEquals(Opcodes.FRETURN, instructions.getReturn());
		assertEquals(Opcodes.FSTORE, instructions.getStore());
		assertEquals(Opcodes.FASTORE, instructions.getArrayStore());
		assertEquals(Opcodes.FLOAD, instructions.getLoad());
		assertEquals(Opcodes.F2I, instructions.convertTo(PrimitiveType.INT));
		assertEquals(Opcodes.F2L, instructions.convertTo(PrimitiveType.LONG));
		assertEquals(Opcodes.F2D, instructions.convertTo(PrimitiveType.DOUBLE));
		assertEquals(Opcodes.NOP, instructions.convertTo(PrimitiveType.BOOLEAN));
		assertEquals(Opcodes.FCONST_0, instructions.getZero());
		assertEquals(Opcodes.T_FLOAT, instructions.getType());
	}
}
