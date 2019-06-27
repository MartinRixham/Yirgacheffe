package yirgacheffe.compiler.comparison;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;

public class EqualsTest
{
	@Test
	public void testEqualDoubles()
	{
		Label label = new Label();
		Equals equals = new Equals();
		Result result = equals.compile(label, PrimitiveType.DOUBLE);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCMPL, firstInstruction.getOpcode());

		JumpInsnNode secondInstruction = (JumpInsnNode) instructions.get(1);

		assertEquals(Opcodes.IFNE, secondInstruction.getOpcode());
	}

	@Test
	public void testEqualLongIntegers()
	{
		Label label = new Label();
		Equals equals = new Equals();
		Result result = equals.compile(label, PrimitiveType.LONG);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.LCMP, firstInstruction.getOpcode());

		JumpInsnNode secondInstruction = (JumpInsnNode) instructions.get(1);

		assertEquals(Opcodes.IFNE, secondInstruction.getOpcode());
	}

	@Test
	public void testEqualBooleans()
	{
		Label label = new Label();
		Equals equals = new Equals();
		Result result = equals.compile(label, PrimitiveType.BOOLEAN);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		JumpInsnNode firstInstruction = (JumpInsnNode) instructions.get(0);

		assertEquals(Opcodes.IF_ICMPNE, firstInstruction.getOpcode());
	}
}
