package yirgacheffe.compiler.type;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JavaLanguageTypeTest
{
	@Test
	public void testString()
	{
		Type string = new JavaLanguageType("String");

		assertEquals("java.lang.String", string.reflectionClass().getName());
		assertEquals("Ljava/lang/String;", string.toJVMType());
		assertEquals("java.lang.String", string.toFullyQualifiedType());
		assertEquals(1, string.width());
	}
}
