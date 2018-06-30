package yirgacheffe.compiler.function;

import org.junit.Test;
import yirgacheffe.compiler.type.ArgumentClasses;
import yirgacheffe.compiler.type.MismatchedTypes;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class FunctionsTest
{
	@Test
	public void testGettingStringPrintlnMethod()
	{
		Type[] stringClass = new Type[] {new ReferenceType(String.class)};
		Type printStream = new ReferenceType(PrintStream.class);
		ArgumentClasses argumentClasses = new ArgumentClasses(stringClass);

		Method[] methods = printStream.reflectionClass().getMethods();
		List<Callable> printlnMethods = new ArrayList<>();

		for (Method method: methods)
		{
			if (method.getName().equals("println"))
			{
				printlnMethods.add(new Function(printStream, method));
			}
		}

		Functions functions = new Functions(printlnMethods);
		MatchResult result = functions.getMatchingExecutable(argumentClasses);
		List<MismatchedTypes> mismatchedParameters = result.getMismatchedParameters();

		assertTrue(result.isSuccessful());
		assertEquals(0, mismatchedParameters.size());
		assertEquals("(Ljava/lang/String;)V", result.getFunction().getDescriptor());
	}

	@Test
	public void testGettingBooleanPrintlnMethod()
	{
		Type[] bool = new Type[] {PrimitiveType.BOOLEAN};
		Type printStream = new ReferenceType(PrintStream.class);
		ArgumentClasses argumentClasses = new ArgumentClasses(bool);

		Method[] methods = printStream.reflectionClass().getMethods();
		List<Callable> printlnMethods = new ArrayList<>();

		for (Method method: methods)
		{
			if (method.getName().equals("println"))
			{
				printlnMethods.add(new Function(printStream, method));
			}
		}

		Functions functions = new Functions(printlnMethods);
		MatchResult result = functions.getMatchingExecutable(argumentClasses);
		List<MismatchedTypes> mismatchedParameters = result.getMismatchedParameters();

		assertTrue(result.isSuccessful());
		assertEquals(0, mismatchedParameters.size());
		assertEquals("(Z)V", result.getFunction().getDescriptor());
	}

	@Test
	public void testAmbiguousMatchingOfBoxedAndUnboxedType()
	{
		Type[] bool = new Type[] {PrimitiveType.CHAR};
		Type testClass = new ReferenceType(FunctionsTest.class);
		ArgumentClasses argumentClasses = new ArgumentClasses(bool);

		Method[] methods = testClass.reflectionClass().getMethods();
		List<Callable> printlnMethods = new ArrayList<>();

		for (Method method: methods)
		{
			if (method.getName().equals("testMethod"))
			{
				printlnMethods.add(new Function(testClass, method));
			}
		}

		Functions functions = new Functions(printlnMethods);
		MatchResult result = functions.getMatchingExecutable(argumentClasses);

		assertTrue(result.isSuccessful());
	}

	public void testMethod(char character)
	{
	}

	public void testMethod(Character character)
	{
	}
}
