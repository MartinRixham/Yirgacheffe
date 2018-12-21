package yirgacheffe.compiler.statement;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.junit.Test;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.InvalidExpression;
import yirgacheffe.compiler.expression.Literal;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class VariableWriteTest
{
	@Test
	public void testVariableWrite()
	{
		Signature caller = new Signature("method", new Array<>());
		Expression value = new Literal(new ReferenceType(String.class), "\"sumpt\"");
		Coordinate coordinate = new Coordinate(4, 2);
		VariableWrite variableWrite =
			new VariableWrite(coordinate, "myVariable", value);
		MethodNode methodVisitor = new MethodNode();

		Variables variables = new Variables();
		variables.declare("myVariable", new ReferenceType(String.class));

		Array<Error> errors = variableWrite.compile(methodVisitor, variables, caller);

		assertEquals(0, errors.length());

		InsnList instructions =  methodVisitor.instructions;

		assertEquals(2, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals("sumpt", firstInstruction.cst);

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.ASTORE, secondInstruction.getOpcode());
		assertEquals(1, secondInstruction.var);
	}

	@Test
	public void testWriteInvalidExpression()
	{
		Signature caller = new Signature("method", new Array<>());
		Coordinate coordinate = new Coordinate(4, 2);
		Type string = new ReferenceType(String.class);
		Expression value = new InvalidExpression(string);
		VariableWrite variableWrite =
			new VariableWrite(coordinate, "myVariable", value);
		MethodNode methodVisitor = new MethodNode();

		Variables variables = new Variables();
		variables.declare("myVariable", string);

		Array<Error> errors = variableWrite.compile(methodVisitor, variables, caller);

		assertEquals(1, errors.length());
	}

	@Test
	public void testEqualVariables()
	{
		Coordinate coordinate = new Coordinate(1, 0);
		Literal firstString = new Literal(new ReferenceType(String.class), "\"string\"");
		Literal secondString = new Literal(new ReferenceType(String.class), "\"thingy\"");
		VariableWrite firstVariable = new VariableWrite(coordinate, "var", firstString);
		VariableWrite secondVariable = new VariableWrite(coordinate, "var", secondString);

		assertEquals(firstVariable, secondVariable);
		assertEquals(firstVariable.hashCode(), secondVariable.hashCode());
	}

	@Test
	public void testNotEqualToString()
	{
		Coordinate coordinate = new Coordinate(1, 0);
		Literal firstString = new Literal(new ReferenceType(String.class), "\"string\"");
		VariableWrite firstVariable = new VariableWrite(coordinate, "var", firstString);
		Object secondVariable = new Object();

		assertNotEquals(firstVariable, secondVariable);
		assertNotEquals(firstVariable.hashCode(), secondVariable.hashCode());
	}

	@Test
	public void testEqualToVariableRead()
	{
		Coordinate coordinate = new Coordinate(1, 0);
		Literal string = new Literal(new ReferenceType(String.class), "\"my string\"");
		VariableWrite variableWrite = new VariableWrite(coordinate, "myVar", string);
		VariableRead variableRead = new VariableRead("myVar", coordinate);

		assertEquals(variableWrite, variableRead);
		assertEquals(variableWrite.hashCode(), variableRead.hashCode());
	}

	@Test
	public void testFirstOperandIsNothing()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		VariableWrite variableWrite = new VariableWrite(coordinate, "var", new Nothing());

		Expression operand = variableWrite.getFirstOperand();

		assertTrue(operand instanceof Nothing);
		assertFalse(variableWrite.isEmpty());
	}

	@Test
	public void testGetVariables()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		VariableWrite variableWrite = new VariableWrite(coordinate, "var", new Nothing());

		Array<VariableRead> reads = variableWrite.getVariableReads();

		assertEquals(0, reads.length());

		Array<VariableWrite> writes = variableWrite.getVariableWrites();

		assertTrue(writes.indexOf(variableWrite) >= 0);
	}
}
