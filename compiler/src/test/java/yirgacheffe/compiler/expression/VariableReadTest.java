package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.statement.VariableWrite;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

public class VariableReadTest
{
	@Test
	public void testCompilingStringRead()
	{
		Variables variables = new LocalVariables(new HashMap<>());
		Type owner = new ReferenceType(String.class);

		variables.declare("myVariable", owner);

		Coordinate coordinate = new Coordinate(1, 0);
		Expression expression = new VariableRead(coordinate, "myVariable");

		Type type = expression.getType(variables);
		Result result = expression.compile(variables);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertFalse(expression.isCondition(variables));
		assertEquals(1, instructions.length());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);

		assertEquals("java/lang/String", type.toFullyQualifiedType());
	}

	@Test
	public void testCompilingNumberRead()
	{
		Variables variables = new LocalVariables(new HashMap<>());
		Type owner = PrimitiveType.DOUBLE;

		variables.declare("myVariable", owner);

		Coordinate coordinate = new Coordinate(1, 0);
		Expression expression = new VariableRead(coordinate, "myVariable");

		Type type = expression.getType(variables);
		Result result = expression.compile(variables);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(1, instructions.length());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.DLOAD, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);

		assertEquals("java/lang/Double", type.toFullyQualifiedType());
	}

	@Test
	public void testCompilingIntegerRead()
	{
		Variables variables = new LocalVariables(new HashMap<>());
		Type owner = PrimitiveType.INT;

		variables.declare("myVariable", owner);

		Coordinate coordinate = new Coordinate(1, 0);
		Expression expression = new VariableRead(coordinate, "myVariable");

		Type type = expression.getType(variables);
		Result result = expression.compileCondition(variables, null, null);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(0, result.getErrors().length());
		assertEquals(1, instructions.length());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ILOAD, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);

		assertEquals("java/lang/Integer", type.toFullyQualifiedType());
	}

	@Test
	public void testEqualVariables()
	{
		Coordinate coordinate = new Coordinate(1, 0);
		VariableRead firstVariable = new VariableRead(coordinate, "myVariable");
		VariableRead secondVariable = new VariableRead(coordinate, "myVariable");

		assertEquals(firstVariable, secondVariable);
		assertEquals(firstVariable.hashCode(), secondVariable.hashCode());
	}

	@Test
	public void testNotEqualToObject()
	{
		Coordinate coordinate = new Coordinate(1, 0);
		VariableRead firstVariable = new VariableRead(coordinate, "myVariable");
		Object secondVariable = new Object();

		assertNotEquals(firstVariable, secondVariable);
		assertNotEquals(firstVariable.hashCode(), secondVariable.hashCode());
	}

	@Test
	public void testEqualToVariableWrite()
	{
		Coordinate coordinate = new Coordinate(1, 0);
		Streeng string = new Streeng("\"my string\"");
		VariableRead variableRead = new VariableRead(coordinate, "myVar");
		VariableWrite variableWrite = new VariableWrite(coordinate, "myVar", string);

		assertEquals(variableRead, variableWrite);
		assertEquals(variableRead.hashCode(), variableWrite.hashCode());
	}
}
