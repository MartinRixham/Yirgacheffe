package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.operator.BooleanOperator;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NotTest
{
	@Test
	public void testCompilingNotFalse()
	{
		Coordinate coordinate = new Coordinate(8, 30);
		Variables variables = new LocalVariables(1, new HashMap<>());

		Bool bool = new Bool(coordinate, "false");
		Not not = new Not(coordinate, bool, 1);

		Type type = not.getType(variables);
		Result result = not.compile(variables);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(0, not.getVariableReads().length());
		assertEquals(1, variables.getStack().length());
		assertTrue(not.isCondition(variables));
		assertEquals("java/lang/Boolean", type.toFullyQualifiedType());
		assertEquals(coordinate, not.getCoordinate());
		assertEquals(7, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_0, firstInstruction.getOpcode());

		JumpInsnNode secondInstruction = (JumpInsnNode) instructions.get(1);

		assertEquals(Opcodes.IFNE, secondInstruction.getOpcode());

		Label falseLabel = secondInstruction.label.getLabel();

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.ICONST_1, thirdInstruction.getOpcode());

		JumpInsnNode fourthInstruction = (JumpInsnNode) instructions.get(3);

		assertEquals(Opcodes.GOTO, fourthInstruction.getOpcode());

		Label trueLabel = fourthInstruction.label.getLabel();

		LabelNode fifthInstruction = (LabelNode) instructions.get(4);

		assertEquals(falseLabel, fifthInstruction.getLabel());

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.ICONST_0, sixthInstruction.getOpcode());

		LabelNode seventhInstruction = (LabelNode) instructions.get(6);

		assertEquals(trueLabel, seventhInstruction.getLabel());
	}

	@Test
	public void testCompilingNotNotFalse()
	{
		Coordinate coordinate = new Coordinate(8, 30);
		Variables variables = new LocalVariables(1, new HashMap<>());

		Bool bool = new Bool(coordinate, "false");
		Not not = new Not(coordinate, bool, 2);

		Type type = not.getType(variables);
		Result result = not.compile(variables);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(0, not.getVariableReads().length());
		assertEquals(1, variables.getStack().length());
		assertTrue(not.isCondition(variables));
		assertEquals("java/lang/Boolean", type.toFullyQualifiedType());
		assertEquals(coordinate, not.getCoordinate());
		assertEquals(7, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_0, firstInstruction.getOpcode());

		JumpInsnNode secondInstruction = (JumpInsnNode) instructions.get(1);

		assertEquals(Opcodes.IFEQ, secondInstruction.getOpcode());

		Label falseLabel = secondInstruction.label.getLabel();

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.ICONST_1, thirdInstruction.getOpcode());

		JumpInsnNode fourthInstruction = (JumpInsnNode) instructions.get(3);

		assertEquals(Opcodes.GOTO, fourthInstruction.getOpcode());

		Label trueLabel = fourthInstruction.label.getLabel();

		LabelNode fifthInstruction = (LabelNode) instructions.get(4);

		assertEquals(falseLabel, fifthInstruction.getLabel());

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.ICONST_0, sixthInstruction.getOpcode());

		LabelNode seventhInstruction = (LabelNode) instructions.get(6);

		assertEquals(trueLabel, seventhInstruction.getLabel());
	}

	@Test
	public void testCompilingNotCondition()
	{
		Coordinate coordinate = new Coordinate(8, 30);
		Variables variables = new LocalVariables(1, new HashMap<>());

		Bool bool = new Bool(coordinate, "false");
		Not not = new Not(coordinate, bool, 1);
		Label trueLabel = new Label();
		Label falseLabel = new Label();

		Result result = not.compileCondition(variables, trueLabel, falseLabel);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(1, variables.getStack().length());
		assertEquals(2, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_0, firstInstruction.getOpcode());

		JumpInsnNode secondInstruction = (JumpInsnNode) instructions.get(1);

		assertEquals(Opcodes.IFNE, secondInstruction.getOpcode());
		assertEquals(falseLabel, secondInstruction.label.getLabel());
	}

	@Test
	public void testCompilingNotWithCondition()
	{
		Coordinate coordinate = new Coordinate(8, 30);
		Variables variables = new LocalVariables(1, new HashMap<>());

		Bool bool = new Bool(coordinate, "false");
		BooleanOperation booleanOperation =
			new BooleanOperation(BooleanOperator.OR, bool, bool);

		Not not = new Not(coordinate, booleanOperation, 2);
		Label trueLabel = new Label();
		Label falseLabel = new Label();

		Result result = not.compileCondition(variables, trueLabel, falseLabel);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(1, variables.getStack().length());
		assertEquals(5, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_0, firstInstruction.getOpcode());

		JumpInsnNode secondInstruction = (JumpInsnNode) instructions.get(1);

		assertEquals(Opcodes.IFNE, secondInstruction.getOpcode());
		assertEquals(trueLabel, secondInstruction.label.getLabel());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.ICONST_0, thirdInstruction.getOpcode());

		JumpInsnNode fourthInstruction = (JumpInsnNode) instructions.get(3);

		assertEquals(Opcodes.IFEQ, fourthInstruction.getOpcode());
		assertEquals(falseLabel, fourthInstruction.label.getLabel());

		JumpInsnNode fifthInstruction = (JumpInsnNode) instructions.get(4);

		assertEquals(Opcodes.IFEQ, fifthInstruction.getOpcode());
		assertEquals(falseLabel, fifthInstruction.label.getLabel());
	}
}
