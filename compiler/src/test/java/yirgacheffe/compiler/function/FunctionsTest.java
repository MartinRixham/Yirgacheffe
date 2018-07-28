package yirgacheffe.compiler.function;

import org.junit.Test;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.type.Arguments;
import yirgacheffe.compiler.type.MismatchedTypes;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

import java.io.PrintStream;
import java.lang.reflect.Method;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class FunctionsTest
{
	@Test
	public void testGettingStringPrintlnMethod()
	{
		Array<Type> string = new Array<>(new ReferenceType(String.class));
		Arguments arguments = new Arguments(string);
		Type printStream = new ReferenceType(PrintStream.class);
		Method[] methods = printStream.reflectionClass().getMethods();
		Array<Callable> printlnMethods = new Array<>();

		for (Method method: methods)
		{
			if (method.getName().equals("println"))
			{
				printlnMethods.push(new Function(printStream, method));
			}
		}

		Functions functions =
			new Functions(
				new Coordinate(0, 1),
				"MyClass.thingy",
				printlnMethods);

		MatchResult result = functions.getMatchingExecutable(arguments);
		Array<MismatchedTypes> mismatchedParameters = result.getMismatchedParameters();

		assertTrue(result.isSuccessful());
		assertEquals(0, mismatchedParameters.length());
		assertEquals("(Ljava/lang/String;)V", result.getFunction().getDescriptor());
	}

	@Test
	public void testGettingBooleanPrintlnMethod()
	{
		Array<Type> bool = new Array<>(PrimitiveType.BOOLEAN);
		Arguments arguments = new Arguments(bool);
		Type printStream = new ReferenceType(PrintStream.class);
		Method[] methods = printStream.reflectionClass().getMethods();
		Array<Callable> printlnMethods = new Array<>();

		for (Method method: methods)
		{
			if (method.getName().equals("println"))
			{
				printlnMethods.push(new Function(printStream, method));
			}
		}

		Functions functions =
			new Functions(
				new Coordinate(0, 1),
				"MyClass.thingy",
				printlnMethods);

		MatchResult result = functions.getMatchingExecutable(arguments);
		Array<MismatchedTypes> mismatchedParameters = result.getMismatchedParameters();

		assertTrue(result.isSuccessful());
		assertEquals(0, mismatchedParameters.length());
		assertEquals("(Z)V", result.getFunction().getDescriptor());
	}

	@Test
	public void testAmbiguousMatchingOfBoxedAndUnboxedType()
	{
		Array<Type> character = new Array<>(PrimitiveType.CHAR);
		Arguments arguments = new Arguments(character);
		Type testClass = new ReferenceType(FunctionsTest.class);
		Method[] methods = testClass.reflectionClass().getMethods();
		Array<Callable> printlnMethods = new Array<>();

		for (Method method: methods)
		{
			if (method.getName().equals("testMethod"))
			{
				printlnMethods.push(new Function(testClass, method));
			}
		}

		Functions functions =
			new Functions(
				new Coordinate(0, 1),
				"MyClass.thingy",
				printlnMethods);

		MatchResult result = functions.getMatchingExecutable(arguments);

		assertTrue(result.isSuccessful());
	}

	public void testMethod(char character)
	{
	}

	public void testMethod(Character character)
	{
	}
}
