package yirgacheffe.compiler.member;

import org.junit.Test;
import yirgacheffe.compiler.type.PrimitiveType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ClassInterfaceTest
{
	@Test
	public void testEqualInterfaces()
	{
		ClassInterface first = new ClassInterface(PrimitiveType.INT, Integer.class);
		ClassInterface second = new ClassInterface(PrimitiveType.INT, Integer.class);

		assertEquals(first, second);
	}

	@Test
	public void testUnequalInterfaces()
	{
		ClassInterface first = new ClassInterface(PrimitiveType.INT, Integer.class);
		ClassInterface second = new ClassInterface(PrimitiveType.INT, Double.class);

		assertNotEquals(first, second);
	}
}
