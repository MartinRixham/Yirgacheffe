package yirgacheffe.compiler.expression;

import org.junit.Test;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DelegateTest
{
	@Test
	public void testDelegate()
	{
		Coordinate coordinate = new Coordinate(2, 9);
		Variables variables = new LocalVariables(new HashMap<>());
		Array<Expression> arguments = new Array<>(new Streeng("\"\""));

		Delegate delegate = new Delegate(coordinate, "MyClass", arguments);

		Result result = delegate.compileCondition(variables, null, null);

		assertEquals(PrimitiveType.VOID, delegate.getType(variables));
		assertEquals(0, result.getErrors().length());
		assertFalse(delegate.isCondition(variables));
		assertEquals(0, delegate.getVariableReads().length());
	}
}
