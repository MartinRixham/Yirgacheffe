package yirgacheffe.compiler.type;

import org.junit.Test;
import yirgacheffe.compiler.Source;
import yirgacheffe.parser.YirgacheffeParser;

import static org.junit.Assert.assertEquals;

public class ReferenceTypeTest
{
	@Test
	public void testTypeFromPackageAndIdentifier()
	{
		Type type = new ReferenceType("some.pkg", "MyClass");

		assertEquals("some.pkg.MyClass", type.toFullyQualifiedType());
		assertEquals("Lsome/pkg/MyClass;", type.toJVMType());
		assertEquals(1, type.width());
	}

	@Test
	public void testTypeFromTypeContext()
	{
		YirgacheffeParser parser = new Source("some.pkg.MyClass").parse();
		YirgacheffeParser.TypeContext context = parser.type();

		Type type = new ReferenceType(context);

		assertEquals("some.pkg.MyClass", type.toFullyQualifiedType());
		assertEquals("Lsome/pkg/MyClass;", type.toJVMType());
		assertEquals(1, type.width());
	}

	@Test
	public void testTypeFromFullyQualifiedTypeContext()
	{
		YirgacheffeParser parser = new Source("some.pkg.MyClass").parse();
		YirgacheffeParser.FullyQualifiedTypeContext context = parser.fullyQualifiedType();

		Type type = new ReferenceType(context);

		assertEquals("some.pkg.MyClass", type.toFullyQualifiedType());
		assertEquals("Lsome/pkg/MyClass;", type.toJVMType());
		assertEquals(1, type.width());
	}
}
