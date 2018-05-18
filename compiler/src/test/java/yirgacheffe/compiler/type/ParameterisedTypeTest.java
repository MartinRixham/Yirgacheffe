package yirgacheffe.compiler.type;

import org.junit.Test;
import org.objectweb.asm.Opcodes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ParameterisedTypeTest
{
	@Test
	public void testTypeFromPackageAndIdentifier() throws Exception
	{
		Class<?> loadedClass =
			Thread.currentThread()
				.getContextClassLoader()
				.loadClass("java.lang.String");

		ReferenceType referenceType = new ReferenceType(loadedClass);
		Type typeParameter = new ReferenceType(loadedClass);
		Type type = new ParameterisedType(referenceType, typeParameter);

		assertEquals("java.lang.String<java.lang.String>", type.toString());
		assertEquals(loadedClass, type.reflectionClass());
		assertEquals("java.lang.String", type.toFullyQualifiedType());
		assertEquals("Ljava/lang/String;", type.toJVMType());
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

		Type stringReferencce = new ParameterisedType(reference, string);
		Type systemReference = new ParameterisedType(reference, system);

		assertFalse(stringReferencce.isAssignableTo(systemReference));
	}
}
