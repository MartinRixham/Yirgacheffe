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
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.operator.BooleanOperator;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BooleanOperationTest
{
	@Test
	public void testCompilingAndDoubles()
	{
		Variables variables = new Variables(new HashMap<>());
		Num firstOperand = new Num("3.0");
		Num secondOperand = new Num("2.0");

		BooleanOperation and =
			new BooleanOperation(
				BooleanOperator.AND,
				firstOperand,
				secondOperand);

		Type type = and.getType(variables);
		Result result = and.compile(variables);

		assertTrue(and.isCondition(variables));
		assertTrue(type.isAssignableTo(PrimitiveType.DOUBLE));
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(10, instructions.length());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(3.0, firstInstruction.cst);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP2, secondInstruction.getOpcode());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DCONST_1, thirdInstruction.getOpcode());

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.DCMPL, fourthInstruction.getOpcode());

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.ICONST_1, fifthInstruction.getOpcode());

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.IADD, sixthInstruction.getOpcode());

		JumpInsnNode seventhInstruction = (JumpInsnNode) instructions.get(6);
		Label label = seventhInstruction.label.getLabel();

		assertEquals(Opcodes.IFEQ, seventhInstruction.getOpcode());

		InsnNode eighthInstruction = (InsnNode) instructions.get(7);

		assertEquals(Opcodes.POP2, eighthInstruction.getOpcode());

		LdcInsnNode ninthInstruction = (LdcInsnNode) instructions.get(8);

		assertEquals(Opcodes.LDC, ninthInstruction.getOpcode());
		assertEquals(2.0, ninthInstruction.cst);

		LabelNode tenthInstruction = (LabelNode) instructions.get(9);

		assertEquals(label, tenthInstruction.getLabel());
	}

	@Test
	public void testCompilingAndIntegers()
	{
		Variables variables = new Variables(new HashMap<>());
		Num firstOperand = new Num("0");
		Num secondOperand = new Num("0");

		BooleanOperation and =
			new BooleanOperation(
				BooleanOperator.AND,
				firstOperand,
				secondOperand);

		Type type = and.getType(variables);
		Result result = and.compile(variables);

		assertTrue(type.isAssignableTo(PrimitiveType.DOUBLE));
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(6, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_0, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		JumpInsnNode thirdInstruction = (JumpInsnNode) instructions.get(2);
		Label label = thirdInstruction.label.getLabel();

		assertEquals(Opcodes.IFEQ, thirdInstruction.getOpcode());

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.POP, fourthInstruction.getOpcode());

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.ICONST_0, fifthInstruction.getOpcode());

		LabelNode sixthInstruction = (LabelNode) instructions.get(5);

		assertEquals(label, sixthInstruction.getLabel());
	}

	@Test
	public void testCompilingAndBooleans()
	{
		Variables variables = new Variables(new HashMap<>());
		Bool firstOperand = new Bool("true");
		Bool secondOperand = new Bool("false");

		BooleanOperation and =
			new BooleanOperation(
				BooleanOperator.AND,
				firstOperand,
				secondOperand);

		Type type = and.getType(variables);
		Result result = and.compile(variables);

		assertTrue(type.isAssignableTo(PrimitiveType.BOOLEAN));
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(6, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_1, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		JumpInsnNode thirdInstruction = (JumpInsnNode) instructions.get(2);
		Label label = thirdInstruction.label.getLabel();

		assertEquals(Opcodes.IFEQ, thirdInstruction.getOpcode());

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.POP, fourthInstruction.getOpcode());

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.ICONST_0, fifthInstruction.getOpcode());

		LabelNode sixthInstruction = (LabelNode) instructions.get(5);

		assertEquals(label, sixthInstruction.getLabel());
	}

	@Test
	public void testCompilingAndObjects()
	{
		Variables variables = new Variables(new HashMap<>());
		Expression firstOperand = new This(new ReferenceType(Object.class));
		Expression secondOperand = new This(new ReferenceType(Object.class));

		BooleanOperation and =
			new BooleanOperation(
				BooleanOperator.AND,
				firstOperand,
				secondOperand);

		Type type = and.getType(variables);
		Result result = and.compile(variables);

		assertTrue(type.isAssignableTo(new ReferenceType(Object.class)));
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(6, instructions.length());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		JumpInsnNode thirdInstruction = (JumpInsnNode) instructions.get(2);
		Label label = thirdInstruction.label.getLabel();

		assertEquals(Opcodes.IFNULL, thirdInstruction.getOpcode());

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.POP, fourthInstruction.getOpcode());

		VarInsnNode fifthInstruction = (VarInsnNode) instructions.get(4);

		assertEquals(Opcodes.ALOAD, fifthInstruction.getOpcode());
		assertEquals(0, fifthInstruction.var);

		LabelNode sixthInstruction = (LabelNode) instructions.get(5);

		assertEquals(label, sixthInstruction.getLabel());
	}

	@Test
	public void testCompilingAndStrings()
	{
		Variables variables = new Variables(new HashMap<>());
		Streeng firstOperand = new Streeng("\"mystring\"");
		Streeng secondOperand = new Streeng("\"notherstring\"");

		BooleanOperation or =
			new BooleanOperation(
				BooleanOperator.AND,
				firstOperand,
				secondOperand);

		Type type = or.getType(variables);
		Result result = or.compile(variables);

		assertTrue(type.isAssignableTo(new ReferenceType(String.class)));
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(7, instructions.length());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals("mystring", firstInstruction.cst);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKESTATIC, thirdInstruction.getOpcode());
		assertEquals("yirgacheffe/lang/Falsyfier", thirdInstruction.owner);
		assertEquals("isTruthy", thirdInstruction.name);
		assertEquals("(Ljava/lang/String;)Z", thirdInstruction.desc);

		JumpInsnNode fourthInstruction = (JumpInsnNode) instructions.get(3);
		Label label = fourthInstruction.label.getLabel();

		assertEquals(Opcodes.IFEQ, fourthInstruction.getOpcode());

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.POP, fifthInstruction.getOpcode());

		LdcInsnNode sixthInstruction = (LdcInsnNode) instructions.get(5);

		assertEquals(Opcodes.LDC, sixthInstruction.getOpcode());
		assertEquals("notherstring", sixthInstruction.cst);

		LabelNode seventhInstruction = (LabelNode) instructions.get(6);

		assertEquals(label, seventhInstruction.getLabel());
	}

	@Test
	public void testCompilingDoubleAndString()
	{
		Variables variables = new Variables(new HashMap<>());
		Num firstOperand = new Num("0.0");
		Streeng secondOperand = new Streeng("\"thingy\"");

		BooleanOperation or =
			new BooleanOperation(
				BooleanOperator.AND,
				firstOperand,
				secondOperand);

		Type type = or.getType(variables);
		Result result = or.compile(variables);

		assertTrue(type.isAssignableTo(new ReferenceType(Object.class)));
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(13, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCONST_0, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP2, secondInstruction.getOpcode());

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKESTATIC, thirdInstruction.getOpcode());
		assertEquals("java/lang/Double", thirdInstruction.owner);
		assertEquals("valueOf", thirdInstruction.name);
		assertEquals("(D)Ljava/lang/Double;", thirdInstruction.desc);

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.DUP_X2, fourthInstruction.getOpcode());

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.POP, fifthInstruction.getOpcode());

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.DCONST_1, sixthInstruction.getOpcode());

		InsnNode seventhInstruction = (InsnNode) instructions.get(6);

		assertEquals(Opcodes.DCMPL, seventhInstruction.getOpcode());

		InsnNode eighthInstruction = (InsnNode) instructions.get(7);

		assertEquals(Opcodes.ICONST_1, eighthInstruction.getOpcode());

		InsnNode ninthInstruction = (InsnNode) instructions.get(8);

		assertEquals(Opcodes.IADD, ninthInstruction.getOpcode());

		JumpInsnNode tenthInstruction = (JumpInsnNode) instructions.get(9);

		assertEquals(Opcodes.IFEQ, tenthInstruction.getOpcode());

		InsnNode eleventhInstruction = (InsnNode) instructions.get(10);

		assertEquals(Opcodes.POP, eleventhInstruction.getOpcode());
	}

	@Test
	public void testCompileIntegerAndString()
	{
		Variables variables = new Variables(new HashMap<>());
		Num firstOperand = new Num("0");
		Streeng secondOperand = new Streeng("\"thingy\"");

		BooleanOperation or =
			new BooleanOperation(
				BooleanOperator.AND,
				firstOperand,
				secondOperand);

		Type type = or.getType(variables);
		Result result = or.compile(variables);

		assertTrue(type.isAssignableTo(new ReferenceType(Object.class)));
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(8, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_0, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKESTATIC, thirdInstruction.getOpcode());
		assertEquals("java/lang/Integer", thirdInstruction.owner);
		assertEquals("valueOf", thirdInstruction.name);
		assertEquals("(I)Ljava/lang/Integer;", thirdInstruction.desc);

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.SWAP, fourthInstruction.getOpcode());

		JumpInsnNode fifthInstruction = (JumpInsnNode) instructions.get(4);
		Label label = fifthInstruction.label.getLabel();

		assertEquals(Opcodes.IFEQ, fifthInstruction.getOpcode());

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.POP, sixthInstruction.getOpcode());

		LdcInsnNode seventhInstruction = (LdcInsnNode) instructions.get(6);

		assertEquals(Opcodes.LDC, seventhInstruction.getOpcode());
		assertEquals("thingy", seventhInstruction.cst);

		LabelNode eighthInstruction = (LabelNode) instructions.get(7);

		assertEquals(label, eighthInstruction.getLabel());
	}

	@Test
	public void testCompilingIntegerAndDouble()
	{
		Variables variables = new Variables(new HashMap<>());
		Num firstOperand = new Num("1");
		Num secondOperand = new Num("0.0");

		BooleanOperation or =
			new BooleanOperation(
				BooleanOperator.AND,
				firstOperand,
				secondOperand);

		Type type = or.getType(variables);
		Result result = or.compile(variables);

		assertTrue(type.isAssignableTo(new ReferenceType(Object.class)));
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(9, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_1, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.I2D, thirdInstruction.getOpcode());

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.DUP2_X1, fourthInstruction.getOpcode());

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.POP2, fifthInstruction.getOpcode());

		JumpInsnNode sixthInstruction = (JumpInsnNode) instructions.get(5);
		Label label = sixthInstruction.label.getLabel();

		assertEquals(Opcodes.IFEQ, sixthInstruction.getOpcode());

		InsnNode seventhInstruction = (InsnNode) instructions.get(6);

		assertEquals(Opcodes.POP2, seventhInstruction.getOpcode());

		InsnNode eighthInstruction = (InsnNode) instructions.get(7);

		assertEquals(Opcodes.DCONST_0, eighthInstruction.getOpcode());

		LabelNode ninthInstruction = (LabelNode) instructions.get(8);

		assertEquals(label, ninthInstruction.getLabel());
	}

	@Test
	public void testCompilingIntegerAndBoolean()
	{
		Variables variables = new Variables(new HashMap<>());
		Num firstOperand = new Num("1");
		Bool secondOperand = new Bool("false");

		BooleanOperation or =
			new BooleanOperation(
				BooleanOperator.AND,
				firstOperand,
				secondOperand);

		Type type = or.getType(variables);
		Result result = or.compile(variables);

		assertTrue(type.isAssignableTo(new ReferenceType(Object.class)));
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(9, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_1, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKESTATIC, thirdInstruction.getOpcode());
		assertEquals("java/lang/Integer", thirdInstruction.owner);
		assertEquals("valueOf", thirdInstruction.name);
		assertEquals("(I)Ljava/lang/Integer;", thirdInstruction.desc);

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.SWAP, fourthInstruction.getOpcode());

		JumpInsnNode fifthInstruction = (JumpInsnNode) instructions.get(4);
		Label label = fifthInstruction.label.getLabel();

		assertEquals(Opcodes.IFEQ, fifthInstruction.getOpcode());

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.POP, sixthInstruction.getOpcode());

		InsnNode seventhInstruction = (InsnNode) instructions.get(6);

		assertEquals(Opcodes.ICONST_0, seventhInstruction.getOpcode());

		MethodInsnNode eighthInstruction = (MethodInsnNode) instructions.get(7);

		assertEquals(Opcodes.INVOKESTATIC, eighthInstruction.getOpcode());
		assertEquals("java/lang/Boolean", eighthInstruction.owner);
		assertEquals("valueOf", eighthInstruction.name);
		assertEquals("(Z)Ljava/lang/Boolean;", eighthInstruction.desc);

		LabelNode ninthInstruction = (LabelNode) instructions.get(8);

		assertEquals(label, ninthInstruction.getLabel());
	}

	@Test
	public void testCompilingDoubleAndInteger()
	{
		Variables variables = new Variables(new HashMap<>());
		Num firstOperand = new Num("1.0");
		Num secondOperand = new Num("0");

		BooleanOperation or =
			new BooleanOperation(
				BooleanOperator.AND,
				firstOperand,
				secondOperand);

		Type type = or.getType(variables);
		Result result = or.compile(variables);

		assertTrue(type.isAssignableTo(new ReferenceType(Object.class)));
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(11, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCONST_1, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP2, secondInstruction.getOpcode());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DCONST_1, thirdInstruction.getOpcode());

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.DCMPL, fourthInstruction.getOpcode());

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.ICONST_1, fifthInstruction.getOpcode());

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.IADD, sixthInstruction.getOpcode());

		JumpInsnNode seventhInstruction = (JumpInsnNode) instructions.get(6);
		Label label = seventhInstruction.label.getLabel();

		assertEquals(Opcodes.IFEQ, seventhInstruction.getOpcode());

		InsnNode eighthInstruction = (InsnNode) instructions.get(7);

		assertEquals(Opcodes.POP2, eighthInstruction.getOpcode());

		InsnNode ninthInstruction = (InsnNode) instructions.get(8);

		assertEquals(Opcodes.ICONST_0, ninthInstruction.getOpcode());

		InsnNode tenthInstruction = (InsnNode) instructions.get(9);

		assertEquals(Opcodes.I2D, tenthInstruction.getOpcode());

		LabelNode eleventhInstruction = (LabelNode) instructions.get(10);

		assertEquals(label, eleventhInstruction.getLabel());
	}

	@Test
	public void testCompilingBooleanAndDouble()
	{
		Variables variables = new Variables(new HashMap<>());
		Bool firstOperand = new Bool("true");
		Num secondOperand = new Num("0.0");

		BooleanOperation or =
			new BooleanOperation(
				BooleanOperator.AND,
				firstOperand,
				secondOperand);

		Type type = or.getType(variables);
		Result result = or.compile(variables);

		assertTrue(type.isAssignableTo(new ReferenceType(Object.class)));
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(9, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_1, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKESTATIC, thirdInstruction.getOpcode());
		assertEquals("java/lang/Boolean", thirdInstruction.owner);
		assertEquals("valueOf", thirdInstruction.name);
		assertEquals("(Z)Ljava/lang/Boolean;", thirdInstruction.desc);

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.SWAP, fourthInstruction.getOpcode());

		JumpInsnNode fifthInstruction = (JumpInsnNode) instructions.get(4);
		Label label = fifthInstruction.label.getLabel();

		assertEquals(Opcodes.IFEQ, fifthInstruction.getOpcode());

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.POP, sixthInstruction.getOpcode());

		InsnNode seventhInstruction = (InsnNode) instructions.get(6);

		assertEquals(Opcodes.DCONST_0, seventhInstruction.getOpcode());

		MethodInsnNode eighthInstruction = (MethodInsnNode) instructions.get(7);

		assertEquals(Opcodes.INVOKESTATIC, eighthInstruction.getOpcode());
		assertEquals("java/lang/Double", eighthInstruction.owner);
		assertEquals("valueOf", eighthInstruction.name);
		assertEquals("(D)Ljava/lang/Double;", eighthInstruction.desc);

		LabelNode ninthInstruction = (LabelNode) instructions.get(8);

		assertEquals(label, ninthInstruction.getLabel());
	}

	@Test
	public void testOrDifferentTypesAreAssignableToCommonSupertype()
	{
		Variables variables = new Variables(new HashMap<>());
		Type arrayList = new ReferenceType(java.util.ArrayList.class);
		Type linkedList = new ReferenceType(java.util.LinkedList.class);
		Type list = new ReferenceType(java.util.List.class);
		Expression firstOperand = new This(arrayList);
		Expression secondOperand = new This(linkedList);

		BooleanOperation or =
			new BooleanOperation(
				BooleanOperator.AND,
				firstOperand,
				secondOperand);

		Type type = or.getType(variables);
		Result result = or.compile(variables);

		assertEquals(0, result.getErrors().length());
		assertFalse(type.isAssignableTo(arrayList));
		assertFalse(type.isAssignableTo(linkedList));
		assertTrue(type.isAssignableTo(list));
	}

	@Test
	public void testGettingVariableReads()
	{
		Coordinate coordinate = new Coordinate(3, 6);
		VariableRead firstOperand = new VariableRead(coordinate, "myVariable");
		VariableRead secondOperand = new VariableRead(coordinate, "myVariable");

		Expression operation =
			new BooleanOperation(
				BooleanOperator.AND,
				firstOperand,
				secondOperand);

		Array<VariableRead> reads = operation.getVariableReads();

		assertTrue(reads.indexOf(firstOperand) >= 0);
		assertTrue(reads.indexOf(secondOperand) >= 0);
	}
}
