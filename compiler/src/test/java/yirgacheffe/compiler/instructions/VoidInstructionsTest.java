package yirgacheffe.compiler.instructions;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.type.PrimitiveType;

import static org.junit.Assert.assertEquals;

public class VoidInstructionsTest
{
	@Test
	public void testInstructions()
	{
		Instructions instructions = new VoidInstructions();

		assertEquals(Opcodes.RETURN, instructions.getReturn());
		assertEquals(Opcodes.NOP, instructions.getStore());
		assertEquals(Opcodes.NOP, instructions.getLoad());
		assertEquals(Opcodes.NOP, instructions.convertTo(PrimitiveType.INT));
		assertEquals(Opcodes.NOP, instructions.getZero());
	}
}
