package yirgacheffe.compiler.variables;

import org.junit.Test;
import yirgacheffe.compiler.expression.Nothing;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class OptimisedVariablesTest
{
	@Test
	public void testReadDeclaredVariable()
	{
		Variables variables = new OptimisedVariables(new LocalVariables(new HashMap<>()));

		assertEquals(0, variables.getVariables().keySet().size());
		assertEquals(0, variables.getVariable("").getIndex());
		assertEquals(0, variables.getErrors().length());
		assertFalse(variables.canOptimise(null));
		assertTrue(variables.getOptimisedExpression(null) instanceof Nothing);
		assertFalse(variables.hasConstant(""));
		assertNull(variables.getConstant(""));
		assertEquals(0, variables.nextVariableIndex());
		assertEquals(0, variables.getStack().length());
	}
}
