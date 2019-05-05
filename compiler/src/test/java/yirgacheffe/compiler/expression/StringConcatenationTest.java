package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;

public class StringConcatenationTest
{
	@Test
	public void testNumberAddition()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();
		Coordinate coordinate = new Coordinate(3, 5);
		Operator operator = Operator.ADD;
		Expression firstOperand = new Num("3.0");
		Expression secondOperand = new Num("2.0");

		BinaryOperation binaryOperation =
			new BinaryOperation(coordinate, operator, firstOperand, secondOperand);

		StringConcatenation concatenation = new StringConcatenation(binaryOperation);

		assertEquals(PrimitiveType.DOUBLE, concatenation.getType(variables));
		assertEquals(0, concatenation.getVariableReads().length());

		Array<Error> errors = concatenation.compile(methodVisitor, variables);

		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		InsnNode nthInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DADD, nthInstruction.getOpcode());
	}

	@Test
	public void testCompilingStringAddition()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();
		Coordinate coordinate = new Coordinate(3,  6);
		Streeng firstOperand = new Streeng("\"this\"");
		Streeng secondOperand = new Streeng("\"that\"");
		Streeng thirdOperand = new Streeng("\"tother\"");

		BinaryOperation operation =
			new BinaryOperation(
				coordinate,
				Operator.ADD,
				new BinaryOperation(
					coordinate,
					Operator.ADD,
					firstOperand,
					secondOperand),
				thirdOperand);

		StringConcatenation concatenation = new StringConcatenation(operation);

		Type type = concatenation.getType(variables);

		Array<Error> errors = concatenation.compile(methodVisitor, variables);

		assertEquals("Ljava/lang/String;", type.toJVMType());
		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		//assertEquals(3, instructions.size());

		TypeInsnNode firstInstruction = (TypeInsnNode) instructions.get(0);

		assertEquals(Opcodes.NEW, firstInstruction.getOpcode());
		assertEquals("java/lang/StringBuilder", firstInstruction.desc);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKESPECIAL, thirdInstruction.getOpcode());
		assertEquals("java/lang/StringBuilder", thirdInstruction.owner);
		assertEquals("<init>", thirdInstruction.name);
		assertEquals("()V", thirdInstruction.desc);

		LdcInsnNode fourthInstruction = (LdcInsnNode) instructions.get(3);

		assertEquals(Opcodes.LDC, fourthInstruction.getOpcode());
		assertEquals("this", fourthInstruction.cst);

		MethodInsnNode fifthInstruction = (MethodInsnNode) instructions.get(4);

		assertEquals(Opcodes.INVOKEVIRTUAL, fifthInstruction.getOpcode());
		assertEquals("java/lang/StringBuilder", fifthInstruction.owner);
		assertEquals("append", fifthInstruction.name);
		assertEquals(
			"(Ljava/lang/String;)Ljava/lang/StringBuilder;", fifthInstruction.desc);

		LdcInsnNode sixthInstruction = (LdcInsnNode) instructions.get(5);

		assertEquals(Opcodes.LDC, sixthInstruction.getOpcode());
		assertEquals("that", sixthInstruction.cst);

		MethodInsnNode seventhInstruction = (MethodInsnNode) instructions.get(6);

		assertEquals(Opcodes.INVOKEVIRTUAL, seventhInstruction.getOpcode());
		assertEquals("java/lang/StringBuilder", seventhInstruction.owner);
		assertEquals("append", seventhInstruction.name);
		assertEquals(
			"(Ljava/lang/String;)Ljava/lang/StringBuilder;", seventhInstruction.desc);

		LdcInsnNode eighthInstruction = (LdcInsnNode) instructions.get(7);

		assertEquals(Opcodes.LDC, eighthInstruction.getOpcode());
		assertEquals("tother", eighthInstruction.cst);

		MethodInsnNode ninthInstruction = (MethodInsnNode) instructions.get(8);

		assertEquals(Opcodes.INVOKEVIRTUAL, ninthInstruction.getOpcode());
		assertEquals("java/lang/StringBuilder", ninthInstruction.owner);
		assertEquals("append", ninthInstruction.name);
		assertEquals(
			"(Ljava/lang/String;)Ljava/lang/StringBuilder;", ninthInstruction.desc);

		MethodInsnNode tenthInstruction = (MethodInsnNode) instructions.get(9);

		assertEquals(Opcodes.INVOKEVIRTUAL, tenthInstruction.getOpcode());
		assertEquals("java/lang/StringBuilder", tenthInstruction.owner);
		assertEquals("toString", tenthInstruction.name);
		assertEquals(
			"()Ljava/lang/String;", tenthInstruction.desc);
	}
}
