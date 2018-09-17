package yirgacheffe.compiler.statement;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.junit.Test;
import yirgacheffe.compiler.error.Coordinate;

import static org.junit.Assert.assertEquals;

public class ReturnTest
{
	@Test
	public void testVoidReturn()
	{
		Coordinate coordinate = new Coordinate(5, 3);
		Return returnStatement = new Return(coordinate);
		MethodNode methodVisitor = new MethodNode();
		StatementResult result = new StatementResult();

		returnStatement.compile(methodVisitor, result);

		assertEquals(0, result.getErrors().length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(1, instructions.size());
	}
}
