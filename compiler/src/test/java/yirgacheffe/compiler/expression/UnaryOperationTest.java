package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UnaryOperationTest
{
	@Test
	public void testPostincrementOfWrongType()
	{
		Variables variables = new LocalVariables(new HashMap<>());
		Coordinate coordinate = new Coordinate(3, 6);
		This operand = new This(new ReferenceType(String.class));

		UnaryOperation postincrement =
			new UnaryOperation(coordinate, operand, false, true);

		Result result = postincrement.compileCondition(variables, null, null);

		assertFalse(postincrement.isCondition(variables));
		assertEquals(1, result.getErrors().length());

		assertEquals(result.getErrors().get(0).toString(),
			"line 3:6 Cannot increment java.lang.String.");
	}

	@Test
	public void testPostincrementStatementOfWrongType()
	{
		LocalVariables variables = new LocalVariables(new HashMap<>());
		Coordinate coordinate = new Coordinate(3, 6);
		This operand = new This(new ReferenceType(String.class));

		UnaryOperation postincrement =
			new UnaryOperation(coordinate, operand, false, true);

		Signature caller = new Signature(new NullType(), "method", new Array<>());

		Result result = postincrement.compile(variables, caller);

		assertEquals(1, result.getErrors().length());

		assertEquals(result.getErrors().get(0).toString(),
			"line 3:6 Cannot increment java.lang.String.");
	}

	@Test
	public void testGettingVariableReads()
	{
		Coordinate coordinate = new Coordinate(3, 6);
		VariableRead read = new VariableRead(coordinate, "myVariable");

		UnaryOperation postincrement = new UnaryOperation(coordinate, read, false, true);

		Array<VariableRead> reads = postincrement.getVariableReads();

		assertTrue(reads.indexOf(read) >= 0);
		assertEquals(read, postincrement.getExpression());
		assertFalse(postincrement.isEmpty());
	}

	@Test
	public void testPostincrementInteger()
	{
		LocalVariables variables = new LocalVariables(new HashMap<>());

		variables.declare("variable", PrimitiveType.INT);

		Coordinate coordinate = new Coordinate(3, 6);
		VariableRead variableRead = new VariableRead(coordinate, "variable");

		UnaryOperation postincrement =
			new UnaryOperation(coordinate, variableRead, false, true);

		Signature caller = new Signature(new NullType(), "method", new Array<>());

		Type type = postincrement.getType(variables);

		Result result = postincrement.compile(variables, caller);

		assertEquals(PrimitiveType.INT, type);
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		IincInsnNode firstInstruction = (IincInsnNode) instructions.get(0);

		assertEquals(Opcodes.IINC, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);
		assertEquals(1, firstInstruction.incr);
	}
}
