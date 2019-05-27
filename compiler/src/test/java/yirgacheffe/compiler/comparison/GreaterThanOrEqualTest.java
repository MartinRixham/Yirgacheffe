package yirgacheffe.compiler.comparison;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.type.PrimitiveType;

import static org.junit.Assert.assertEquals;

public class GreaterThanOrEqualTest
{
	@Test
	public void testGreaterThanOrEqualDoubles()
	{
		MethodNode methodVisitor = new MethodNode();
		Label label = new Label();
		GreaterThanOrEqual greaterThanOrEqual = new GreaterThanOrEqual();

		greaterThanOrEqual.compile(methodVisitor, label, PrimitiveType.DOUBLE);

		InsnList instructions = methodVisitor.instructions;

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCMPL, firstInstruction.getOpcode());

		JumpInsnNode secondInstruction = (JumpInsnNode) instructions.get(1);

		assertEquals(Opcodes.IFLT, secondInstruction.getOpcode());
	}

	@Test
	public void testGreaterThanOrEqualLongIntegers()
	{
		MethodNode methodVisitor = new MethodNode();
		Label label = new Label();
		GreaterThanOrEqual greaterThanOrEqual = new GreaterThanOrEqual();

		greaterThanOrEqual.compile(methodVisitor, label, PrimitiveType.LONG);

		InsnList instructions = methodVisitor.instructions;

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.LCMP, firstInstruction.getOpcode());

		JumpInsnNode secondInstruction = (JumpInsnNode) instructions.get(1);

		assertEquals(Opcodes.IFLT, secondInstruction.getOpcode());
	}

	@Test
	public void testLessThanOrEqualBooleans()
	{
		MethodNode methodVisitor = new MethodNode();
		Label label = new Label();
		GreaterThanOrEqual greaterThanOrEqual = new GreaterThanOrEqual();

		greaterThanOrEqual.compile(methodVisitor, label, PrimitiveType.BOOLEAN);

		InsnList instructions = methodVisitor.instructions;

		JumpInsnNode firstInstruction = (JumpInsnNode) instructions.get(0);

		assertEquals(Opcodes.IF_ICMPLT, firstInstruction.getOpcode());
	}
}
