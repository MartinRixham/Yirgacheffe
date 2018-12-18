package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.statement.VariableWrite;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class VariableReadTest
{
	@Test
	public void testCompilingStringRead()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();
		Type owner = new ReferenceType(String.class);

		variables.declare("myVariable", owner);

		Coordinate coordinate = new Coordinate(1, 0);
		Expression expression = new VariableRead("myVariable", coordinate);

		Type type = expression.getType(variables);

		expression.compile(methodVisitor, variables);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(1, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);

		assertEquals("java.lang.String", type.toFullyQualifiedType());
	}

	@Test
	public void testCompilingNumberRead()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();
		Type owner = PrimitiveType.DOUBLE;

		variables.declare("myVariable", owner);

		Coordinate coordinate = new Coordinate(1, 0);
		Expression expression = new VariableRead("myVariable", coordinate);

		Type type = expression.getType(variables);

		expression.compile(methodVisitor, variables);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(1, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.DLOAD, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);

		assertEquals("java.lang.Double", type.toFullyQualifiedType());
	}

	@Test
	public void testEqualVariables()
	{
		Coordinate coordinate = new Coordinate(1, 0);
		VariableRead firstVariable = new VariableRead("myVariable", coordinate);
		VariableRead secondVariable = new VariableRead("myVariable", coordinate);

		assertEquals(firstVariable, secondVariable);
		assertEquals(firstVariable.hashCode(), secondVariable.hashCode());
	}

	@Test
	public void testNotEqualToObject()
	{
		Coordinate coordinate = new Coordinate(1, 0);
		VariableRead firstVariable = new VariableRead("myVariable", coordinate);
		Object secondVariable = new Object();

		assertNotEquals(firstVariable, secondVariable);
		assertNotEquals(firstVariable.hashCode(), secondVariable.hashCode());
	}

	@Test
	public void testEqualToVariableWrite()
	{
		Coordinate coordinate = new Coordinate(1, 0);
		Literal string = new Literal(new ReferenceType(String.class), "\"my string\"");
		VariableRead variableRead = new VariableRead("myVar", coordinate);
		VariableWrite variableWrite = new VariableWrite("myVar", string, coordinate);

		assertEquals(variableRead, variableWrite);
		assertEquals(variableRead.hashCode(), variableWrite.hashCode());
	}
}
