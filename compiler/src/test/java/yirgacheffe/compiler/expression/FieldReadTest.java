package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.type.PrimitiveType;
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
		Expression string = new This(owner);
		Type type = PrimitiveType.DOUBLE;
		FieldRead fieldRead = new FieldRead(string, "length", type);

		fieldRead.compile(methodVisitor);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(2, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		FieldInsnNode secondInstruction = (FieldInsnNode) instructions.get(1);

		assertEquals(Opcodes.GETFIELD, secondInstruction.getOpcode());
		assertEquals("java/lang/String", secondInstruction.owner);
		assertEquals("length", secondInstruction.name);
		assertEquals("D", secondInstruction.desc);

		assertEquals("java.lang.Double", fieldRead.getType().toFullyQualifiedType());
	}
}
