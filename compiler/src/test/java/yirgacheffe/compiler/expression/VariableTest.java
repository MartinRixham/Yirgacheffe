package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;

import static org.junit.Assert.assertEquals;

public class VariableTest
{
	@Test
	public void testCompilingStringRead()
	{
		MethodNode methodVisitor = new MethodNode();
		Type owner = new ReferenceType(String.class);
		Variable variable = new Variable(1, owner);

		variable.compile(methodVisitor);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(1, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);

		assertEquals("java.lang.String", variable.getType().toFullyQualifiedType());
	}

	@Test
	public void testCompilingNumberRead()
	{
		MethodNode methodVisitor = new MethodNode();
		Type owner = PrimitiveType.DOUBLE;
		Variable variable = new Variable(1, owner);

		variable.compile(methodVisitor);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(1, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.DLOAD, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);

		assertEquals("java.lang.Double", variable.getType().toFullyQualifiedType());
	}
}
