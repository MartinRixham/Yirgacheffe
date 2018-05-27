package yirgacheffe.compiler.type;

import org.junit.Test;
import org.objectweb.asm.Opcodes;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ParameterisedTypeTest
{
	@Test
	public void testTypeFromPackageAndIdentifier() throws Exception
	{
		Class<?> list =
			Thread.currentThread()
				.getContextClassLoader()
				.loadClass("java.util.List");

		Class<?> string =
			Thread.currentThread()
				.getContextClassLoader()
				.loadClass("java.lang.String");

		ReferenceType referenceType = new ReferenceType(list);
		Type typeParameter = new ReferenceType(string);
		Type type = new ParameterisedType(referenceType, Arrays.asList(typeParameter));

		assertEquals("java.util.List<java.lang.String>", type.toString());
		assertEquals(list, type.reflectionClass());
		assertEquals("java.util.List", type.toFullyQualifiedType());
		assertEquals("Ljava/util/List;", type.toJVMType());
		assertEquals(1, type.width());
		assertEquals(Opcodes.ARETURN, type.getReturnInstruction());
		assertEquals(Opcodes.ASTORE, type.getStoreInstruction());
		assertEquals(Opcodes.ALOAD, type.getLoadInstruction());
	}

	@Test
	public void testStringIsNotAssignableToSystem() throws Exception
	{
		Class<?> stringClass =
			Thread.currentThread()
				.getContextClassLoader()
				.loadClass("java.lang.String");

		Class<?> systemClass =
			Thread.currentThread()
				.getContextClassLoader()
				.loadClass("yirgacheffe.lang.System");

		Class<?> mutableReferenceClass =
			Thread.currentThread()
				.getContextClassLoader()
				.loadClass("yirgacheffe.lang.MutableReference");

		ReferenceType reference = new ReferenceType(mutableReferenceClass);
		Type string = new ReferenceType(stringClass);
		Type system = new ReferenceType(systemClass);

		Type stringReference = new ParameterisedType(reference, Arrays.asList(string));
		Type systemReference = new ParameterisedType(reference, Arrays.asList(system));

		assertFalse(stringReference.isAssignableTo(systemReference));
	}

	@Test
	public void testNotAssignableToReferenceType() throws Exception
	{
		Class<?> stringClass =
			Thread.currentThread()
				.getContextClassLoader()
				.loadClass("java.lang.String");

		Class<?> mutableReferenceClass =
			Thread.currentThread()
				.getContextClassLoader()
				.loadClass("yirgacheffe.lang.MutableReference");

		ReferenceType reference = new ReferenceType(mutableReferenceClass);
		Type string = new ReferenceType(stringClass);

		Type stringReference = new ParameterisedType(reference, Arrays.asList(string));

		assertFalse(stringReference.isAssignableTo(new ReferenceType(stringClass)));
	}
}
