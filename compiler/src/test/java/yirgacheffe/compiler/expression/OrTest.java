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
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;

public class OrTest
{
	@Test
	public void testCompilingAndDoubles()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();
		Literal firstOperand = new Literal(PrimitiveType.DOUBLE, "3");
		Literal secondOperand = new Literal(PrimitiveType.DOUBLE, "2");

		Or or = new Or(firstOperand, secondOperand);

		Type type = or.getType(variables);

		Array<Error> errors = or.compile(methodVisitor, variables);

		assertEquals(PrimitiveType.DOUBLE, type);
		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(10, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(2.0, firstInstruction.cst);

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(Opcodes.LDC, secondInstruction.getOpcode());
		assertEquals(3.0, secondInstruction.cst);

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DUP2, thirdInstruction.getOpcode());

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.DCONST_0, fourthInstruction.getOpcode());

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.DCMPL, fifthInstruction.getOpcode());

		JumpInsnNode sixthInstruction = (JumpInsnNode) instructions.get(5);
		Label label = sixthInstruction.label.getLabel();

		assertEquals(Opcodes.IFEQ, sixthInstruction.getOpcode());

		InsnNode seventhInstruction = (InsnNode) instructions.get(6);

		assertEquals(Opcodes.DUP2_X2, seventhInstruction.getOpcode());

		InsnNode eightInstruction = (InsnNode) instructions.get(7);

		assertEquals(Opcodes.POP2, eightInstruction.getOpcode());

		LabelNode ninthInstruction = (LabelNode) instructions.get(8);

		assertEquals(label, ninthInstruction.getLabel());

		InsnNode tenthInstruction = (InsnNode) instructions.get(9);

		assertEquals(Opcodes.POP2, tenthInstruction.getOpcode());
	}

	@Test
	public void testCompilingAndBooleans()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();
		Literal firstOperand = new Literal(PrimitiveType.BOOLEAN, "true");
		Literal secondOperand = new Literal(PrimitiveType.BOOLEAN, "false");

		Or or = new Or(firstOperand, secondOperand);

		Type type = or.getType(variables);

		Array<Error> errors = or.compile(methodVisitor, variables);

		assertEquals(PrimitiveType.BOOLEAN, type);
		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(7, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_0, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.ICONST_1, secondInstruction.getOpcode());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DUP, thirdInstruction.getOpcode());

		JumpInsnNode fourthInstruction = (JumpInsnNode) instructions.get(3);
		Label label = fourthInstruction.label.getLabel();

		assertEquals(Opcodes.IFEQ, fourthInstruction.getOpcode());

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.SWAP, fifthInstruction.getOpcode());

		LabelNode sixthInstruction = (LabelNode) instructions.get(5);

		assertEquals(label, sixthInstruction.getLabel());

		InsnNode seventhInstruction = (InsnNode) instructions.get(6);

		assertEquals(Opcodes.POP, seventhInstruction.getOpcode());
	}

	@Test
	public void testCompilingAndStrings()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();
		Type string = new ReferenceType(String.class);
		Literal firstOperand = new Literal(string, "\"mystring\"");
		Literal secondOperand = new Literal(string, "\"notherstring\"");

		Or or = new Or(firstOperand, secondOperand);

		Type type = or.getType(variables);

		Array<Error> errors = or.compile(methodVisitor, variables);

		assertEquals(string, type);
		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(7, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals("notherstring", firstInstruction.cst);

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(Opcodes.LDC, secondInstruction.getOpcode());
		assertEquals("mystring", secondInstruction.cst);

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DUP, thirdInstruction.getOpcode());

		JumpInsnNode fourthInstruction = (JumpInsnNode) instructions.get(3);
		Label label = fourthInstruction.label.getLabel();

		assertEquals(Opcodes.IFNULL, fourthInstruction.getOpcode());

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.SWAP, fifthInstruction.getOpcode());

		LabelNode sixthInstruction = (LabelNode) instructions.get(5);

		assertEquals(label, sixthInstruction.getLabel());

		InsnNode seventhInstruction = (InsnNode) instructions.get(6);

		assertEquals(Opcodes.POP, seventhInstruction.getOpcode());
	}
}
