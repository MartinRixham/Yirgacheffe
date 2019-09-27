package yirgacheffe.compiler.type;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.operator.BooleanOperator;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class PrimitiveTypeTest
{
	@Test
	public void testVoidIsPrimitive()
	{
		PrimitiveType type = PrimitiveType.VOID;

		assertEquals("Void", type.toString());
		assertEquals("java/lang/Void", type.toFullyQualifiedType());
		assertEquals("V", type.toJVMType());
		assertEquals("V", type.getSignature());
		assertEquals(0, type.width());
		assertEquals(Opcodes.RETURN, type.getReturnInstruction());
		assertEquals(Opcodes.NOP, type.getStoreInstruction());
		assertEquals(Opcodes.NOP, type.getArrayStoreInstruction());
		assertEquals(Opcodes.NOP, type.getLoadInstruction());
		assertEquals(Opcodes.NOP, type.getZero());
		assertFalse(type.hasParameter());
		assertTrue(type.isPrimitive());
		assertTrue(type.getTypeParameter("") instanceof NullType);
	}

	@Test
	public void testBoolIsPrimitive()
	{
		PrimitiveType type = PrimitiveType.BOOLEAN;

		assertEquals("Bool", type.toString());
		assertEquals("java/lang/Boolean", type.toFullyQualifiedType());
		assertEquals("Z", type.toJVMType());
		assertEquals(1, type.width());
		assertEquals(Opcodes.IRETURN, type.getReturnInstruction());
		assertEquals(Opcodes.ISTORE, type.getStoreInstruction());
		assertEquals(Opcodes.IASTORE, type.getArrayStoreInstruction());
		assertEquals(Opcodes.ILOAD, type.getLoadInstruction());
		assertEquals(Opcodes.ICONST_0, type.getZero());
	}

	@Test
	public void testCharIsPrimitive()
	{
		PrimitiveType type = PrimitiveType.CHAR;

		assertEquals("Char", type.toString());
		assertEquals("java/lang/Character", type.toFullyQualifiedType());
		assertEquals("C", type.toJVMType());
		assertEquals(1, type.width());
		assertEquals(Opcodes.IRETURN, type.getReturnInstruction());
		assertEquals(Opcodes.ISTORE, type.getStoreInstruction());
		assertEquals(Opcodes.IASTORE, type.getArrayStoreInstruction());
		assertEquals(Opcodes.ILOAD, type.getLoadInstruction());
		assertEquals(Opcodes.ICONST_0, type.getZero());
	}

	@Test
	public void testNumIsPrimitive()
	{
		PrimitiveType type = PrimitiveType.DOUBLE;

		assertEquals("Num", type.toString());
		assertEquals("java/lang/Double", type.toFullyQualifiedType());
		assertEquals("D", type.toJVMType());
		assertEquals(2, type.width());
		assertEquals(Opcodes.DRETURN, type.getReturnInstruction());
		assertEquals(Opcodes.DSTORE, type.getStoreInstruction());
		assertEquals(Opcodes.DASTORE, type.getArrayStoreInstruction());
		assertEquals(Opcodes.DLOAD, type.getLoadInstruction());
		assertEquals(Opcodes.DCONST_0, type.getZero());
	}

	@Test
	public void testPrimitiveIsAssignableToItself()
	{
		Type type = PrimitiveType.CHAR;

		assertTrue(type.isAssignableTo(type));
	}

	@Test
	public void testPrimitiveIsNotAssignableToSomethingElse()
	{
		Type type = PrimitiveType.CHAR;
		Type otherType = PrimitiveType.BOOLEAN;

		assertFalse(type.isAssignableTo(otherType));
	}

	@Test
	public void testNewArray()
	{
		PrimitiveType type = PrimitiveType.DOUBLE;

		Result result = type.newArray();

		assertEquals(0, result.getErrors().length());
		assertEquals(1, result.getInstructions().length());

		IntInsnNode instruction = (IntInsnNode) result.getInstructions().get(0);

		assertEquals(Opcodes.NEWARRAY, instruction.getOpcode());
		assertEquals(Opcodes.T_DOUBLE, instruction.operand);
	}

	@Test
	public void testTypeConversion()
	{
		PrimitiveType type = PrimitiveType.DOUBLE;

		Result result = type.convertTo(new ReferenceType(Object.class));

		assertEquals(0, result.getErrors().length());
		assertEquals(1, result.getInstructions().length());

		MethodInsnNode instruction = (MethodInsnNode) result.getInstructions().get(0);

		assertEquals(Opcodes.INVOKESTATIC, instruction.getOpcode());
		assertEquals("java/lang/Double", instruction.owner);
		assertEquals("valueOf", instruction.name);
		assertEquals("(D)Ljava/lang/Double;", instruction.desc);
	}

	@Test
	public void testSwapDoubleWithDouble()
	{
		PrimitiveType type = PrimitiveType.DOUBLE;

		Result result = type.swapWith(type);

		assertEquals(0, result.getErrors().length());
		assertEquals(2, result.getInstructions().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();
		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DUP2_X2, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.POP2, secondInstruction.getOpcode());
	}

	@Test
	public void testSwapIntWithDouble()
	{
		PrimitiveType type = PrimitiveType.INT;

		Result result = type.swapWith(PrimitiveType.DOUBLE);

		assertEquals(0, result.getErrors().length());
		assertEquals(2, result.getInstructions().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();
		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DUP2_X1, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.POP2, secondInstruction.getOpcode());
	}

	@Test
	public void testSwapDoubleWithInt()
	{
		PrimitiveType type = PrimitiveType.DOUBLE;

		Result result = type.swapWith(PrimitiveType.INT);

		assertEquals(0, result.getErrors().length());
		assertEquals(2, result.getInstructions().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();
		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DUP_X2, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.POP, secondInstruction.getOpcode());
	}

	@Test
	public void testSwapIntWithInt()
	{
		PrimitiveType type = PrimitiveType.INT;

		Result result = type.swapWith(type);

		assertEquals(0, result.getErrors().length());
		assertEquals(1, result.getInstructions().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();
		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.SWAP, firstInstruction.getOpcode());
	}

	@Test
	public void testSwapIntWithVoid()
	{
		PrimitiveType type = PrimitiveType.INT;

		Result result = type.swapWith(PrimitiveType.VOID);

		assertEquals(0, result.getErrors().length());
		assertEquals(0, result.getInstructions().length());
	}

	@Test
	public void testIntersection()
	{
		Type bool = PrimitiveType.BOOLEAN;
		Type integer = PrimitiveType.INT;
		Type lon = PrimitiveType.LONG;
		Type dub = PrimitiveType.DOUBLE;
		Type obj = new ReferenceType(Object.class);

		assertEquals(bool, bool.intersect(bool));
		assertEquals(dub, integer.intersect(dub));
		assertEquals(dub, dub.intersect(integer));
		assertEquals(lon, lon.intersect(integer));
		assertEquals(lon, integer.intersect(lon));
		assertEquals(obj, dub.intersect(bool));
	}

	@Test
	public void compareInteger()
	{
		Type type = PrimitiveType.INT;
		Label label = new Label();

		Result result = type.compare(BooleanOperator.AND, label);

		assertEquals(0, result.getErrors().length());
		assertEquals(1, result.getInstructions().length());

		JumpInsnNode firstInstruction = (JumpInsnNode) result.getInstructions().get(0);

		assertEquals(Opcodes.IFEQ, firstInstruction.getOpcode());
		assertEquals(label, firstInstruction.label.getLabel());
	}

	@Test
	public void compareLong()
	{
		Type type = PrimitiveType.LONG;
		Label label = new Label();

		Result result = type.compare(BooleanOperator.AND, label);

		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(3, instructions.length());

		InsnNode firstInstruction = (InsnNode) result.getInstructions().get(0);

		assertEquals(Opcodes.LCONST_0, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.LCMP, secondInstruction.getOpcode());

		JumpInsnNode thirdInstruction = (JumpInsnNode) result.getInstructions().get(2);

		assertEquals(Opcodes.IFEQ, thirdInstruction.getOpcode());
		assertEquals(label, thirdInstruction.label.getLabel());
	}

	@Test
	public void compareDouble()
	{
		Type type = PrimitiveType.DOUBLE;
		Label label = new Label();

		Result result = type.compare(BooleanOperator.AND, label);

		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(5, instructions.length());

		InsnNode firstInstruction = (InsnNode) result.getInstructions().get(0);

		assertEquals(Opcodes.DCONST_1, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DCMPL, secondInstruction.getOpcode());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.ICONST_1, thirdInstruction.getOpcode());

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.IADD, fourthInstruction.getOpcode());

		JumpInsnNode fifthInstruction = (JumpInsnNode) result.getInstructions().get(4);

		assertEquals(Opcodes.IFEQ, fifthInstruction.getOpcode());
		assertEquals(label, fifthInstruction.label.getLabel());
	}
}
