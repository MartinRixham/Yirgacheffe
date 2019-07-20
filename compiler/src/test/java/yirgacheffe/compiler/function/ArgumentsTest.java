package yirgacheffe.compiler.function;

import org.junit.Test;
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
		Variables variables = new LocalVariables(new HashMap<>());
		Expression string = new Streeng("\"\"");
		Array<Expression> args = new Array<>(string);
		Arguments arguments = new Arguments(args, variables);
		Type printStream = new ReferenceType(PrintStream.class);
		Method[] methods = printStream.reflectionClass().getMethods();
		MatchResult matchResult = new FailedMatchResult();

		for (Method method: methods)
		{
			if (method.getName().equals("println"))
			{
				Function function = new Function(printStream, method);

				matchResult = matchResult.betterOf(arguments.matches(function));
			}
		}

		assertTrue(matchResult instanceof SuccessfulMatchResult);
		assertEquals(1000, matchResult.score());
		assertEquals("(Ljava/lang/String;)V", matchResult.getDescriptor());
		assertEquals("println", matchResult.getName());
		assertEquals(0, matchResult.getMismatchedParameters().length());
		assertEquals(PrimitiveType.VOID, matchResult.getReturnType());
	}

	@Test
	public void testGettingBooleanPrintlnMethod()
	{
		Variables variables = new LocalVariables(new HashMap<>());
		Expression bool = new Bool("true");
		Array<Expression> args = new Array<>(bool);
		Arguments arguments = new Arguments(args, variables);
		Type printStream = new ReferenceType(PrintStream.class);
		Method[] methods = printStream.reflectionClass().getMethods();
		MatchResult matchResult = new FailedMatchResult();

		for (Method method: methods)
		{
			if (method.getName().equals("println"))
			{
				Function function = new Function(printStream, method);

				matchResult = matchResult.betterOf(arguments.matches(function));
			}
		}

		assertTrue(matchResult instanceof SuccessfulMatchResult);
		assertEquals(1000, matchResult.score());
		assertEquals("(Z)V", matchResult.getDescriptor());
		assertEquals("println", matchResult.getName());
		assertEquals(0, matchResult.getMismatchedParameters().length());
		assertEquals(PrimitiveType.VOID, matchResult.getReturnType());
	}

	@Test
	public void testFailedToMatchFunction()
	{
		Variables variables = new LocalVariables(new HashMap<>());
		Type string = new ReferenceType(String.class);
		Method[] methods = string.reflectionClass().getMethods();
		Arguments arguments = new Arguments(new Array<>(), variables);
		MatchResult matchResult = new FailedMatchResult();

		for (Method method: methods)
		{
			if (method.getName().equals("notAMethod"))
			{
				Function function = new Function(string, method);

				matchResult = matchResult.betterOf(arguments.matches(function));
			}
		}

		assertTrue(matchResult instanceof FailedMatchResult);
		assertEquals(-1, matchResult.score());
		assertEquals("()V", matchResult.getDescriptor());
		assertEquals("", matchResult.getName());
		assertEquals(0, matchResult.getMismatchedParameters().length());
		assertTrue(matchResult.getReturnType() instanceof NullType);
	}

	@Test
	public void testAmbiguousMatching()
	{
		Variables variables = new LocalVariables(new HashMap<>());
		ReferenceType hashMap = new ReferenceType(HashMap.class);
		Type string = new ReferenceType(String.class);
		Array<Type> strings = new Array<>(string, string);
		Type mapType = new ParameterisedType(hashMap, strings);
		Array<Expression> args = new Array<>(new This(mapType));
		Arguments arguments = new Arguments(args, variables);
		Type testClass = new ReferenceType(ArgumentsTest.class);
		Method[] methods = testClass.reflectionClass().getMethods();
		MatchResult matchResult = new FailedMatchResult();

		for (Method method: methods)
		{
			if (method.getName().equals("mapIt"))
			{
				Function function = new Function(testClass, method);

				matchResult = matchResult.betterOf(arguments.matches(function));
			}
		}

		assertTrue(matchResult instanceof AmbiguousMatchResult);
		assertEquals(0, matchResult.score());
		assertEquals("()V", matchResult.getDescriptor());
		assertEquals("", matchResult.getName());
		assertEquals(0, matchResult.getMismatchedParameters().length());
		assertTrue(matchResult.getReturnType() instanceof NullType);
	}

	@Test
	public void testMismatchedParameters()
	{
		Variables variables = new LocalVariables(new HashMap<>());
		Type string = new ReferenceType(String.class);
		Method[] methods = string.reflectionClass().getMethods();
		Array<Expression> args = new Array<>(new Bool("true"));
		Arguments arguments = new Arguments(args, variables);
		MatchResult matchResult = new FailedMatchResult();

		for (Method method: methods)
		{
			if (method.getName().equals("split"))
			{
				Function function = new Function(string, method);

				matchResult = matchResult.betterOf(arguments.matches(function));
			}
		}

		assertTrue(matchResult instanceof FailedMatchResult);
		assertEquals(-1, matchResult.score());
		assertEquals("()V", matchResult.getDescriptor());
		assertEquals("", matchResult.getName());
		assertEquals(0, matchResult.getMismatchedParameters().length());
		assertTrue(matchResult.getReturnType() instanceof NullType);
	}

	@Test
	public void testConstructorCallWithWrongArgument()
	{
		Variables variables = new LocalVariables(new HashMap<>());
		Type string = new ReferenceType(String.class);
		Constructor<?>[] constructors = string.reflectionClass().getConstructors();
		Array<Expression> args = new Array<>(new Num("1"));
		Arguments arguments = new Arguments(args, variables);
		MatchResult matchResult = new FailedMatchResult();

		for (Constructor<?> constructor: constructors)
		{
			Function function = new Function(string, constructor);

			matchResult = matchResult.betterOf(arguments.matches(function));
		}

		assertTrue(matchResult instanceof FailedMatchResult);
		assertEquals(-1, matchResult.score());
		assertEquals("()V", matchResult.getDescriptor());
		assertEquals("", matchResult.getName());
		assertEquals(0, matchResult.getMismatchedParameters().length());
		assertTrue(matchResult.getReturnType() instanceof NullType);
	}

	@Test
	public void testMethodCallWithMismatchedTypeParameter()
	{
		Variables variables = new LocalVariables(new HashMap<>());
		ReferenceType mutableReference = new ReferenceType(MutableReference.class);
		Type string = new ReferenceType(String.class);
		Type object = new ReferenceType(Object.class);
		Type owner = new ParameterisedType(mutableReference, new Array<>(string));
		Method[] methods = mutableReference.reflectionClass().getMethods();
		Array<Expression> args = new Array<>(new This(object));
		Arguments arguments = new Arguments(args, variables);
		MatchResult matchResult = new FailedMatchResult();

		for (Method method: methods)
		{
			if (method.getName().equals("set"))
			{
				Function function = new Function(owner, method);

				matchResult = matchResult.betterOf(arguments.matches(function));
			}
		}

		assertTrue(matchResult instanceof SuccessfulMatchResult);
		assertEquals(1000, matchResult.score());
		assertEquals("(Ljava/lang/Object;)V", matchResult.getDescriptor());
		assertEquals("set", matchResult.getName());
		assertEquals(1, matchResult.getMismatchedParameters().length());
		assertEquals(PrimitiveType.VOID, matchResult.getReturnType());
	}

	@Test
	public void testArrayConstructor()
	{
		Variables variables = new LocalVariables(new HashMap<>());
		Type string = new ReferenceType(String.class);
		Array<Type> typeParams = new Array<>(string);
		Type array = new ParameterisedType(new ReferenceType(Array.class), typeParams);
		Constructor<?>[] constructors = array.reflectionClass().getConstructors();
		Expression argument = new This(new ArrayType("[Ljava.lang.String;", string));
		Array<Expression> args = new Array<>(argument);
		Arguments arguments = new Arguments(args, variables);
		MatchResult matchResult = new FailedMatchResult();

		for (Constructor<?> constructor: constructors)
		{
			Function function = new Function(array, constructor);

			matchResult = matchResult.betterOf(arguments.matches(function));
		}

		assertTrue(matchResult instanceof SuccessfulMatchResult);
		assertEquals(0, matchResult.score());
		assertEquals("([Ljava/lang/Object;)V", matchResult.getDescriptor());
		assertEquals("yirgacheffe.lang.Array", matchResult.getName());
		assertEquals(0, matchResult.getMismatchedParameters().length());
		assertEquals(PrimitiveType.VOID, matchResult.getReturnType());
	}
}
