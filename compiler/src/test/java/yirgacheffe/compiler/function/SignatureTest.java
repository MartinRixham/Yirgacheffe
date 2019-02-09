package yirgacheffe.compiler.function;

import org.junit.Test;
import yirgacheffe.compiler.type.ParameterisedType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

import java.util.Map;

import static junit.framework.TestCase.assertEquals;
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
	public void testGenericSignature()
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
}
