package yirgacheffe.compiler.implementation;

import org.junit.Test;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

import java.lang.reflect.Method;

import static org.junit.Assert.assertTrue;

public class TotalImplementationTest
{
	@Test
	public void testIntersectEqualInterfaces()
	{
		Type interfaceType = new ReferenceType(String.class.getInterfaces()[1]);

		Implementation first = new TotalImplementation();
		Implementation second = new InterfaceImplementation(new Array<>(interfaceType));

		Implementation intersection = first.intersect(second);

		for (Method method : interfaceType.reflectionClass().getMethods())
		{
			Function function = new Function(interfaceType, method);

			assertTrue(intersection.implementsMethod(function, interfaceType));
			assertTrue(first.implementsMethod(function, interfaceType));
		}
	}
}
