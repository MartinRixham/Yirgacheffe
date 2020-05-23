package yirgacheffe.compiler.type;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.operator.BooleanOperator;
import yirgacheffe.lang.Array;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GenericTypeTest
{
	@Test
	public void testTypeFromPackageAndIdentifier()
	{
		Class<?> loadedClass = String.class;

		ReferenceType referenceType = new ReferenceType(loadedClass);

		Type type = new GenericType(referenceType);

		assertEquals("java.lang.String", type.toString());
		assertTrue(type.reflect().doesImplement(loadedClass));
		assertTrue(type.reflect(type).doesImplement(loadedClass));
		assertEquals("java/lang/String", type.toFullyQualifiedType());
		assertEquals("Ljava/lang/Object;", type.toJVMType());
		assertEquals("Ljava/lang/String;", type.getSignature());
		assertEquals(0, type.construct(new Coordinate(0, 0)).getErrors().length());
		assertEquals(1, type.width());
		assertEquals(Opcodes.ARETURN, type.getReturnInstruction());
		assertEquals(Opcodes.ASTORE, type.getStoreInstruction());
		assertEquals(Opcodes.AASTORE, type.getArrayStoreInstruction());
		assertEquals(Opcodes.ALOAD, type.getLoadInstruction());
		assertEquals(Opcodes.ACONST_NULL, type.getZero());
		assertTrue(type.isAssignableTo(new ReferenceType(java.lang.String.class)));
		assertFalse(type.hasParameter());
		assertFalse(type.isPrimitive());
		assertEquals(type, referenceType);
		assertEquals(type.hashCode(), referenceType.hashCode());
		assertTrue(type.getTypeParameter("") instanceof NullType);
	}

	@Test
	public void testNewArray()
	{
		Type concreteType = new ReferenceType(Random.class);
		Type type = new GenericType(concreteType);

		Result result = type.newArray();

		assertEquals(0, result.getErrors().length());
		assertEquals(1, result.getInstructions().length());

		TypeInsnNode instruction = (TypeInsnNode) result.getInstructions().get(0);

		assertEquals(Opcodes.ANEWARRAY, instruction.getOpcode());
		assertEquals(concreteType.toFullyQualifiedType(), instruction.desc);
	}

	@Test
	public void testTypeConversion()
	{
		Type concreteType = new ReferenceType(Random.class);
		Type type = new GenericType(concreteType);

		Result result = type.convertTo(new ReferenceType(Random.class));

		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(1, instructions.length());

		TypeInsnNode firstInstruction = (TypeInsnNode) instructions.get(0);

		assertEquals(Opcodes.CHECKCAST, firstInstruction.getOpcode());
		assertEquals("java/util/Random", firstInstruction.desc);
	}

	@Test
	public void testTypeConversionToPrimitive()
	{
		Type concreteType = PrimitiveType.DOUBLE;
		Type type = new GenericType(concreteType);

		Result result = type.convertTo(PrimitiveType.DOUBLE);

		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(1, instructions.length());

		MethodInsnNode firstInstruction = (MethodInsnNode) instructions.get(0);

		assertEquals(Opcodes.INVOKESTATIC, firstInstruction.getOpcode());
		assertEquals("yirgacheffe/lang/Boxer", firstInstruction.owner);
		assertEquals("toDouble", firstInstruction.name);
		assertEquals("(Ljava/lang/Object;)D", firstInstruction.desc);
	}

	@Test
	public void testSwap()
	{
		Type concreteType = new ReferenceType(Random.class);
		Type type = new GenericType(concreteType);

		Result result = type.swapWith(new ReferenceType(Object.class));

		assertEquals(0, result.getErrors().length());
		assertEquals(0, result.getInstructions().length());
	}

	@Test
	public void testIntersection()
	{
		Type concreteType = new ReferenceType(Random.class);
		Type type = new GenericType(concreteType);

		Type intersection = type.intersect(new ReferenceType(Object.class));

		assertEquals(new ReferenceType(Object.class), intersection);
	}

	@Test
	public void testComparison()
	{
		Type concreteType = new ReferenceType(Random.class);
		Type type = new GenericType(concreteType);
		Label label = new Label();

		Result result = type.compare(BooleanOperator.AND, label);

		assertEquals(0, result.getErrors().length());
		assertEquals(1, result.getInstructions().length());

		JumpInsnNode firstInstruction = (JumpInsnNode) result.getInstructions().get(0);

		assertEquals(Opcodes.IFNULL, firstInstruction.getOpcode());
		assertEquals(label, firstInstruction.label.getLabel());
	}
}
