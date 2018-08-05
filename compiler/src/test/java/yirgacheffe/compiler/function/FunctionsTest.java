package yirgacheffe.compiler.function;

import org.junit.Test;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.type.Arguments;
import yirgacheffe.compiler.type.ParameterisedType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;
import yirgacheffe.lang.MutableReference;

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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
				printlnMethods,
				false);

		MatchResult result = functions.getMatchingExecutable(arguments);

		assertEquals(0, result.getErrors().length());
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
				printlnMethods,
				false);

		MatchResult result = functions.getMatchingExecutable(arguments);

		assertEquals(0, result.getErrors().length());
		assertEquals("(Z)V", result.getFunction().getDescriptor());
	}

	@Test
	public void testFailedToMatchFunction()
	{
		Type string = new ReferenceType(String.class);
		Method[] methods = string.reflectionClass().getMethods();
		Array<Callable> notMethods = new Array<>();
		Arguments arguments = new Arguments(new Array<>());

		for (Method method: methods)
		{
			if (method.getName().equals("notAMethod"))
			{
				notMethods.push(new Function(string, method));
			}
		}

		Functions functions =
			new Functions(
				new Coordinate(2, 3),
				"java.lang.String.notAMethod",
				notMethods,
				false);

		MatchResult result = functions.getMatchingExecutable(arguments);

		assertEquals(1, result.getErrors().length());
		assertEquals(
			"line 2:3 Method java.lang.String.notAMethod() not found.",
			result.getErrors().get(0).toString());
	}

	@Test
	public void testAmbiguousMatching()
	{
		ReferenceType hashMap = new ReferenceType(HashMap.class);
		Type string = new ReferenceType(String.class);
		Array<Type> strings = new Array<>(string, string);
		Array<Type> argumentTypes = new Array<>(new ParameterisedType(hashMap, strings));
		Arguments arguments = new Arguments(argumentTypes);
		Type testClass = new ReferenceType(FunctionsTest.class);
		Method[] methods = testClass.reflectionClass().getMethods();
		Array<Callable> printlnMethods = new Array<>();

		for (Method method: methods)
		{
			if (method.getName().equals("mapIt"))
			{
				printlnMethods.push(new Function(testClass, method));
			}
		}

		Functions functions =
			new Functions(
				new Coordinate(4, 4),
				"FunctionsTest.mapIt",
				printlnMethods,
				false);

		MatchResult result = functions.getMatchingExecutable(arguments);

		assertEquals(1, result.getErrors().length());
		assertEquals(
			"line 4:4 Ambiguous call to method FunctionsTest.mapIt" +
			"(java.util.HashMap<java.lang.String,java.lang.String>).",
			result.getErrors().get(0).toString());
	}

	@Test
	public void testMismatchedParameters()
	{
		Type string = new ReferenceType(String.class);
		Method[] methods = string.reflectionClass().getMethods();
		Array<Callable> splitMethods = new Array<>();
		Arguments arguments = new Arguments(new Array<>(PrimitiveType.BOOLEAN));

		for (Method method: methods)
		{
			if (method.getName().equals("split"))
			{
				splitMethods.push(new Function(string, method));
			}
		}

		Functions functions =
			new Functions(
				new Coordinate(2, 3),
				"java.lang.String.split",
				splitMethods,
				false);

		MatchResult result = functions.getMatchingExecutable(arguments);

		assertEquals(1, result.getErrors().length());
		assertEquals(
			"line 2:3 Method java.lang.String.split(Bool) not found.",
			result.getErrors().get(0).toString());
	}

	@Test
	public void testConstructorCallWithWrongArgument()
	{
		Type string = new ReferenceType(String.class);
		Constructor<?>[] constructors = string.reflectionClass().getConstructors();
		Array<Callable> callables = new Array<>();

		for (Constructor<?> constructor: constructors)
		{
			callables.push(new Function(string, constructor));
		}

		Arguments arguments = new Arguments(new Array<>(PrimitiveType.DOUBLE));

		Functions functions =
			new Functions(
				new Coordinate(3, 5),
				"java.lang.String",
				callables,
				true);

		MatchResult result = functions.getMatchingExecutable(arguments);

		assertEquals(1, result.getErrors().length());
		assertEquals(
			"line 3:5 Constructor java.lang.String(Num) not found.",
			result.getErrors().get(0).toString());
	}

	@Test
	public void testMethodCallWithMismatchedTypeParameter()
	{
		ReferenceType mutableReference = new ReferenceType(MutableReference.class);
		Type string = new ReferenceType(String.class);
		Type object = new ReferenceType(Object.class);
		Type owner = new ParameterisedType(mutableReference, new Array<>(string));
		Method[] methods = mutableReference.reflectionClass().getMethods();
		Array<Callable> setMethods = new Array<>();
		Arguments arguments = new Arguments(new Array<>(object));

		for (Method method: methods)
		{
			if (method.getName().equals("set"))
			{
				setMethods.push(new Function(owner, method));
			}
		}

		Functions functions =
			new Functions(
				new Coordinate(3, 5),
				"yirgacheffe.lang.MutableReference.set",
				setMethods,
				false);

		MatchResult result = functions.getMatchingExecutable(arguments);

		assertEquals(1, result.getErrors().length());
		assertEquals(
			"line 3:5 Argument of type java.lang.Object cannot be assigned to " +
				"generic parameter of type java.lang.String.",
			result.getErrors().get(0).toString());
	}

	public void mapIt(Map<String, String> map)
	{
	}

	public void mapIt(Object map)
	{
	}
}
