package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;

import static org.junit.Assert.assertEquals;

public class FieldReadTest
{
	@Test
	public void testCompilingFieldRead()
	{
		MethodNode methodVisitor = new MethodNode();

		Type owner = new ReferenceType(String.class);
		FieldRead fieldRead = new FieldRead(owner, "length", "I");

		fieldRead.compile(methodVisitor);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(1, instructions.size());

		FieldInsnNode firstInstruction = (FieldInsnNode) instructions.get(0);

		assertEquals(Opcodes.GETFIELD, firstInstruction.getOpcode());
		assertEquals("java/lang/String", firstInstruction.owner);
		assertEquals("length", firstInstruction.name);
		assertEquals("I", firstInstruction.desc);
	}
}
