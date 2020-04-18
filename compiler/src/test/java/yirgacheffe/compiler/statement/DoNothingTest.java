package yirgacheffe.compiler.statement;

import org.junit.Test;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.function.FunctionSignature;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.implementation.NullImplementation;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DoNothingTest
{
	@Test
	public void testDoNothing()
	{
		Signature caller = new FunctionSignature(new NullType(), "method", new Array<>());
		DoNothing doNothing = new DoNothing();
		LocalVariables variables = new LocalVariables(1, new HashMap<>());

		Result result = doNothing.compile(variables, caller);

		assertNotNull(result.getErrors());
		assertFalse(doNothing.returns());
		assertTrue(doNothing.isEmpty());
		assertFalse(doNothing.getFieldAssignments().contains(""));

		Implementation delegatedInterfaces =
			doNothing.getDelegatedInterfaces(new HashMap<>(), new NullType());

		assertTrue(delegatedInterfaces instanceof NullImplementation);
	}

	@Test
	public void testFirstOperandIsNothing()
	{
		DoNothing doNothing = new DoNothing();

		Expression expression = doNothing.getExpression();

		assertTrue(expression instanceof Nothing);
		assertEquals(0, doNothing.getVariableReads().length());
		assertEquals(0, doNothing.getVariableWrites().length());
	}
}
