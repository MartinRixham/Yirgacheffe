package yirgacheffe.compiler.type;

import org.junit.Test;
import org.objectweb.asm.Opcodes;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ParameterisedTypeTest
{
	@Test
	public void testTypeWithOneParameter()
	{
		ReferenceType referenceType = new ReferenceType(java.util.List.class);
		Type typeParameter = new ReferenceType(java.lang.String.class);
		Type type = new ParameterisedType(referenceType, Arrays.asList(typeParameter));

		assertEquals("java.util.List<java.lang.String>", type.toString());
		assertEquals(java.util.List.class, type.reflectionClass());
		assertEquals("java.util.List", type.toFullyQualifiedType());
		assertEquals("Ljava/util/List;", type.toJVMType());
		assertEquals(1, type.width());
		assertEquals(Opcodes.ARETURN, type.getReturnInstruction());
		assertEquals(Opcodes.ASTORE, type.getStoreInstruction());
		assertEquals(Opcodes.ALOAD, type.getLoadInstruction());
	}

	@Test
	public void testTypeWithTwoParameters()
	{
		ReferenceType referenceType = new ReferenceType(java.util.Map.class);
		Type firstParameter = new ReferenceType(java.lang.String.class);
		Type secondParameter = PrimitiveType.DOUBLE;
		List<Type> parameters = Arrays.asList(firstParameter, secondParameter);
		Type type = new ParameterisedType(referenceType, parameters);

		assertEquals("java.util.Map<java.lang.String,Num>", type.toString());
	}

	@Test
	public void testStringIsNotAssignableToSystem()
	{
		ReferenceType reference =
			new ReferenceType(yirgacheffe.lang.MutableReference.class);

		Type string = new ReferenceType(java.lang.String.class);
		Type system = new ReferenceType(java.lang.System.class);

		Type stringReference = new ParameterisedType(reference, Arrays.asList(string));
		Type systemReference = new ParameterisedType(reference, Arrays.asList(system));

		assertFalse(stringReference.isAssignableTo(systemReference));
	}

	@Test
	public void testNotAssignableToReferenceType()
	{

		ReferenceType reference =
			new ReferenceType(yirgacheffe.lang.MutableReference.class);
		Type string = new ReferenceType(java.lang.String.class);

		Type stringReference = new ParameterisedType(reference, Arrays.asList(string));

		assertFalse(stringReference.isAssignableTo(string));
	}
}
