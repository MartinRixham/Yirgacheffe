package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CharTest
{
	@Test
	public void testCompilingCharacter()
	{
		Coordinate coordinate = new Coordinate(6, 7);
		Variables variables = new LocalVariables(1, new HashMap<>(), new HashMap<>());

		Char literal = new Char(coordinate, "'r'");

		Type type = literal.getType(variables);
		Result result = literal.compile(variables);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(1, variables.getStack().length());
		assertFalse(literal.isCondition(variables));
		assertEquals('r', literal.getValue());
		assertEquals(0, literal.getVariableReads().length());
		assertEquals(0, result.getErrors().length());
		assertEquals(1, instructions.length());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals('r', firstInstruction.cst);

		assertEquals("java/lang/Character", type.toFullyQualifiedType());
		assertEquals(coordinate, literal.getCoordinate());
	}

	@Test
	public void testCompilingWhitespace()
	{
		Coordinate coordinate = new Coordinate(3, 75);
		Variables variables = new LocalVariables(1, new HashMap<>(), new HashMap<>());

		Char literal = new Char(coordinate, "'\n'");

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
