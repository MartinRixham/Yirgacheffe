package yirgacheffe.compiler.type;

import org.junit.Test;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ExecutablesTest
{
	@Test
	public void testGettingCorrectPrintlnMethod()
	{
		Type[] stringClass = new Type[] {new ReferenceType(String.class)};
		ArgumentClasses argumentClasses =
			new ArgumentClasses(stringClass, new ArrayList<>());

		Class<?> printStreamClass = PrintStream.class;
		Method[] methods = printStreamClass.getMethods();
		StringBuilder stringBuilder = new StringBuilder();
		List<Method> printlnMethods = new ArrayList<>();

		for (Method method: methods)
		{
			if (method.getName().equals("println"))
			{
				printlnMethods.add(method);
			}
		}

		Executables<Method> executables = new Executables<>(printlnMethods);
		Method matchedMethod = executables.getExecutable(argumentClasses, stringBuilder);

		assertEquals(Object.class, matchedMethod.getParameterTypes()[0]);
		assertEquals("(Ljava/lang/Object;)", stringBuilder.toString());
	}
}
