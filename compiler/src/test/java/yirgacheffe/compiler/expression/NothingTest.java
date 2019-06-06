package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class NothingTest
{
	@Test
	public void testGettingVariableReads()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());

		Expression nothing = new Nothing();

		nothing.compile(methodVisitor, variables);
		Array<Error> errors =
			nothing.compileCondition(methodVisitor, variables, new Label());

		Array<VariableRead> reads = nothing.getVariableReads();

		assertEquals(0, errors.length());
		assertFalse(nothing.isCondition(variables));
		assertEquals(0, reads.length());
	}
}
