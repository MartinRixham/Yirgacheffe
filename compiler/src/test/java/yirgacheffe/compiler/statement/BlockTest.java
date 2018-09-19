package yirgacheffe.compiler.statement;

import org.objectweb.asm.tree.MethodNode;
import org.junit.Test;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;

public class BlockTest
{
	@Test
	public void testFailToReadVariableDeclaredInBlock()
	{
		VariableDeclaration variableDeclaration =
			new VariableDeclaration("myVariable", PrimitiveType.DOUBLE);
		Block block = new Block(new Array<>(variableDeclaration));
		MethodNode methodVisitor = new MethodNode();
		StatementResult result = new StatementResult();

		block.compile(methodVisitor, result);

		Coordinate coordinate = new Coordinate(3, 5);
		VariableRead variableRead = new VariableRead("myVariable", coordinate);

		variableRead.check(result);
		variableRead.compile(methodVisitor);

		assertEquals(1, result.getErrors().length());
		assertEquals(
			"line 3:5 Unknown local variable 'myVariable'.",
			result.getErrors().get(0).toString());
	}
}
