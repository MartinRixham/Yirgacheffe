package yirgacheffe.compiler.statement;

import org.junit.Test;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TailCallTest
{
	@Test
	public void testTailCall()
	{
		Statement invocation = new DoNothing();
		Signature caller = new Signature("caller", new Array<>());
		Variables variables = new Variables();

		TailCall tailCall = new TailCall(invocation, caller, variables);

		assertFalse(tailCall.returns());
		assertTrue(tailCall.getFirstOperand() instanceof Nothing);
		assertTrue(tailCall.getExpression() instanceof Nothing);
		assertEquals(new Array<>(), tailCall.getVariableReads());
		assertEquals(new Array<>(), tailCall.getVariableWrites());
	}
}
