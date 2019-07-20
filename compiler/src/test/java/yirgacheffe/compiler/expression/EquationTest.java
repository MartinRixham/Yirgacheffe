package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.comparison.Comparator;
import yirgacheffe.compiler.comparison.Equals;
import yirgacheffe.compiler.comparison.GreaterThan;
import yirgacheffe.compiler.comparison.NotEquals;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EquationTest
{
	@Test
	public void testCompilingEqualDoubles()
	{
		Coordinate coordinate = new Coordinate(3, 6);
		Variables variables = new LocalVariables(new HashMap<>());
		Num firstOperand = new Num("3.0");
		Num secondOperand = new Num("2.0");
		Comparator equals = new Equals();

		Equation equation = new Equation(coordinate, equals, firstOperand, secondOperand);

		Type type = equation.getType(variables);
		Result result = equation.compile(variables);

		assertTrue(equation.isCondition(variables));
		assertEquals(PrimitiveType.BOOLEAN, type);
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(9, instructions.length());

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
		Coordinate coordinate = new Coordinate(3, 6);
		Variables variables = new LocalVariables(new HashMap<>());
		Num firstOperand = new Num("3.0");
		Num secondOperand = new Num("2.0");
		Comparator notEquals = new NotEquals();

		Equation equation =
			new Equation(coordinate, notEquals, firstOperand, secondOperand);

		Type type = equation.getType(variables);
		Result result = equation.compile(variables);

		assertEquals(PrimitiveType.BOOLEAN, type);
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(9, instructions.length());

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
		Coordinate coordinate = new Coordinate(3, 6);
		Variables variables = new LocalVariables(new HashMap<>());
		Bool firstOperand = new Bool("true");
		Bool secondOperand = new Bool("false");
		Comparator equals = new Equals();

		Equation equation = new Equation(coordinate, equals, firstOperand, secondOperand);

		Type type = equation.getType(variables);
		Result result = equation.compile(variables);

		assertEquals(PrimitiveType.BOOLEAN, type);
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(8, instructions.length());

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
		Coordinate coordinate = new Coordinate(3, 6);
		Variables variables = new LocalVariables(new HashMap<>());
		Num firstOperand = new Num("1");
		Num secondOperand = new Num("0");
		Comparator equals = new Equals();

		Equation equation = new Equation(coordinate, equals, firstOperand, secondOperand);

		Type type = equation.getType(variables);
		Result result = equation.compile(variables);

		assertEquals(PrimitiveType.BOOLEAN, type);
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(8, instructions.length());

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
		Coordinate coordinate = new Coordinate(3, 6);
		Variables variables = new LocalVariables(new HashMap<>());
		Bool firstOperand = new Bool("true");
		Bool secondOperand = new Bool("false");
		Comparator notEquals = new NotEquals();

		Equation equation =
			new Equation(coordinate, notEquals, firstOperand, secondOperand);

		Type type = equation.getType(variables);
		Result result = equation.compile(variables);

		assertEquals(PrimitiveType.BOOLEAN, type);
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(8, instructions.length());

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
		Comparator greaterThan = new GreaterThan();

		Expression operation =
			new Equation(coordinate, greaterThan, firstOperand, secondOperand);

		Array<VariableRead> reads = operation.getVariableReads();

		assertTrue(reads.indexOf(firstOperand) >= 0);
		assertTrue(reads.indexOf(secondOperand) >= 0);
	}

	@Test
	public void testCompilingIntegerEqualsDouble()
	{
		Coordinate coordinate = new Coordinate(3, 6);
		Variables variables = new LocalVariables(new HashMap<>());
		Num firstOperand = new Num("3");
		Num secondOperand = new Num("2.0");
		Comparator equals = new Equals();

		Equation equation = new Equation(coordinate, equals, firstOperand, secondOperand);

		Type type = equation.getType(variables);
		Result result = equation.compile(variables);

		assertEquals(PrimitiveType.BOOLEAN, type);
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(10, instructions.length());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(3L, firstInstruction.cst);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.L2D, secondInstruction.getOpcode());

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
		Coordinate coordinate = new Coordinate(3, 6);
		Variables variables = new LocalVariables(new HashMap<>());
		Num firstOperand = new Num("3.0");
		Num secondOperand = new Num("2");
		Comparator equals = new Equals();

		Equation equation = new Equation(coordinate, equals, firstOperand, secondOperand);

		Type type = equation.getType(variables);
		Result result = equation.compile(variables);

		assertEquals(PrimitiveType.BOOLEAN, type);
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(10, instructions.length());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(3.0, firstInstruction.cst);

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(Opcodes.LDC, secondInstruction.getOpcode());
		assertEquals(2L, secondInstruction.cst);

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.L2D, thirdInstruction.getOpcode());

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
	public void testEquationOfWrongType()
	{
		Variables variables = new LocalVariables(new HashMap<>());
		Coordinate coordinate = new Coordinate(3,  6);
		Num firstOperand = new Num("3.0");
		This secondOperand = new This(new ReferenceType(Object.class));
		Comparator greaterThan = new GreaterThan();

		Equation operation =
			new Equation(coordinate, greaterThan, firstOperand, secondOperand);

		Type type = operation.getType(variables);
		Result result = operation.compile(variables);

		assertEquals(PrimitiveType.BOOLEAN, type);
		assertEquals(1, result.getErrors().length());

		assertEquals(result.getErrors().get(0).toString(),
			"line 3:6 Cannot compare Num and java.lang.Object.");
	}

	@Test
	public void testEquationGreaterThanBooleans()
	{
		Variables variables = new LocalVariables(new HashMap<>());
		Coordinate coordinate = new Coordinate(3,  6);
		Bool firstOperand = new Bool("true");
		Bool secondOperand = new Bool("false");
		Comparator greaterThan = new GreaterThan();

		Equation operation =
			new Equation(coordinate, greaterThan, firstOperand, secondOperand);

		Type type = operation.getType(variables);
		Result result = operation.compile(variables);

		assertEquals(PrimitiveType.BOOLEAN, type);
		assertEquals(1, result.getErrors().length());

		assertEquals(result.getErrors().get(0).toString(),
			"line 3:6 Cannot compare Bool and Bool.");
	}

	@Test
	public void testEquationGreaterThanStrings()
	{
		Variables variables = new LocalVariables(new HashMap<>());
		Coordinate coordinate = new Coordinate(3,  6);
		Streeng firstOperand = new Streeng("\"thingy\"");
		Streeng secondOperand = new Streeng("\"sumpt\"");
		Comparator greaterThan = new GreaterThan();

		Equation operation =
			new Equation(coordinate, greaterThan, firstOperand, secondOperand);

		Type type = operation.getType(variables);
		Result result = operation.compile(variables);

		assertEquals(PrimitiveType.BOOLEAN, type);
		assertEquals(1, result.getErrors().length());

		assertEquals(result.getErrors().get(0).toString(),
			"line 3:6 Cannot compare java.lang.String and java.lang.String.");
	}

	@Test
	public void testCompilingEqualObjects()
	{
		Coordinate coordinate = new Coordinate(3, 6);
		Variables variables = new LocalVariables(new HashMap<>());
		Expression firstOperand = new This(new ReferenceType(Object.class));
		Expression secondOperand = new This(new ReferenceType(Object.class));
		Comparator notEquals = new Equals();

		Equation equation =
			new Equation(coordinate, notEquals, firstOperand, secondOperand);

		Type type = equation.getType(variables);
		Result result = equation.compile(variables);

		assertEquals(PrimitiveType.BOOLEAN, type);
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(8, instructions.length());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.ALOAD, secondInstruction.getOpcode());
		assertEquals(0, secondInstruction.var);

		JumpInsnNode thirdInstruction = (JumpInsnNode) instructions.get(2);
		Label trueLabel = thirdInstruction.label.getLabel();

		assertEquals(Opcodes.IF_ACMPNE, thirdInstruction.getOpcode());

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
	public void testCompilingNotEqualObjects()
	{
		Coordinate coordinate = new Coordinate(3, 6);
		Variables variables = new LocalVariables(new HashMap<>());
		Expression firstOperand = new This(new ReferenceType(Object.class));
		Expression secondOperand = new This(new ReferenceType(Object.class));
		Comparator notEquals = new NotEquals();

		Equation equation =
			new Equation(coordinate, notEquals, firstOperand, secondOperand);

		Type type = equation.getType(variables);
		Result result = equation.compile(variables);

		assertEquals(PrimitiveType.BOOLEAN, type);
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(8, instructions.length());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.ALOAD, secondInstruction.getOpcode());
		assertEquals(0, secondInstruction.var);

		JumpInsnNode thirdInstruction = (JumpInsnNode) instructions.get(2);
		Label trueLabel = thirdInstruction.label.getLabel();

		assertEquals(Opcodes.IF_ACMPEQ, thirdInstruction.getOpcode());

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
	public void testEquationGreaterThanObjects()
	{
		Variables variables = new LocalVariables(new HashMap<>());
		Coordinate coordinate = new Coordinate(3,  6);
		Expression firstOperand = new This(new ReferenceType(Object.class));
		Expression secondOperand = new This(new ReferenceType(Object.class));
		Comparator greaterThan = new GreaterThan();

		Equation operation =
			new Equation(coordinate, greaterThan, firstOperand, secondOperand);

		Type type = operation.getType(variables);

		Result result = operation.compile(variables);

		assertEquals(PrimitiveType.BOOLEAN, type);
		assertEquals(1, result.getErrors().length());

		assertEquals(result.getErrors().get(0).toString(),
			"line 3:6 Cannot compare java.lang.Object and java.lang.Object.");
	}

	@Test
	public void testCompilingEqualStrings()
	{
		Coordinate coordinate = new Coordinate(3, 6);
		Variables variables = new LocalVariables(new HashMap<>());
		Expression firstOperand = new This(new ReferenceType(String.class));
		Expression secondOperand = new This(new ReferenceType(String.class));
		Comparator equals = new Equals();

		Equation equation =
			new Equation(coordinate, equals, firstOperand, secondOperand);

		Type type = equation.getType(variables);
		Result result = equation.compile(variables);

		assertEquals(PrimitiveType.BOOLEAN, type);
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(3, instructions.length());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.ALOAD, secondInstruction.getOpcode());
		assertEquals(0, secondInstruction.var);

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKEVIRTUAL, thirdInstruction.getOpcode());
		assertEquals("java/lang/String", thirdInstruction.owner);
		assertEquals("equals", thirdInstruction.name);
		assertEquals("(Ljava/lang/Object;)Z", thirdInstruction.desc);
	}

	@Test
	public void testCompilingNotEqualStrings()
	{
		Coordinate coordinate = new Coordinate(3, 6);
		Variables variables = new LocalVariables(new HashMap<>());
		Expression firstOperand = new This(new ReferenceType(String.class));
		Expression secondOperand = new This(new ReferenceType(String.class));
		Comparator notEquals = new NotEquals();

		Equation equation =
			new Equation(coordinate, notEquals, firstOperand, secondOperand);

		Type type = equation.getType(variables);

		Result result = equation.compile(variables);

		assertEquals(PrimitiveType.BOOLEAN, type);
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(9, instructions.length());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.ALOAD, secondInstruction.getOpcode());
		assertEquals(0, secondInstruction.var);

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKEVIRTUAL, thirdInstruction.getOpcode());
		assertEquals("java/lang/String", thirdInstruction.owner);
		assertEquals("equals", thirdInstruction.name);
		assertEquals("(Ljava/lang/Object;)Z", thirdInstruction.desc);

		JumpInsnNode fourthInstruction = (JumpInsnNode) instructions.get(3);
		Label falseLabel = fourthInstruction.label.getLabel();

		assertEquals(Opcodes.IFNE, fourthInstruction.getOpcode());

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.ICONST_1, fifthInstruction.getOpcode());

		JumpInsnNode sixthInstruction = (JumpInsnNode) instructions.get(5);
		Label trueLabel = sixthInstruction.label.getLabel();

		assertEquals(Opcodes.GOTO, sixthInstruction.getOpcode());

		LabelNode seventhInstruction = (LabelNode) instructions.get(6);

		assertEquals(falseLabel, seventhInstruction.getLabel());

		InsnNode eighthInstruction = (InsnNode) instructions.get(7);

		assertEquals(Opcodes.ICONST_0, eighthInstruction.getOpcode());

		LabelNode ninthInstruction = (LabelNode) instructions.get(8);

		assertEquals(trueLabel, ninthInstruction.getLabel());
	}
}
