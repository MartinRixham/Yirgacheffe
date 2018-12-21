package yirgacheffe.compiler.function;

import org.junit.Test;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

import static junit.framework.TestCase.assertEquals;

public class SignatureTest
{
	@Test
	public void testHashCode()
	{
		String name = "method";
		Array<Type> parameters = new Array<>(PrimitiveType.DOUBLE);
		Signature signature = new Signature(name, parameters);

		assertEquals(name.hashCode() + parameters.hashCode(), signature.hashCode());
	}
}
