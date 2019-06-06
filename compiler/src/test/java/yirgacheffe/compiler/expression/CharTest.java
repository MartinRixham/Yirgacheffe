package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.error.Error;
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
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());

		Char literal = new Char("'r'");

		Type type = literal.getType(variables);

		Array<Error> errors = literal.compile(methodVisitor, variables);

		InsnList instructions = methodVisitor.instructions;

		assertFalse(literal.isCondition(variables));
		assertEquals('r', literal.getValue());
		assertEquals(0, literal.getVariableReads().length());
		assertEquals(0, errors.length());
		assertEquals(1, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals('r', firstInstruction.cst);

		assertEquals("java/lang/Character", type.toFullyQualifiedType());
	}

	@Test
	public void testCompilingWhitespace()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());

		Char literal = new Char("'\n'");

		Type type = literal.getType(variables);

		Array<Error> errors =
			literal.compileCondition(methodVisitor, variables, new Label());

		InsnList instructions = methodVisitor.instructions;

		assertFalse(literal.isCondition(variables));
		assertEquals('\n', literal.getValue());
		assertEquals(0, literal.getVariableReads().length());
		assertEquals(0, errors.length());
		assertEquals(1, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals('\n', firstInstruction.cst);

		assertEquals("java/lang/Character", type.toFullyQualifiedType());
	}
}
