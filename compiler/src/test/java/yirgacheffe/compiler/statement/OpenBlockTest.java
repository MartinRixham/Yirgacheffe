package yirgacheffe.compiler.statement;

import org.objectweb.asm.tree.MethodNode;
import org.junit.Test;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OpenBlockTest
{
	@Test
	public void testOpenBlock()
	{
		OpenBlock openBlock = new OpenBlock();
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Array<Error> errors = openBlock.compile(methodVisitor, variables);

		assertNotNull(errors);
		assertFalse(openBlock.returns());
	}

	@Test
	public void testFirstOperandIsNothing()
	{
		OpenBlock openBlock = new OpenBlock();

		Expression operand = openBlock.getFirstOperand();

		assertTrue(operand instanceof Nothing);
		assertEquals(0, openBlock.getVariableReads().length());
		assertEquals(0, openBlock.getVariableWrites().length());
	}
}
