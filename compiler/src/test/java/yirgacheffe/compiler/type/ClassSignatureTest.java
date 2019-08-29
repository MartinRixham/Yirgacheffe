package yirgacheffe.compiler.type;

import org.junit.Test;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ClassSignatureTest
{
	@Test
	public void testEmptySignature()
	{
		ClassSignature signature = new ClassSignature(new Array<>(), new Array<>());

		assertNull(signature.toString());
	}

	@Test
	public void testSignatureWithInterface()
	{
		ReferenceType type = new ReferenceType(Runnable.class);

		ClassSignature signature = new ClassSignature(new Array<>(type), new Array<>());

		assertEquals("Ljava/lang/Object;Ljava/lang/Runnable;", signature.toString());
	}

	@Test
	public void testSignatureWithParameter()
	{
		ClassSignature signature = new ClassSignature(new Array<>(), new Array<>("T"));

		assertEquals("<T:Ljava/lang/Object;>Ljava/lang/Object;", signature.toString());
	}

	@Test
	public void testSignatureImplementsComparableOfNum()
	{
		Type num = PrimitiveType.DOUBLE;
		ReferenceType comparable = new ReferenceType(Comparable.class);
		Type interfaceType = new ParameterisedType(comparable, new Array<>(num));

		ClassSignature signature =
			new ClassSignature(new Array<>(interfaceType), new Array<>());

		assertEquals(
			"Ljava/lang/Object;Ljava/lang/Comparable<Ljava/lang/Double;>;",
			signature.toString());
	}
}
