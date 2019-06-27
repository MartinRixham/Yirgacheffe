package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class BoolTest
{
	@Test
	public void testCompilingFalse()
	{
		Variables variables = new Variables(new HashMap<>());

		Bool literal = new Bool("false");

		Type type = literal.getType(variables);
		Result result = literal.compile(variables);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertFalse(literal.isCondition(variables));
		assertEquals(false, literal.getValue());
		assertEquals(1, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_0, firstInstruction.getOpcode());
		assertEquals("java/lang/Boolean", type.toFullyQualifiedType());
	}

	@Test
	public void testCompilingTrue()
	{
		Variables variables = new Variables(new HashMap<>());

		Bool literal = new Bool("true");

		Type type = literal.getType(variables);
		Result result = literal.compileCondition(variables, null, null);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(0, result.getErrors().length());
		assertEquals(true, literal.getValue());
		assertEquals(1, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_1, firstInstruction.getOpcode());

		assertEquals("java/lang/Boolean", type.toFullyQualifiedType());
	}
}
