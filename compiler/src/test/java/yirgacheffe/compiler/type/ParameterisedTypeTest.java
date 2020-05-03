package yirgacheffe.compiler.type;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.operator.BooleanOperator;
import yirgacheffe.lang.Array;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class ParameterisedTypeTest
{
	public Comparable<String> getStringComparable()
	{
		return null;
	}

	public Comparable<Double> getNumberComparable()
	{
		return null;
	}

	@Test
	public void testTypeWithOneParameter()
	{
		ReferenceType referenceType = new ReferenceType(List.class);
		Array<Type> typeParameter = new Array<>(new ReferenceType(String.class));
		Type type = new ParameterisedType(referenceType, typeParameter);

		assertEquals("java.util.List<java.lang.String>", type.toString());
		assertTrue(type.reflect().doesImplement(List.class));
		assertTrue(type.reflect(type).doesImplement(List.class));
		assertEquals("java/util/List", type.toFullyQualifiedType());
		assertEquals("Ljava/util/List;", type.toJVMType());
		assertEquals("Ljava/util/List<Ljava/lang/String;>;", type.getSignature());
		assertEquals(1, type.width());
		assertEquals(Opcodes.ARETURN, type.getReturnInstruction());
		assertEquals(Opcodes.ASTORE, type.getStoreInstruction());
		assertEquals(Opcodes.AASTORE, type.getArrayStoreInstruction());
		assertEquals(Opcodes.ALOAD, type.getLoadInstruction());
		assertEquals(Opcodes.ACONST_NULL, type.getZero());
		assertTrue(type.hasParameter());
		assertFalse(type.isPrimitive());
		assertEquals(new ReferenceType(String.class), type.getTypeParameter("E"));
		assertEquals(referenceType.hashCode(), type.hashCode());
		assertNotEquals(type, "");

		assertFalse(type.isAssignableTo(
			new ParameterisedType(new ReferenceType(Set.class), typeParameter)));
	}

	@Test
	public void testTypeWithTwoParameters()
	{
		ReferenceType referenceType = new ReferenceType(Map.class);
		Type firstParameter = new ReferenceType(String.class);
		Type secondParameter = PrimitiveType.DOUBLE;
		Array<Type> parameters = new Array<>(firstParameter, secondParameter);
		Type type = new ParameterisedType(referenceType, parameters);

		assertEquals("java.util.Map<java.lang.String,Num>", type.toString());
	}

	@Test
	public void testStringIsNotAssignableToSystem()
	{
		ReferenceType reference =
			new ReferenceType(yirgacheffe.lang.MutableReference.class);

		Type string = new ReferenceType(String.class);
		Type system = new ReferenceType(System.class);

		Type stringReference = new ParameterisedType(reference, new Array<>(string));
		Type systemReference = new ParameterisedType(reference, new Array<>(system));

		assertFalse(stringReference.isAssignableTo(systemReference));
	}

	@Test
	public void testNotAssignableToReferenceType()
	{

		ReferenceType reference =
			new ReferenceType(yirgacheffe.lang.MutableReference.class);
		Type string = new ReferenceType(String.class);

		Type stringReference = new ParameterisedType(reference, new Array<>(string));

		assertFalse(stringReference.isAssignableTo(string));
	}

	@Test
	public void testNewArray()
	{
		ReferenceType referenceType = new ReferenceType(Random.class);
		Type type = new ParameterisedType(referenceType, new Array<>());

		Result result = type.newArray();

		assertEquals(0, result.getErrors().length());
		assertEquals(1, result.getInstructions().length());

		TypeInsnNode instruction = (TypeInsnNode) result.getInstructions().get(0);

		assertEquals(Opcodes.ANEWARRAY, instruction.getOpcode());
		assertEquals(referenceType.toFullyQualifiedType(), instruction.desc);
	}

	@Test
	public void testTypeConversion()
	{
		ReferenceType referenceType = new ReferenceType(Random.class);
		Type type = new ParameterisedType(referenceType, new Array<>());

		Result result = type.convertTo(new ReferenceType(Object.class));

		assertEquals(0, result.getErrors().length());
		assertEquals(0, result.getInstructions().length());
	}

	@Test
	public void testSwap()
	{
		ReferenceType referenceType = new ReferenceType(Random.class);
		Type type = new ParameterisedType(referenceType, new Array<>());

		Result result = type.swapWith(new ReferenceType(Object.class));

		assertEquals(0, result.getErrors().length());
		assertEquals(0, result.getInstructions().length());
	}

	@Test
	public void testIntersection()
	{
		ReferenceType referenceType = new ReferenceType(Random.class);
		Type type = new ParameterisedType(referenceType, new Array<>());

		Type intersection = type.intersect(new ReferenceType(Object.class));

		assertEquals(new ReferenceType(Object.class), intersection);
	}

	@Test
	public void testComparison()
	{
		ReferenceType referenceType = new ReferenceType(Random.class);
		Type type = new ParameterisedType(referenceType, new Array<>());
		Label label = new Label();

		Result result = type.compare(BooleanOperator.AND, label);

		assertEquals(0, result.getErrors().length());
		assertEquals(1, result.getInstructions().length());

		JumpInsnNode firstInstruction = (JumpInsnNode) result.getInstructions().get(0);

		assertEquals(Opcodes.IFNULL, firstInstruction.getOpcode());
		assertEquals(label, firstInstruction.label.getLabel());
	}

	@Test
	public void typesDifferByTypeParameter() throws Exception
	{
		Class<?> testClass = this.getClass();

		java.lang.reflect.Type stringComparableType =
			testClass.getMethod("getStringComparable").getGenericReturnType();

		java.lang.reflect.Type numberComparableType =
			testClass.getMethod("getNumberComparable").getGenericReturnType();

		Type stringComparable =
			Type.getType(stringComparableType, new ReferenceType(testClass));

		Type numberComparable =
			Type.getType(numberComparableType, new ReferenceType(testClass));

		assertNotEquals(stringComparable, numberComparable);
	}
}
