package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
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
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());
		Coordinate coordinate = new Coordinate(3, 6);
		This operand = new This(new ReferenceType(String.class));

		UnaryOperation postincrement =
			new UnaryOperation(coordinate, operand, false, true);

		Type type = postincrement.getType(variables);

		Array<Error> errors =
			postincrement.compileCondition(methodVisitor, variables, new Label());

		assertFalse(postincrement.isCondition(variables));
		assertEquals(1, errors.length());

		assertEquals(errors.get(0).toString(),
			"line 3:6 Cannot increment java.lang.String.");
	}

	@Test
	public void testPostincrementStatementOfWrongType()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());
		Coordinate coordinate = new Coordinate(3, 6);
		This operand = new This(new ReferenceType(String.class));

		UnaryOperation postincrement =
			new UnaryOperation(coordinate, operand, false, true);

		Signature caller = new Signature(new NullType(), "method", new Array<>());

		Array<Error> errors = postincrement.compile(methodVisitor, variables, caller);

		assertEquals(1, errors.length());

		assertEquals(errors.get(0).toString(),
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
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());

		variables.declare("variable", PrimitiveType.INT);

		Coordinate coordinate = new Coordinate(3, 6);
		VariableRead variableRead = new VariableRead(coordinate, "variable");

		UnaryOperation postincrement =
			new UnaryOperation(coordinate, variableRead, false, true);

		Signature caller = new Signature(new NullType(), "method", new Array<>());

		Type type = postincrement.getType(variables);

		Array<Error> errors = postincrement.compile(methodVisitor, variables, caller);

		assertEquals(PrimitiveType.INT, type);
		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		IincInsnNode firstInstruction = (IincInsnNode) instructions.get(0);

		assertEquals(Opcodes.IINC, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);
		assertEquals(1, firstInstruction.incr);
	}
}
