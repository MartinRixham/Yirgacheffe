package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.comparison.Comparison;
import yirgacheffe.compiler.comparison.Equals;
import yirgacheffe.compiler.comparison.GreaterThan;
import yirgacheffe.compiler.comparison.NotEquals;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EquationTest
{
	@Test
	public void testCompilingEqualDoubles()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();
		Num firstOperand = new Num("3.0");
		Num secondOperand = new Num("2.0");
		Comparison equals = new Equals();

		Equation equation = new Equation(firstOperand, secondOperand, equals);

		Type type = equation.getType(variables);

		Array<Error> errors = equation.compile(methodVisitor, variables);

		assertEquals(PrimitiveType.BOOLEAN, type);
		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(9, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(3.0, firstInstruction.cst);

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(Opcodes.LDC, secondInstruction.getOpcode());
		assertEquals(2.0, secondInstruction.cst);

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DCMPL, thirdInstruction.getOpcode());

		JumpInsnNode fourthInstruction = (JumpInsnNode) instructions.get(3);
		Label trueLabel = fourthInstruction.label.getLabel();

		assertEquals(Opcodes.IFNE, fourthInstruction.getOpcode());

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.ICONST_1, fifthInstruction.getOpcode());

		JumpInsnNode sixthInstruction = (JumpInsnNode) instructions.get(5);

		assertEquals(Opcodes.GOTO, sixthInstruction.getOpcode());
		Label falseLabel = sixthInstruction.label.getLabel();

		LabelNode seventhInstruction = (LabelNode) instructions.get(6);

		assertEquals(trueLabel, seventhInstruction.getLabel());

		InsnNode eightInstruction = (InsnNode) instructions.get(7);

		assertEquals(Opcodes.ICONST_0, eightInstruction.getOpcode());

		LabelNode ninthInstruction = (LabelNode) instructions.get(8);

		assertEquals(falseLabel, ninthInstruction.getLabel());
	}

	@Test
	public void testCompilingNotEqualDoubles()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();
		Num firstOperand = new Num("3.0");
		Num secondOperand = new Num("2.0");
		Comparison notEquals = new NotEquals();

		Equation equation = new Equation(firstOperand, secondOperand, notEquals);

		Type type = equation.getType(variables);

		Array<Error> errors = equation.compile(methodVisitor, variables);

		assertEquals(PrimitiveType.BOOLEAN, type);
		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(9, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(3.0, firstInstruction.cst);

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(Opcodes.LDC, secondInstruction.getOpcode());
		assertEquals(2.0, secondInstruction.cst);

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DCMPL, thirdInstruction.getOpcode());

		JumpInsnNode fourthInstruction = (JumpInsnNode) instructions.get(3);
		Label trueLabel = fourthInstruction.label.getLabel();

		assertEquals(Opcodes.IFEQ, fourthInstruction.getOpcode());

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.ICONST_1, fifthInstruction.getOpcode());

		JumpInsnNode sixthInstruction = (JumpInsnNode) instructions.get(5);

		assertEquals(Opcodes.GOTO, sixthInstruction.getOpcode());
		Label falseLabel = sixthInstruction.label.getLabel();

		LabelNode seventhInstruction = (LabelNode) instructions.get(6);

		assertEquals(trueLabel, seventhInstruction.getLabel());

		InsnNode eightInstruction = (InsnNode) instructions.get(7);

		assertEquals(Opcodes.ICONST_0, eightInstruction.getOpcode());

		LabelNode ninthInstruction = (LabelNode) instructions.get(8);

		assertEquals(falseLabel, ninthInstruction.getLabel());
	}

	@Test
	public void testCompilingEqualBooleans()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();
		Bool firstOperand = new Bool("true");
		Bool secondOperand = new Bool("false");
		Comparison equals = new Equals();

		Equation equation = new Equation(firstOperand, secondOperand, equals);

		Type type = equation.getType(variables);

		Array<Error> errors = equation.compile(methodVisitor, variables);

		assertEquals(PrimitiveType.BOOLEAN, type);
		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(8, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_1, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.ICONST_0, secondInstruction.getOpcode());

		JumpInsnNode thirdInstruction = (JumpInsnNode) instructions.get(2);
		Label trueLabel = thirdInstruction.label.getLabel();

		assertEquals(Opcodes.IF_ICMPNE, thirdInstruction.getOpcode());

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.ICONST_1, fourthInstruction.getOpcode());

		JumpInsnNode fifthInstruction = (JumpInsnNode) instructions.get(4);

		assertEquals(Opcodes.GOTO, fifthInstruction.getOpcode());
		Label falseLabel = fifthInstruction.label.getLabel();

		LabelNode sixthInstruction = (LabelNode) instructions.get(5);

		assertEquals(trueLabel, sixthInstruction.getLabel());

		InsnNode seventhInstruction = (InsnNode) instructions.get(6);

		assertEquals(Opcodes.ICONST_0, seventhInstruction.getOpcode());

		LabelNode eightInstruction = (LabelNode) instructions.get(7);

		assertEquals(falseLabel, eightInstruction.getLabel());
	}

	@Test
	public void testCompilingEqualIntegers()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();
		Num firstOperand = new Num("1");
		Num secondOperand = new Num("0");
		Comparison equals = new Equals();

		Equation equation = new Equation(firstOperand, secondOperand, equals);

		Type type = equation.getType(variables);

		Array<Error> errors = equation.compile(methodVisitor, variables);

		assertEquals(PrimitiveType.BOOLEAN, type);
		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(8, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_1, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.ICONST_0, secondInstruction.getOpcode());

		JumpInsnNode thirdInstruction = (JumpInsnNode) instructions.get(2);
		Label trueLabel = thirdInstruction.label.getLabel();

		assertEquals(Opcodes.IF_ICMPNE, thirdInstruction.getOpcode());

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.ICONST_1, fourthInstruction.getOpcode());

		JumpInsnNode fifthInstruction = (JumpInsnNode) instructions.get(4);

		assertEquals(Opcodes.GOTO, fifthInstruction.getOpcode());
		Label falseLabel = fifthInstruction.label.getLabel();

		LabelNode sixthInstruction = (LabelNode) instructions.get(5);

		assertEquals(trueLabel, sixthInstruction.getLabel());

		InsnNode seventhInstruction = (InsnNode) instructions.get(6);

		assertEquals(Opcodes.ICONST_0, seventhInstruction.getOpcode());

		LabelNode eightInstruction = (LabelNode) instructions.get(7);

		assertEquals(falseLabel, eightInstruction.getLabel());
	}

	@Test
	public void testCompilingNotEqualBooleans()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();
		Bool firstOperand = new Bool("true");
		Bool secondOperand = new Bool("false");
		Comparison notEquals = new NotEquals();

		Equation equation = new Equation(firstOperand, secondOperand, notEquals);

		Type type = equation.getType(variables);

		Array<Error> errors = equation.compile(methodVisitor, variables);

		assertEquals(PrimitiveType.BOOLEAN, type);
		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(8, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_1, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.ICONST_0, secondInstruction.getOpcode());

		JumpInsnNode thirdInstruction = (JumpInsnNode) instructions.get(2);
		Label trueLabel = thirdInstruction.label.getLabel();

		assertEquals(Opcodes.IF_ICMPEQ, thirdInstruction.getOpcode());

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.ICONST_1, fourthInstruction.getOpcode());

		JumpInsnNode fifthInstruction = (JumpInsnNode) instructions.get(4);

		assertEquals(Opcodes.GOTO, fifthInstruction.getOpcode());
		Label falseLabel = fifthInstruction.label.getLabel();

		LabelNode sixthInstruction = (LabelNode) instructions.get(5);

		assertEquals(trueLabel, sixthInstruction.getLabel());

		InsnNode seventhInstruction = (InsnNode) instructions.get(6);

		assertEquals(Opcodes.ICONST_0, seventhInstruction.getOpcode());

		LabelNode eightInstruction = (LabelNode) instructions.get(7);

		assertEquals(falseLabel, eightInstruction.getLabel());
	}

	@Test
	public void testGettingVariableReads()
	{
		Coordinate coordinate = new Coordinate(3, 6);
		VariableRead firstOperand = new VariableRead(coordinate, "myVariable");
		VariableRead secondOperand = new VariableRead(coordinate, "myVariable");

		Expression operation =
			new Equation(firstOperand, secondOperand, new GreaterThan());

		Array<VariableRead> reads = operation.getVariableReads();

		assertTrue(reads.indexOf(firstOperand) >= 0);
		assertTrue(reads.indexOf(secondOperand) >= 0);
	}

	@Test
	public void testCompilingIntegerEqualsDouble()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();
		Num firstOperand = new Num("3");
		Num secondOperand = new Num("2.0");
		Comparison equals = new Equals();

		Equation equation = new Equation(firstOperand, secondOperand, equals);

		Type type = equation.getType(variables);

		Array<Error> errors = equation.compile(methodVisitor, variables);

		assertEquals(PrimitiveType.BOOLEAN, type);
		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(10, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(3, firstInstruction.cst);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.I2D, secondInstruction.getOpcode());

		LdcInsnNode thirdInstruction = (LdcInsnNode) instructions.get(2);

		assertEquals(Opcodes.LDC, thirdInstruction.getOpcode());
		assertEquals(2.0, thirdInstruction.cst);

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.DCMPL, fourthInstruction.getOpcode());

		JumpInsnNode fifthInstruction = (JumpInsnNode) instructions.get(4);
		Label trueLabel = fifthInstruction.label.getLabel();

		assertEquals(Opcodes.IFNE, fifthInstruction.getOpcode());

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.ICONST_1, sixthInstruction.getOpcode());

		JumpInsnNode seventhInstruction = (JumpInsnNode) instructions.get(6);

		assertEquals(Opcodes.GOTO, seventhInstruction.getOpcode());
		Label falseLabel = seventhInstruction.label.getLabel();

		LabelNode eightInstruction = (LabelNode) instructions.get(7);

		assertEquals(trueLabel, eightInstruction.getLabel());

		InsnNode ninthInstruction = (InsnNode) instructions.get(8);

		assertEquals(Opcodes.ICONST_0, ninthInstruction.getOpcode());

		LabelNode tenthInstruction = (LabelNode) instructions.get(9);

		assertEquals(falseLabel, tenthInstruction.getLabel());
	}

	@Test
	public void testCompilingDoubleEqualsInteger()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();
		Num firstOperand = new Num("3.0");
		Num secondOperand = new Num("2");
		Comparison equals = new Equals();

		Equation equation = new Equation(firstOperand, secondOperand, equals);

		Type type = equation.getType(variables);

		Array<Error> errors = equation.compile(methodVisitor, variables);

		assertEquals(PrimitiveType.BOOLEAN, type);
		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(10, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(3.0, firstInstruction.cst);

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(Opcodes.LDC, secondInstruction.getOpcode());
		assertEquals(2, secondInstruction.cst);

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.I2D, thirdInstruction.getOpcode());

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.DCMPL, fourthInstruction.getOpcode());

		JumpInsnNode fifthInstruction = (JumpInsnNode) instructions.get(4);
		Label trueLabel = fifthInstruction.label.getLabel();

		assertEquals(Opcodes.IFNE, fifthInstruction.getOpcode());

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.ICONST_1, sixthInstruction.getOpcode());

		JumpInsnNode seventhInstruction = (JumpInsnNode) instructions.get(6);

		assertEquals(Opcodes.GOTO, seventhInstruction.getOpcode());
		Label falseLabel = seventhInstruction.label.getLabel();

		LabelNode eightInstruction = (LabelNode) instructions.get(7);

		assertEquals(trueLabel, eightInstruction.getLabel());

		InsnNode ninthInstruction = (InsnNode) instructions.get(8);

		assertEquals(Opcodes.ICONST_0, ninthInstruction.getOpcode());

		LabelNode tenthInstruction = (LabelNode) instructions.get(9);

		assertEquals(falseLabel, tenthInstruction.getLabel());
	}
}
