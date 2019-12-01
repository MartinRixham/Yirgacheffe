package yirgacheffe.compiler.expression;

import org.junit.Test;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class InvokeThisTest
{
	@Test
	public void testCompilation()
	{
		Coordinate coordinate = new Coordinate(3, 3);
		Type string = new ReferenceType(String.class);
		Array<Expression> arguments = new Array<>(new Streeng(coordinate, "thingy"));
		InvokeThis invokeThis = new InvokeThis(coordinate, string, arguments);
		Variables variables = new LocalVariables(new HashMap<>());

		Result result = invokeThis.compileCondition(variables, null, null);

		assertEquals(0, result.getErrors().length());
		assertFalse(invokeThis.isCondition(variables));
		assertEquals(coordinate, invokeThis.getCoordinate());
	}
}
