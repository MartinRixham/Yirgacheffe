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

public class StreengTest
{
	@Test
	public void testCompilingStringLiteral()
	{
		Coordinate coordinate = new Coordinate(3, 65);
		Streeng literal = new Streeng(coordinate, "\"thingy\"");
		Variables variables = new LocalVariables(1, new HashMap<>(), new HashMap<>());

		Type type = literal.getType(variables);
		Result result = literal.compile(variables);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(1, variables.getStack().length());
		assertFalse(literal.isCondition(variables));
		assertEquals("thingy", literal.getValue());
		assertEquals(1, instructions.length());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals("thingy", firstInstruction.cst);

		assertEquals("java/lang/String", type.toFullyQualifiedType());
	}

	@Test
	public void testCompilingStringWithQuotes()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		Variables variables = new LocalVariables(1, new HashMap<>(), new HashMap<>());

		Streeng literal = new Streeng(coordinate, "\"thi\"ngy\"");

		Type type = literal.getType(variables);
		Result result = literal.compileCondition(variables, null, null);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(0, result.getErrors().length());
		assertEquals("thi\"ngy", literal.getValue());
		assertEquals(1, instructions.length());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals("thi\"ngy", firstInstruction.cst);

		assertEquals("java/lang/String", type.toFullyQualifiedType());
		assertEquals(coordinate, literal.getCoordinate());
	}
}
