package yirgacheffe.compiler.function;

import org.junit.Test;
import org.objectweb.asm.Label;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NullSignatureTest
{
	@Test
	public void testNullSignature()
	{
		Signature signature = new NullSignature();

		assertEquals("", signature.getName());
		assertEquals("()V", signature.getDescriptor());
		assertEquals("()V", signature.getSignature());
		assertTrue(signature.getLabel() instanceof Label);
		assertTrue(signature.getReturnType() instanceof NullType);
		assertEquals(new Array<>(), signature.getParameters());
		assertTrue(signature.isImplementedBy(signature));
	}
}
