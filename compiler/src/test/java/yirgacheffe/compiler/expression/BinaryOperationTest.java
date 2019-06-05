package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.operator.Operator;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BinaryOperationTest
{
	@Test
	public void testCompilingIntegerAddition()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());
		Coordinate coordinate = new Coordinate(3,  6);
		Num firstOperand = new Num("0");
		Num secondOperand = new Num("1");

		BinaryOperation operation =
			new BinaryOperation(
				coordinate,
				Operator.ADD,
				firstOperand, secondOperand);

		Type type = operation.getType(variables);

		Array<Error> errors = operation.compile(methodVisitor, variables);

		assertEquals(PrimitiveType.INT, type);
		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(3, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_0, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.ICONST_1, secondInstruction.getOpcode());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.IADD, thirdInstruction.getOpcode());
	}

	@Test
	public void testCompilingLongIntegerAddition()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());
		Coordinate coordinate = new Coordinate(3,  6);
		Num firstOperand = new Num("3");
		Num secondOperand = new Num("2");

		BinaryOperation operation =
			new BinaryOperation(
				coordinate,
				Operator.ADD,
				firstOperand, secondOperand);

		Type type = operation.getType(variables);

		Array<Error> errors = operation.compile(methodVisitor, variables);

		assertEquals(PrimitiveType.LONG, type);
		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(3, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(3L, firstInstruction.cst);

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(Opcodes.LDC, secondInstruction.getOpcode());
		assertEquals(2L, secondInstruction.cst);

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.LADD, thirdInstruction.getOpcode());
	}

	@Test
	public void testAddIntegerAndLongInteger()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());
		Coordinate coordinate = new Coordinate(3,  6);
		Num firstOperand = new Num("1");
		Num secondOperand = new Num("2");

		BinaryOperation operation =
			new BinaryOperation(
				coordinate,
				Operator.ADD,
				firstOperand, secondOperand);

		Type type = operation.getType(variables);

		Array<Error> errors = operation.compile(methodVisitor, variables);

		assertEquals(PrimitiveType.LONG, type);
		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(4, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_1, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.I2L, secondInstruction.getOpcode());

		LdcInsnNode thirdInstruction = (LdcInsnNode) instructions.get(2);

		assertEquals(Opcodes.LDC, thirdInstruction.getOpcode());
		assertEquals(2L, thirdInstruction.cst);

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.LADD, fourthInstruction.getOpcode());
	}

	@Test
	public void testCompilingFloatAddition()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());
		Coordinate coordinate = new Coordinate(3,  6);
		Num firstOperand = new Num("3");
		Num secondOperand = new Num("2.0");

		BinaryOperation operation =
			new BinaryOperation(
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
		assertEquals(3L, firstInstruction.cst);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.L2D, secondInstruction.getOpcode());

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
		Variables variables = new Variables(new HashMap<>());
		Coordinate coordinate = new Coordinate(3,  6);
		Num firstOperand = new Num("3.0");
		Num secondOperand = new Num("2");

		BinaryOperation operation =
			new BinaryOperation(
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
		assertEquals(2L, secondInstruction.cst);

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.L2D, thirdInstruction.getOpcode());

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.DADD, fourthInstruction.getOpcode());
	}

	@Test
	public void testAdditionOfWrongType()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());
		Coordinate coordinate = new Coordinate(3,  6);
		Num firstOperand = new Num("3.0");
		This secondOperand = new This(new ReferenceType(Object.class));

		BinaryOperation operation =
			new BinaryOperation(
				coordinate,
				Operator.ADD,
				firstOperand, secondOperand);

		Type type = operation.getType(variables);

		Array<Error> errors = operation.compile(methodVisitor, variables);

		assertEquals(PrimitiveType.DOUBLE, type);
		assertEquals(1, errors.length());

		assertEquals(errors.get(0).toString(),
			"line 3:6 Cannot add Num and java.lang.Object.");
	}

	@Test
	public void testInvalidOperands()
	{
		Coordinate coordinate = new Coordinate(3, 6);
		Expression firstOperand = new InvalidExpression(PrimitiveType.DOUBLE);
		Expression secondOperand = new InvalidExpression(PrimitiveType.DOUBLE);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());

		Expression expression =
			new BinaryOperation(
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
			new BinaryOperation(
				coordinate,
				Operator.DIVIDE,
				firstOperand,
				secondOperand);

		Array<VariableRead> reads = operation.getVariableReads();

		assertTrue(reads.indexOf(firstOperand) >= 0);
		assertTrue(reads.indexOf(secondOperand) >= 0);
	}
}
