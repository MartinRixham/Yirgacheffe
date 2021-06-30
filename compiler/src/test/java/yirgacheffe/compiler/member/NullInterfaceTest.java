package yirgacheffe.compiler.member;

import org.junit.Test;
import yirgacheffe.compiler.function.NullFunction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NullInterfaceTest
{
	@Test
	public void testNullInterface()
	{
		Interface members = new NullInterface();

		assertEquals("", members.getSimpleName());
		assertFalse(members.isInterface());
		assertFalse(members.doesImplement(String.class));
		assertTrue(members.isImplementedBy(String.class));
		assertTrue(members.getField("") instanceof NullProperty);
		assertEquals(0, members.getFields().size());
		assertTrue(members.hasDefaultConstructor());
		assertTrue(members.hasMethod(""));
		assertEquals(0, members.getTypeParameters().length());
		assertTrue(members.getMethods().iterator().next() instanceof NullFunction);
		assertTrue(members.getPublicMethods().iterator().next() instanceof NullFunction);
		assertTrue(members.getConstructors().iterator().next() instanceof NullFunction);
		assertEquals(0, members.getGenericInterfaces().size());

		assertTrue(
			members.getPublicConstructors().iterator().next() instanceof NullFunction);
	}
}
