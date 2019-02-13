package yirgacheffe.compiler.function;

import org.junit.Test;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Literal;
import yirgacheffe.compiler.expression.This;
import yirgacheffe.compiler.type.Arguments;
import yirgacheffe.compiler.type.ParameterisedType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
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
		Variables variables = new Variables();
		Expression string = new Literal(new ReferenceType(String.class), "\"\"");
		Array<Expression> args = new Array<>(string);
		Arguments arguments = new Arguments(args, variables);
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
		assertEquals("Void println(java.lang.String)", result.getFunction().toString());
	}

	@Test
	public void testGettingBooleanPrintlnMethod()
	{
		Variables variables = new Variables();
		Expression bool = new Literal(PrimitiveType.BOOLEAN, "true");
		Array<Expression> args = new Array<>(bool);
		Arguments arguments = new Arguments(args, variables);
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
		assertEquals("Void println(Bool)", result.getFunction().toString());
	}

	@Test
	public void testFailedToMatchFunction()
	{
		Variables variables = new Variables();
		Type string = new ReferenceType(String.class);
		Method[] methods = string.reflectionClass().getMethods();
		Array<Callable> notMethods = new Array<>();
		Arguments arguments = new Arguments(new Array<>(), variables);

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
		Variables variables = new Variables();
		ReferenceType hashMap = new ReferenceType(HashMap.class);
		Type string = new ReferenceType(String.class);
		Array<Type> strings = new Array<>(string, string);
		Type mapType = new ParameterisedType(hashMap, strings);
		Array<Expression> args = new Array<>(new This(mapType));
		Arguments arguments = new Arguments(args, variables);
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
		Variables variables = new Variables();
		Type string = new ReferenceType(String.class);
		Method[] methods = string.reflectionClass().getMethods();
		Array<Callable> splitMethods = new Array<>();
		Array<Expression> args = new Array<>(new Literal(PrimitiveType.BOOLEAN, "true"));
		Arguments arguments = new Arguments(args, variables);

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
		Variables variables = new Variables();
		Type string = new ReferenceType(String.class);
		Constructor<?>[] constructors = string.reflectionClass().getConstructors();
		Array<Callable> callables = new Array<>();

		for (Constructor<?> constructor: constructors)
		{
			callables.push(new Function(string, constructor));
		}

		Array<Expression> args = new Array<>(new Literal(PrimitiveType.DOUBLE, "1"));
		Arguments arguments = new Arguments(args, variables);

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
		Variables variables = new Variables();
		ReferenceType mutableReference = new ReferenceType(MutableReference.class);
		Type string = new ReferenceType(String.class);
		Type object = new ReferenceType(Object.class);
		Type owner = new ParameterisedType(mutableReference, new Array<>(string));
		Method[] methods = mutableReference.reflectionClass().getMethods();
		Array<Callable> setMethods = new Array<>();
		Array<Expression> args = new Array<>(new This(object));
		Arguments arguments = new Arguments(args, variables);

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
