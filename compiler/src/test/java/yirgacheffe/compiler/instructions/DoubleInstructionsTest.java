package yirgacheffe.compiler.instructions;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.type.PrimitiveType;

import static org.junit.Assert.assertEquals;

public class DoubleInstructionsTest
{
	@Test
	public void testInstructions()
	{
		Instructions instructions = new DoubleInstructions();

		assertEquals(Opcodes.DRETURN, instructions.getReturn());
		assertEquals(Opcodes.DSTORE, instructions.getStore());
		assertEquals(Opcodes.DASTORE, instructions.getArrayStore());
		assertEquals(Opcodes.DLOAD, instructions.getLoad());
		assertEquals(Opcodes.D2I, instructions.convertTo(PrimitiveType.INT));
		assertEquals(Opcodes.D2L, instructions.convertTo(PrimitiveType.LONG));
		assertEquals(Opcodes.NOP, instructions.convertTo(PrimitiveType.DOUBLE));
		assertEquals(Opcodes.DCONST_0, instructions.getZero());
		assertEquals(Opcodes.T_DOUBLE, instructions.getType());
	}
}
