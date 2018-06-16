package yirgacheffe.compiler.type;

import org.junit.Test;
import yirgacheffe.compiler.MatchResult;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ExecutablesTest
{
	@Test
	public void testGettingStringPrintlnMethod()
	{
		Type[] stringClass = new Type[] {new ReferenceType(String.class)};
		ArgumentClasses argumentClasses =
			new ArgumentClasses(stringClass, new ArrayList<>());

		Class<?> printStreamClass = PrintStream.class;
		Method[] methods = printStreamClass.getMethods();
		List<Method> printlnMethods = new ArrayList<>();

		for (Method method: methods)
		{
			if (method.getName().equals("println"))
			{
				printlnMethods.add(method);
			}
		}

		Executables<Method> executables = new Executables<>(printlnMethods);
		MatchResult<Method> result = executables.getMatchingExecutable(argumentClasses);
		Method matchedMethod = result.getExecutable();

		assertTrue(result.isSuccessful());
		assertEquals(String.class, matchedMethod.getParameterTypes()[0]);
		assertEquals("(Ljava/lang/String;)", result.getDescriptor());
	}

	@Test
	public void testGettingBooleanPrintlnMethod()
	{
		Type[] bool = new Type[] {PrimitiveType.BOOLEAN};
		ArgumentClasses argumentClasses =
			new ArgumentClasses(bool, new ArrayList<>());

		Class<?> printStreamClass = PrintStream.class;
		Method[] methods = printStreamClass.getMethods();
		List<Method> printlnMethods = new ArrayList<>();

		for (Method method: methods)
		{
			if (method.getName().equals("println"))
			{
				printlnMethods.add(method);
			}
		}

		Executables<Method> executables = new Executables<>(printlnMethods);
		MatchResult<Method> result = executables.getMatchingExecutable(argumentClasses);
		Method matchedMethod = result.getExecutable();

		assertTrue(result.isSuccessful());
		assertEquals(boolean.class, matchedMethod.getParameterTypes()[0]);
		assertEquals("(Z)", result.getDescriptor());
	}

	@Test
	public void testAmbiguousMatchingOfBoxedAndUnboxedType()
	{
		Type[] bool = new Type[] {PrimitiveType.CHAR};
		ArgumentClasses argumentClasses =
			new ArgumentClasses(bool, new ArrayList<>());

		Class<?> testClass = ExecutablesTest.class;
		Method[] methods = testClass.getMethods();
		List<Method> printlnMethods = new ArrayList<>();

		for (Method method: methods)
		{
			if (method.getName().equals("testMethod"))
			{
				printlnMethods.add(method);
			}
		}

		Executables<Method> executables = new Executables<>(printlnMethods);
		MatchResult<Method> result = executables.getMatchingExecutable(argumentClasses);

		assertFalse(result.isSuccessful());
	}

	// The order of these method declarations is important.
	public void testMethod(char character)
	{
	}

	// This one must come second.
	public void testMethod(Character character)
	{
	}
}
