package yirgacheffe.compiler.statement;

import org.objectweb.asm.tree.MethodNode;
import org.junit.Test;
import yirgacheffe.compiler.type.Variables;

import static org.junit.Assert.assertFalse;

public class OpenBlockTest
{
	@Test
	public void testOpenBlock()
	{
		OpenBlock openBlock = new OpenBlock();
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		StatementResult result = openBlock.compile(methodVisitor, variables);

		assertFalse(openBlock.returns());
	}
}