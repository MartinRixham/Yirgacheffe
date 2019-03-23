package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;

import static org.junit.Assert.assertEquals;

public class BoolTest
{
	@Test
	public void testCompilingFalse()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Bool literal = new Bool("false");

		Type type = literal.getType(variables);

		literal.compile(methodVisitor, variables);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(1, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_0, firstInstruction.getOpcode());

		assertEquals("java.lang.Boolean", type.toFullyQualifiedType());
	}

	@Test
	public void testCompilingTrue()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Bool literal = new Bool("true");

		Type type = literal.getType(variables);

		literal.compile(methodVisitor, variables);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(1, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_1, firstInstruction.getOpcode());

		assertEquals("java.lang.Boolean", type.toFullyQualifiedType());
	}
}
