package yirgacheffe.compiler.statement;

import org.objectweb.asm.tree.MethodNode;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class OpenBlockTest
{
	@Test
	public void testOpenBlock()
	{
		OpenBlock openBlock = new OpenBlock();
		MethodNode methodVisitor = new MethodNode();
		StatementResult result = new StatementResult();

		boolean returns = openBlock.compile(methodVisitor, result);

		assertFalse(returns);
	}
}
