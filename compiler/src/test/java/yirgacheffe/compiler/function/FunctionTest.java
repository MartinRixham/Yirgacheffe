package yirgacheffe.compiler.function;

import org.junit.Test;
import yirgacheffe.compiler.type.ReferenceType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class FunctionTest
{
	@Test
	public void testEqualFunctions() throws Exception
	{
		Class<?> string = "".getClass();

		Function firstFunction =
			new ClassFunction(
				new ReferenceType(string),
				string.getMethod("toString"));

		Function secondFunction =
			new ClassFunction(
				new ReferenceType(string),
				string.getMethod("toString"));

		assertEquals(firstFunction, secondFunction);
		assertEquals(firstFunction.hashCode(), secondFunction.hashCode());
	}

	@Test
	public void testUnequalFunctions() throws Exception
	{
		Class<?> string = "".getClass();

		Function firstFunction =
			new ClassFunction(
				new ReferenceType(string),
				string.getMethod("toString"));

		Function secondFunction =
			new ClassFunction(
				new ReferenceType(string),
				string.getMethod("hashCode"));

		assertNotEquals(firstFunction, secondFunction);
		assertNotEquals(firstFunction.hashCode(), secondFunction.hashCode());
	}

	@Test
	public void testFunctionNotEqualsToString() throws Exception
	{
		Class<?> string = "".getClass();

		Function function =
			new ClassFunction(
				new ReferenceType(string),
				string.getMethod("toString"));

		assertNotEquals(function, "");
		assertNotEquals(function.hashCode(), "".hashCode());
	}
}
