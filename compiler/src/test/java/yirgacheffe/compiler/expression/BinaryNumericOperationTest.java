package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BinaryNumericOperationTest
{
	@Test
	public void testCompilingIntegerAddition()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();
		Coordinate coordinate = new Coordinate(3,  6);
		Num firstOperand = new Num("3");
		Num secondOperand = new Num("2");

		BinaryNumericOperation operation =
			new BinaryNumericOperation(
				coordinate,
				Operator.ADD,
				firstOperand, secondOperand);

		Type type = operation.getType(variables);

		Array<Error> errors = operation.compile(methodVisitor, variables);

		assertEquals(PrimitiveType.INT, type);
		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(3, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(3, firstInstruction.cst);

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(Opcodes.LDC, secondInstruction.getOpcode());
		assertEquals(2, secondInstruction.cst);

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.IADD, thirdInstruction.getOpcode());
	}

	@Test
	public void testCompilingFloatAddition()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();
		Coordinate coordinate = new Coordinate(3,  6);
		Num firstOperand = new Num("3");
		Num secondOperand = new Num("2.0");

		BinaryNumericOperation operation =
			new BinaryNumericOperation(
				coordinate,
				Operator.ADD,
				firstOperand, secondOperand);

		Type type = operation.getType(variables);

		Array<Error> errors = operation.compile(methodVisitor, variables);

		assertEquals(PrimitiveType.DOUBLE, type);
		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(4, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(3, firstInstruction.cst);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.I2D, secondInstruction.getOpcode());

		LdcInsnNode thirdInstruction = (LdcInsnNode) instructions.get(2);

		assertEquals(Opcodes.LDC, thirdInstruction.getOpcode());
		assertEquals(2.0, thirdInstruction.cst);

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.DADD, fourthInstruction.getOpcode());
	}

	@Test
	public void testCompilingFloatAdditionWithCastOfSecondOperand()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();
		Coordinate coordinate = new Coordinate(3,  6);
		Num firstOperand = new Num("3.0");
		Num secondOperand = new Num("2");

		BinaryNumericOperation operation =
			new BinaryNumericOperation(
				coordinate,
				Operator.ADD,
				firstOperand, secondOperand);

		Type type = operation.getType(variables);

		Array<Error> errors = operation.compile(methodVisitor, variables);

		assertEquals(PrimitiveType.DOUBLE, type);
		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(4, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(3.0, firstInstruction.cst);

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(Opcodes.LDC, secondInstruction.getOpcode());
		assertEquals(2, secondInstruction.cst);

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.I2D, thirdInstruction.getOpcode());

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.DADD, fourthInstruction.getOpcode());
	}

	@Test
	public void testAdditionOfWrongType()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();
		Coordinate coordinate = new Coordinate(3,  6);
		Num firstOperand = new Num("3.0");
		This secondOperand = new This(new ReferenceType(String.class));

		BinaryNumericOperation operation =
			new BinaryNumericOperation(
				coordinate,
				Operator.ADD,
				firstOperand, secondOperand);

		Type type = operation.getType(variables);

		Array<Error> errors = operation.compile(methodVisitor, variables);

		assertEquals(PrimitiveType.DOUBLE, type);
		assertEquals(1, errors.length());

		assertEquals(errors.get(0).toString(),
			"line 3:6 Cannot add Num and java.lang.String.");
	}

	@Test
	public void testInvalidOperands()
	{
		Coordinate coordinate = new Coordinate(3, 6);
		Expression firstOperand = new InvalidExpression(PrimitiveType.DOUBLE);
		Expression secondOperand = new InvalidExpression(PrimitiveType.DOUBLE);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Expression expression =
			new BinaryNumericOperation(
				coordinate,
				Operator.ADD,
				firstOperand,
				secondOperand);

		Array<Error> errors = expression.compile(methodVisitor, variables);

		assertEquals(2, errors.length());
	}

	@Test
	public void testGettingVariableReads()
	{
		Coordinate coordinate = new Coordinate(3, 6);
		VariableRead firstOperand = new VariableRead(coordinate, "myVariable");
		VariableRead secondOperand = new VariableRead(coordinate, "myVariable");

		Expression operation =
			new BinaryNumericOperation(
				coordinate,
				Operator.DIVIDE,
				firstOperand,
				secondOperand);

		Array<VariableRead> reads = operation.getVariableReads();

		assertTrue(reads.indexOf(firstOperand) >= 0);
		assertTrue(reads.indexOf(secondOperand) >= 0);
	}
}
