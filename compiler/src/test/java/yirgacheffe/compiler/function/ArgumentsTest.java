package yirgacheffe.compiler.function;

import org.junit.Test;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Bool;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Num;
import yirgacheffe.compiler.expression.Streeng;
import yirgacheffe.compiler.expression.This;
import yirgacheffe.compiler.type.ArrayType;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.ParameterisedType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;
import yirgacheffe.lang.MutableReference;

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArgumentsTest
{
	public void mapIt(Map<String, String> map)
	{
	}

	public void mapIt(Object map)
	{
	}

	@Test
	public void testGettingStringPrintlnMethod()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		Variables variables = new LocalVariables(1, new HashMap<>());
		Expression string = new Streeng(coordinate, "\"\"");
		Array<Expression> args = new Array<>(string);
		Arguments arguments = new Arguments(coordinate, "method", args, variables);
		Type printStream = new ReferenceType(PrintStream.class);
		Method[] methods = printStream.reflectionClass().getMethods();
		MatchResult matchResult = new FailedMatchResult(coordinate, "method");

		for (Method method: methods)
		{
			if (method.getName().equals("println"))
			{
				Function function = new Function(printStream, method);

				matchResult = matchResult.betterOf(arguments.matches(function));
			}
		}

		assertTrue(matchResult instanceof SuccessfulMatchResult);
		assertEquals("println", matchResult.getName());
		assertEquals(0, matchResult.compileArguments(variables).getErrors().length());
		assertEquals(PrimitiveType.VOID, matchResult.getReturnType());

		Array<Type> parameterTypes = matchResult.getParameterTypes();

		assertEquals(1, parameterTypes.length());
		assertEquals(new ReferenceType(String.class), parameterTypes.get(0));
	}

	@Test
	public void testGettingBooleanPrintlnMethod()
	{
		Coordinate coordinate = new Coordinate(4, 56);
		Variables variables = new LocalVariables(1, new HashMap<>());
		Expression bool = new Bool(coordinate, "true");
		Array<Expression> args = new Array<>(bool);
		Arguments arguments = new Arguments(coordinate, "method", args, variables);
		Type printStream = new ReferenceType(PrintStream.class);
		Method[] methods = printStream.reflectionClass().getMethods();
		MatchResult matchResult = new FailedMatchResult(coordinate, "method");

		for (Method method: methods)
		{
			if (method.getName().equals("println"))
			{
				Function function = new Function(printStream, method);

				matchResult = matchResult.betterOf(arguments.matches(function));
			}
		}

		assertTrue(matchResult instanceof SuccessfulMatchResult);
		assertEquals("println", matchResult.getName());
		assertEquals(0, matchResult.compileArguments(variables).getErrors().length());
		assertEquals(PrimitiveType.VOID, matchResult.getReturnType());

		Array<Type> parameterTypes = matchResult.getParameterTypes();

		assertEquals(1, parameterTypes.length());
		assertEquals(PrimitiveType.BOOLEAN, parameterTypes.get(0));
	}

	@Test
	public void testFailedToMatchFunction()
	{
		Coordinate coordinate = new Coordinate(3, 6);
		Variables variables = new LocalVariables(1, new HashMap<>());
		Type string = new ReferenceType(String.class);
		Method[] methods = string.reflectionClass().getMethods();

		Arguments arguments =
			new Arguments(coordinate, "method", new Array<>(), variables);

		MatchResult matchResult = arguments.matches();

		for (Method method: methods)
		{
			if (method.getName().equals("notAMethod"))
			{
				Function function = new Function(string, method);

				matchResult = matchResult.betterOf(arguments.matches(function));
			}
		}

		assertTrue(matchResult instanceof FailedMatchResult);
		assertEquals("", matchResult.getName());
		assertTrue(matchResult.getReturnType() instanceof NullType);

		Array<Error> errors =  matchResult.compileArguments(variables).getErrors();

		assertEquals(1, errors.length());
		assertEquals("line 3:6 Invoked method() not found.", errors.get(0).toString());

		Array<Type> parameterTypes = matchResult.getParameterTypes();

		assertEquals(0, parameterTypes.length());
	}

	@Test
	public void testAmbiguousMatching()
	{
		Coordinate coordinate = new Coordinate(3, 65);
		Variables variables = new LocalVariables(1, new HashMap<>());
		ReferenceType hashMap = new ReferenceType(HashMap.class);
		Type string = new ReferenceType(String.class);
		Array<Type> strings = new Array<>(string, string);
		Type mapType = new ParameterisedType(hashMap, strings);
		Array<Expression> args = new Array<>(new This(coordinate, mapType));
		Arguments arguments = new Arguments(coordinate, "method", args, variables);
		Type testClass = new ReferenceType(ArgumentsTest.class);
		Method[] methods = testClass.reflectionClass().getMethods();
		MatchResult matchResult = new FailedMatchResult(coordinate, "method");

		for (Method method: methods)
		{
			if (method.getName().equals("mapIt"))
			{
				Function function = new Function(testClass, method);

				matchResult = matchResult.betterOf(arguments.matches(function));
			}
		}

		assertTrue(matchResult instanceof AmbiguousMatchResult);
		assertEquals("", matchResult.getName());
		assertTrue(matchResult.getReturnType() instanceof NullType);

		Array<Error> errors =  matchResult.compileArguments(variables).getErrors();

		assertEquals(1, errors.length());

		assertEquals(
			"line 3:65 Ambiguous call to " +
			"method(java.util.HashMap<java.lang.String,java.lang.String>).",
			errors.get(0).toString());

		Array<Type> parameterTypes = matchResult.getParameterTypes();

		assertEquals(0, parameterTypes.length());
	}

	@Test
	public void testMismatchedParameters()
	{
		Coordinate coordinate = new Coordinate(3, 65);
		Variables variables = new LocalVariables(1, new HashMap<>());
		Type string = new ReferenceType(String.class);
		Method[] methods = string.reflectionClass().getMethods();
		Array<Expression> args = new Array<>(new Bool(coordinate, "true"));
		Arguments arguments = new Arguments(coordinate, "method", args, variables);
		MatchResult matchResult = new FailedMatchResult(coordinate, "method");

		for (Method method: methods)
		{
			if (method.getName().equals("split"))
			{
				Function function = new Function(string, method);

				matchResult = matchResult.betterOf(arguments.matches(function));
			}
		}

		assertTrue(matchResult instanceof FailedMatchResult);
		assertEquals("", matchResult.getName());
		assertTrue(matchResult.getReturnType() instanceof NullType);

		Array<Error> errors = matchResult.compileArguments(variables).getErrors();

		assertEquals(1, errors.length());
		assertEquals(
			"line 3:65 Invoked method(Bool) not found.",
			errors.get(0).toString());

		Array<Type> parameterTypes = matchResult.getParameterTypes();

		assertEquals(0, parameterTypes.length());
	}

	@Test
	public void testConstructorCallWithWrongArgument()
	{
		Coordinate coordinate = new Coordinate(3, 34);
		Variables variables = new LocalVariables(1, new HashMap<>());
		Type string = new ReferenceType(String.class);
		Constructor<?>[] constructors = string.reflectionClass().getConstructors();
		Array<Expression> args = new Array<>(new Num(coordinate, "1"));
		Arguments arguments = new Arguments(coordinate, "method", args, variables);
		MatchResult matchResult = new FailedMatchResult(coordinate, "method");

		for (Constructor<?> constructor: constructors)
		{
			Function function = new Function(string, constructor);

			matchResult = matchResult.betterOf(arguments.matches(function));
		}

		assertTrue(matchResult instanceof FailedMatchResult);
		assertEquals("", matchResult.getName());
		assertTrue(matchResult.getReturnType() instanceof NullType);

		Array<Error> errors = matchResult.compileArguments(variables).getErrors();

		assertEquals(1, errors.length());

		assertEquals(
			"line 3:34 Invoked method(Num) not found.",
			errors.get(0).toString());

		Array<Type> parameterTypes = matchResult.getParameterTypes();

		assertEquals(0, parameterTypes.length());
	}

	@Test
	public void testMethodCallWithMismatchedTypeParameter()
	{
		Coordinate coordinate = new Coordinate(3, 4);
		Variables variables = new LocalVariables(1, new HashMap<>());
		ReferenceType mutableReference = new ReferenceType(MutableReference.class);
		Type string = new ReferenceType(String.class);
		Type object = new ReferenceType(Object.class);
		Type owner = new ParameterisedType(mutableReference, new Array<>(string));
		Method[] methods = mutableReference.reflectionClass().getMethods();
		Array<Expression> args = new Array<>(new This(coordinate, object));
		Arguments arguments = new Arguments(coordinate, "method", args, variables);
		MatchResult matchResult = new FailedMatchResult(coordinate, "method");

		for (Method method: methods)
		{
			if (method.getName().equals("set"))
			{
				Function function = new Function(owner, method);

				matchResult = matchResult.betterOf(arguments.matches(function));
			}
		}

		assertTrue(matchResult instanceof SuccessfulMatchResult);
		assertEquals("set", matchResult.getName());
		assertEquals(1, matchResult.compileArguments(variables).getErrors().length());
		assertEquals(PrimitiveType.VOID, matchResult.getReturnType());

		Array<Type> parameterTypes = matchResult.getParameterTypes();

		assertEquals(1, parameterTypes.length());
		assertEquals("java/lang/String", parameterTypes.get(0).toFullyQualifiedType());
	}

	@Test
	public void testArrayConstructor()
	{
		Coordinate coordinate = new Coordinate(3, 45);
		Variables variables = new LocalVariables(1, new HashMap<>());
		Type string = new ReferenceType(String.class);
		Array<Type> typeParams = new Array<>(string);
		Type array = new ParameterisedType(new ReferenceType(Array.class), typeParams);
		Constructor<?>[] constructors = array.reflectionClass().getConstructors();
		ArrayType arrayType = new ArrayType("[Ljava.lang.String;", string);
		Expression argument = new This(coordinate, arrayType);
		Array<Expression> args = new Array<>(argument);
		Arguments arguments = new Arguments(coordinate, "method", args, variables);
		MatchResult matchResult = new FailedMatchResult(coordinate, "method");

		for (Constructor<?> constructor: constructors)
		{
			Function function = new Function(array, constructor);

			matchResult = matchResult.betterOf(arguments.matches(function));
		}

		assertTrue(matchResult instanceof SuccessfulMatchResult);
		assertEquals("yirgacheffe.lang.Array", matchResult.getName());
		assertEquals(0, matchResult.compileArguments(variables).getErrors().length());
		assertEquals(PrimitiveType.VOID, matchResult.getReturnType());

		Array<Type> parameterTypes = matchResult.getParameterTypes();

		assertEquals(1, parameterTypes.length());
		assertEquals(
			new ArrayType("[Ljava.lang.Object;", new ReferenceType(Object.class)),
			parameterTypes.get(0));
	}
}
