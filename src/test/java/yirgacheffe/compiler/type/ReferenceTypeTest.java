package yirgacheffe.compiler.type;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReferenceTypeTest
{
	@Test
	public void testTypeFromPackageAndIdentifier() throws Exception
	{
		Class<?> loadedClass =
			Thread.currentThread()
				.getContextClassLoader()
				.loadClass("java.lang.String");

		Type type = new ReferenceType(loadedClass);

		assertEquals(loadedClass, type.reflectionClass());
		assertEquals("java.lang.String", type.toFullyQualifiedType());
		assertEquals("Ljava/lang/String;", type.toJVMType());
		assertEquals(1, type.width());
	}
}
