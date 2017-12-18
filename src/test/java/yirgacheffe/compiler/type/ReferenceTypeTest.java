package yirgacheffe.compiler.type;

import org.junit.Test;
import org.objectweb.asm.Opcodes;

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

		assertEquals("java.lang.String", type.toString());
		assertEquals(loadedClass, type.reflectionClass());
		assertEquals("java.lang.String", type.toFullyQualifiedType());
		assertEquals("Ljava/lang/String;", type.toJVMType());
		assertEquals(1, type.width());
		assertEquals(Opcodes.ARETURN, type.getReturnOpcode());
	}
}
