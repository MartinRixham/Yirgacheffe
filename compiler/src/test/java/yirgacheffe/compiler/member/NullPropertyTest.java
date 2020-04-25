package yirgacheffe.compiler.member;

import org.junit.Test;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NullPropertyTest
{
	@Test
	public void testNullProperty()
	{
		Property property = new NullProperty("name");

		assertEquals("name", property.getName());
		assertFalse(property.isStatic());
		assertTrue(property.getType() instanceof NullType);

		assertEquals(0,
			property.checkType(new Coordinate(1, 1), PrimitiveType.INT)
				.getInstructions()
				.length());
	}
}
