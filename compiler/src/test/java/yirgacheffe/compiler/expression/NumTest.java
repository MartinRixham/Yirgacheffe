package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
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

public class NumTest
{
	@Test
	public void testCompilingZero()
	{
		Coordinate coordinate = new Coordinate(4, 8);
		Variables variables = new LocalVariables(1, new HashMap<>(), new HashMap<>());

		Num literal = new Num(coordinate, "0.0");

		Type type = literal.getType(variables);
		Result result = literal.compile(variables);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(1, variables.getStack().length());
		assertFalse(literal.isCondition(variables));
		assertEquals(0.0, literal.getValue());
		assertEquals(1, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCONST_0, firstInstruction.getOpcode());
		assertEquals("java/lang/Double", type.toFullyQualifiedType());
	}

	@Test
	public void testCompilingOne()
	{
		Coordinate coordinate = new Coordinate(3, 6);
		Variables variables = new LocalVariables(1, new HashMap<>(), new HashMap<>());

		Num literal = new Num(coordinate, "1.0");

		Type type = literal.getType(variables);
		Result result = literal.compile(variables);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(1.0, literal.getValue());
		assertEquals(1, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCONST_1, firstInstruction.getOpcode());
		assertEquals("java/lang/Double", type.toFullyQualifiedType());
	}
	@Test
	public void testCompilingIntegerZero()
	{
		Coordinate coordinate = new Coordinate(7, 9);
		Variables variables = new LocalVariables(1, new HashMap<>(), new HashMap<>());

		Num literal = new Num(coordinate, "0");

		Type type = literal.getType(variables);
		Result result = literal.compile(variables);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(0, literal.getValue());
		assertEquals(1, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_0, firstInstruction.getOpcode());
		assertEquals("java/lang/Integer", type.toFullyQualifiedType());
	}

	@Test
	public void testCompilingIntegerOne()
	{
		Coordinate coordinate = new Coordinate(2, 56);
		Variables variables = new LocalVariables(1, new HashMap<>(), new HashMap<>());

		Num literal = new Num(coordinate, "1");

		Type type = literal.getType(variables);
		Result result = literal.compile(variables);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(1, literal.getValue());
		assertEquals(1, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_1, firstInstruction.getOpcode());
		assertEquals("java/lang/Integer", type.toFullyQualifiedType());
	}

	@Test
	public void testCompilingInteger()
	{
		Coordinate coordinate = new Coordinate(3, 67);
		Variables variables = new LocalVariables(1, new HashMap<>(), new HashMap<>());

		Num literal = new Num(coordinate, "2.0");

		Type type = literal.getType(variables);
		Result result = literal.compile(variables);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(2.0, literal.getValue());
		assertEquals(1, instructions.length());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(2.0, firstInstruction.cst);

		assertEquals("java/lang/Double", type.toFullyQualifiedType());
	}

	@Test
	public void testCompilingDecimal()
	{
		Coordinate coordinate = new Coordinate(3, 67);
		Variables variables = new LocalVariables(1, new HashMap<>(), new HashMap<>());

		Num literal = new Num(coordinate, "0.5");

		Type type = literal.getType(variables);
		Result result = literal.compile(variables);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(0.5, literal.getValue());
		assertEquals(1, instructions.length());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(0.5, firstInstruction.cst);

		assertEquals("java/lang/Double", type.toFullyQualifiedType());
	}

	@Test
	public void testCompilingIntegerTwo()
	{
		Coordinate coordinate = new Coordinate(5, 76);
		Variables variables = new LocalVariables(1, new HashMap<>(), new HashMap<>());

		Num literal = new Num(coordinate, "2");

		Type type = literal.getType(variables);
		Result result = literal.compileCondition(variables, null, null);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(0, result.getErrors().length());
		assertEquals(2L, literal.getValue());
		assertEquals(1, instructions.length());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(2L, firstInstruction.cst);
		assertEquals("java/lang/Long", type.toFullyQualifiedType());
		assertEquals(coordinate, literal.getCoordinate());
	}
}
