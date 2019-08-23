package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.comparison.Comparator;
import yirgacheffe.compiler.comparison.GreaterThan;
import yirgacheffe.compiler.comparison.LessThan;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MultiEquationTest
{
	@Test
	public void testCompilingMultiequation()
	{
		Comparator lessThan = new LessThan();
		Comparator greaterThan = new GreaterThan();
		Coordinate coordinate = new Coordinate(3, 5);
		Array<Comparator> comparators = new Array<>(lessThan, lessThan, greaterThan);

		Array<Expression> expressions =
			new Array<>(new Num("1"), new Num("1"), new Num("1"), new Num("0"));

		Variables variables = new LocalVariables(new HashMap<>());

		MultiEquation equation = new MultiEquation(coordinate, comparators, expressions);

		Result result = equation.compile(variables);

		assertEquals(1, variables.getStack().length());
		assertTrue(equation.isCondition(variables));
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(19, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_1, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.ICONST_1, secondInstruction.getOpcode());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DUP_X1, thirdInstruction.getOpcode());

		JumpInsnNode fourthInstruction = (JumpInsnNode) instructions.get(3);
		Label firstLabel = fourthInstruction.label.getLabel();

		assertEquals(Opcodes.IF_ICMPGE, fourthInstruction.getOpcode());

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.ICONST_1, fifthInstruction.getOpcode());

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.DUP_X1, sixthInstruction.getOpcode());

		JumpInsnNode seventhInstruction = (JumpInsnNode) instructions.get(6);
		Label secondLabel = fourthInstruction.label.getLabel();

		assertEquals(Opcodes.IF_ICMPGE, seventhInstruction.getOpcode());

		InsnNode eighthInstruction = (InsnNode) instructions.get(7);

		assertEquals(Opcodes.ICONST_0, eighthInstruction.getOpcode());

		JumpInsnNode ninthInstruction = (JumpInsnNode) instructions.get(8);
		Label thirdLabel = ninthInstruction.label.getLabel();

		assertEquals(Opcodes.IF_ICMPLE, ninthInstruction.getOpcode());

		JumpInsnNode tenthInstruction = (JumpInsnNode) instructions.get(9);
		Label fourthLabel = tenthInstruction.label.getLabel();

		assertEquals(Opcodes.GOTO, tenthInstruction.getOpcode());

		LabelNode eleventhInstruction = (LabelNode) instructions.get(10);

		assertEquals(firstLabel, eleventhInstruction.getLabel());
		assertEquals(secondLabel, eleventhInstruction.getLabel());

		InsnNode twelfthInstruction = (InsnNode) instructions.get(11);

		assertEquals(Opcodes.POP, twelfthInstruction.getOpcode());
	}

	@Test
	public void testCompilingMultiequationOfIntegerAndDouble()
	{
		Comparator lessThan = new LessThan();
		Coordinate coordinate = new Coordinate(3, 5);
		Array<Comparator> comparators = new Array<>(lessThan);

		Array<Expression> expressions =
			new Array<>(new Num("1"), new Num("1.0"));

		Variables variables = new LocalVariables(new HashMap<>());

		MultiEquation equation = new MultiEquation(coordinate, comparators, expressions);

		Result result = equation.compile(variables);

		assertEquals(1, variables.getStack().length());
		assertTrue(equation.isCondition(variables));
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(15, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_1, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.I2D, secondInstruction.getOpcode());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DCONST_1, thirdInstruction.getOpcode());

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.DCMPG, fourthInstruction.getOpcode());
	}
}
