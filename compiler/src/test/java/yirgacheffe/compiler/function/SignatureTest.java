package yirgacheffe.compiler.function;

import org.junit.Test;
import yirgacheffe.compiler.type.ParameterisedType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

public class SignatureTest
{
	@Test
	public void testSignature()
	{
		String name = "method";
		Array<Type> parameters = new Array<>(PrimitiveType.DOUBLE);
		Signature signature = new Signature(PrimitiveType.DOUBLE, name, parameters);

		assertEquals("(D)D", signature.getDescriptor());
		assertNull(signature.getSignature());
		assertEquals(name.hashCode() + parameters.hashCode(), signature.hashCode());
	}

	@Test
	public void testSignatureWithGenericReturnType()
	{
		String name = "method";
		Type string = new ReferenceType(String.class);
		Array<Type> typeParams = new Array<>(string, string);
		Type returnType = new ParameterisedType(new ReferenceType(Map.class), typeParams);
		Array<Type> parameters = new Array<>(PrimitiveType.DOUBLE);
		Signature signature = new Signature(returnType, name, parameters);

		assertEquals("(D)Ljava/util/Map;", signature.getDescriptor());

		assertEquals(
			"(D)Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;",
			signature.getSignature());

		assertEquals(name.hashCode() + parameters.hashCode(), signature.hashCode());
	}

	@Test
	public void testSignatureWithGenericParameter()
	{
		String name = "method";
		Type string = new ReferenceType(String.class);
		Array<Type> typeParams = new Array<>(string, string);
		Type map = new ParameterisedType(new ReferenceType(Map.class), typeParams);
		Array<Type> parameters = new Array<>(map);
		Signature signature = new Signature(PrimitiveType.DOUBLE, name, parameters);

		assertEquals("(Ljava/util/Map;)D", signature.getDescriptor());

		assertEquals(
			"(Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;)D",
			signature.getSignature());

		assertEquals(name.hashCode() + parameters.hashCode(), signature.hashCode());
	}

	@Test
	public void testSignaturesWithUnequalNames()
	{
		Type dub = PrimitiveType.DOUBLE;
		Array<Type> parameters = new Array<>(dub);
		Signature firstSignature = new Signature(dub, "thingy", parameters);
		Signature secondSignature = new Signature(dub, "sumpt", parameters);

		assertNotEquals(firstSignature, secondSignature);
		assertFalse(firstSignature.isImplementedBy(secondSignature));
	}

	@Test
	public void testSignaturesWithUnequalParameters()
	{
		Type dub = PrimitiveType.DOUBLE;
		Array<Type> firstParameters = new Array<>(dub);
		Array<Type> secondParameters = new Array<>(new ReferenceType(String.class));
		Signature firstSignature = new Signature(dub, "thingy", firstParameters);
		Signature secondSignature = new Signature(dub, "thingy", secondParameters);

		assertNotEquals(firstSignature, secondSignature);
		assertFalse(firstSignature.isImplementedBy(secondSignature));
	}
}
