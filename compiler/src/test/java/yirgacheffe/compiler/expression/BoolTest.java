package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class BoolTest
{
	@Test
	public void testCompilingFalse()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());

		Bool literal = new Bool("false");

		Type type = literal.getType(variables);

		literal.compile(methodVisitor, variables);

		InsnList instructions = methodVisitor.instructions;

		assertFalse(literal.isCondition(variables));
		assertEquals(false, literal.getValue());
		assertEquals(1, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_0, firstInstruction.getOpcode());
		assertEquals("java/lang/Boolean", type.toFullyQualifiedType());
	}

	@Test
	public void testCompilingTrue()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());

		Bool literal = new Bool("true");

		Type type = literal.getType(variables);

		literal.compileCondition(methodVisitor, variables, new Label());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(true, literal.getValue());
		assertEquals(1, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_1, firstInstruction.getOpcode());

		assertEquals("java/lang/Boolean", type.toFullyQualifiedType());
	}
}
