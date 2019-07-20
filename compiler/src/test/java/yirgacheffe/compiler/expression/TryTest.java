package yirgacheffe.compiler.expression;

import org.junit.Test;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.variables.Variables;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TryTest
{
	@Test
	public void testCompilingTry()
	{
		Variables variables = new LocalVariables(new HashMap<>());
		Try tryExpression = new Try(new Num("1"));

		Result result = tryExpression.compileCondition(variables, null, null);

		assertEquals(1, variables.getStack().length());
		assertFalse(tryExpression.isCondition(variables));
		assertEquals(0, result.getErrors().length());
		assertEquals(5, result.getInstructions().length());
		assertEquals(PrimitiveType.INT, tryExpression.getType(null));
		assertEquals(0, tryExpression.getVariableReads().length());
	}
}
