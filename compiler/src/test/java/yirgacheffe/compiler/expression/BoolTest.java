package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class BoolTest
{
	@Test
	public void testCompilingFalse()
	{
		Coordinate coordinate = new Coordinate(8, 30);
		Variables variables = new LocalVariables(1, new HashMap<>(), new HashMap<>());

		Bool literal = new Bool(coordinate, "false");

		Type type = literal.getType(variables);
		Result result = literal.compile(variables);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(1, variables.getStack().length());
		assertFalse(literal.isCondition(variables));
		assertEquals(false, literal.getValue());
		assertEquals(1, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_0, firstInstruction.getOpcode());
		assertEquals("java/lang/Boolean", type.toFullyQualifiedType());
		assertEquals(coordinate, literal.getCoordinate());
	}

	@Test
	public void testCompilingTrue()
	{
		Coordinate coordinate = new Coordinate(5, 7);
		Variables variables = new LocalVariables(1, new HashMap<>(), new HashMap<>());

		Bool literal = new Bool(coordinate, "true");

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
