package yirgacheffe.compiler.statement;

import org.junit.Test;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OpenBlockTest
{
	@Test
	public void testOpenBlock()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		OpenBlock openBlock = new OpenBlock();
		LocalVariables variables = new LocalVariables(new HashMap<>());

		Result result = openBlock.compile(variables, caller);

		assertEquals(0, variables.getStack().length());
		assertNotNull(result.getErrors());
		assertFalse(openBlock.returns());
		assertTrue(openBlock.isEmpty());
	}

	@Test
	public void testFirstOperandIsNothing()
	{
		OpenBlock openBlock = new OpenBlock();

		Expression expression = openBlock.getExpression();

		assertTrue(expression instanceof Nothing);
		assertEquals(0, openBlock.getVariableReads().length());
		assertEquals(0, openBlock.getVariableWrites().length());
	}
}
