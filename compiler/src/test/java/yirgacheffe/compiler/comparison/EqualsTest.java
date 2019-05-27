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

public class EqualsTest
{
	@Test
	public void testEqualDoubles()
	{
		MethodNode methodVisitor = new MethodNode();
		Label label = new Label();
		Equals equals = new Equals();

		equals.compile(methodVisitor, label, PrimitiveType.DOUBLE);

		InsnList instructions = methodVisitor.instructions;

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCMPL, firstInstruction.getOpcode());

		JumpInsnNode secondInstruction = (JumpInsnNode) instructions.get(1);

		assertEquals(Opcodes.IFNE, secondInstruction.getOpcode());
	}

	@Test
	public void testEqualLongIntegers()
	{
		MethodNode methodVisitor = new MethodNode();
		Label label = new Label();
		Equals equals = new Equals();

		equals.compile(methodVisitor, label, PrimitiveType.LONG);

		InsnList instructions = methodVisitor.instructions;

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.LCMP, firstInstruction.getOpcode());

		JumpInsnNode secondInstruction = (JumpInsnNode) instructions.get(1);

		assertEquals(Opcodes.IFNE, secondInstruction.getOpcode());
	}

	@Test
	public void testEqualBooleans()
	{
		MethodNode methodVisitor = new MethodNode();
		Label label = new Label();
		Equals equals = new Equals();

		equals.compile(methodVisitor, label, PrimitiveType.BOOLEAN);

		InsnList instructions = methodVisitor.instructions;

		JumpInsnNode firstInstruction = (JumpInsnNode) instructions.get(0);

		assertEquals(Opcodes.IF_ICMPNE, firstInstruction.getOpcode());
	}
}
