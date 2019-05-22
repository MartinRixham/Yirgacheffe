package yirgacheffe.compiler.statement;

import org.objectweb.asm.tree.MethodNode;
import org.junit.Test;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.Variables;
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
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());

		Array<Error> errors = openBlock.compile(methodVisitor, variables, caller);

		assertNotNull(errors);
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
