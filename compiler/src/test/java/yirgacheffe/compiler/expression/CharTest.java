package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CharTest
{
	@Test
	public void testCompilingCharacter()
	{
		Variables variables = new Variables(new HashMap<>());

		Char literal = new Char("'r'");

		Type type = literal.getType(variables);
		Result result = literal.compile(variables);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertFalse(literal.isCondition(variables));
		assertEquals('r', literal.getValue());
		assertEquals(0, literal.getVariableReads().length());
		assertEquals(0, result.getErrors().length());
		assertEquals(1, instructions.length());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals('r', firstInstruction.cst);

		assertEquals("java/lang/Character", type.toFullyQualifiedType());
	}

	@Test
	public void testCompilingWhitespace()
	{
		Variables variables = new Variables(new HashMap<>());

		Char literal = new Char("'\n'");

		Type type = literal.getType(variables);
		Result result = literal.compileCondition(variables, null, null);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertFalse(literal.isCondition(variables));
		assertEquals('\n', literal.getValue());
		assertEquals(0, literal.getVariableReads().length());
		assertEquals(0, result.getErrors().length());
		assertEquals(1, instructions.length());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals('\n', firstInstruction.cst);

		assertEquals("java/lang/Character", type.toFullyQualifiedType());
	}
}
