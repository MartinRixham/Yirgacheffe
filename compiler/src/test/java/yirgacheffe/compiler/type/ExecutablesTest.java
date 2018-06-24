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
		Type printStream = new ReferenceType(PrintStream.class);
		ArgumentClasses argumentClasses = new ArgumentClasses(stringClass, printStream);

		Method[] methods = printStream.reflectionClass().getMethods();
		List<Function> printlnMethods = new ArrayList<>();

		for (Method method: methods)
		{
			if (method.getName().equals("println"))
			{
				printlnMethods.add(new Function(printStream, method));
			}
		}

		Executables executables = new Executables(printlnMethods);
		MatchResult result = executables.getMatchingExecutable(argumentClasses);
		List<MismatchedTypes> mismatchedParameters = result.getMismatchedParameters();

		assertTrue(result.isSuccessful());
		assertEquals(0, mismatchedParameters.size());
		assertEquals("(Ljava/lang/String;)", result.getDescriptor());
	}

	@Test
	public void testGettingBooleanPrintlnMethod()
	{
		Type[] bool = new Type[] {PrimitiveType.BOOLEAN};
		Type printStream = new ReferenceType(PrintStream.class);
		ArgumentClasses argumentClasses = new ArgumentClasses(bool, printStream);

		Method[] methods = printStream.reflectionClass().getMethods();
		List<Function> printlnMethods = new ArrayList<>();

		for (Method method: methods)
		{
			if (method.getName().equals("println"))
			{
				printlnMethods.add(new Function(printStream, method));
			}
		}

		Executables executables = new Executables(printlnMethods);
		MatchResult result = executables.getMatchingExecutable(argumentClasses);
		List<MismatchedTypes> mismatchedParameters = result.getMismatchedParameters();

		assertTrue(result.isSuccessful());
		assertEquals(0, mismatchedParameters.size());
		assertEquals("(Z)", result.getDescriptor());
	}

	@Test
	public void testAmbiguousMatchingOfBoxedAndUnboxedType()
	{
		Type[] bool = new Type[] {PrimitiveType.CHAR};
		Type testClass = new ReferenceType(ExecutablesTest.class);
		ArgumentClasses argumentClasses = new ArgumentClasses(bool, testClass);

		Method[] methods = testClass.reflectionClass().getMethods();
		List<Function> printlnMethods = new ArrayList<>();

		for (Method method: methods)
		{
			if (method.getName().equals("testMethod"))
			{
				printlnMethods.add(new Function(testClass, method));
			}
		}

		Executables executables = new Executables(printlnMethods);
		MatchResult result = executables.getMatchingExecutable(argumentClasses);

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
