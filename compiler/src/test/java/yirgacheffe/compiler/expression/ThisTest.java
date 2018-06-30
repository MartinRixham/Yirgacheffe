package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.type.ReferenceType;

import static org.junit.Assert.assertEquals;

public class ThisTest
{
	@Test
	public void testCompilingThis()
	{
		MethodNode methodVisitor = new MethodNode();

		This thisRead = new This(new ReferenceType(this.getClass()));

		thisRead.compile(methodVisitor);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(1, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		assertEquals(
			"yirgacheffe.compiler.expression.ThisTest",
			thisRead.getType().toFullyQualifiedType());
	}
}
