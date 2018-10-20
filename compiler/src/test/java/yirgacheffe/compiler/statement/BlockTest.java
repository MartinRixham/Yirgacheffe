package yirgacheffe.compiler.statement;

import org.objectweb.asm.tree.MethodNode;
import org.junit.Test;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;

public class BlockTest
{
	@Test
	public void testFailToReadVariableDeclaredInBlock()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		VariableDeclaration variableDeclaration =
			new VariableDeclaration("myVariable", PrimitiveType.DOUBLE);
		Block block = new Block(coordinate, new Array<>(variableDeclaration));
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		block.compile(methodVisitor, variables);

		VariableRead variableRead = new VariableRead("myVariable", coordinate);

		variableRead.getType(variables);
		variableRead.compile(methodVisitor, variables);

		assertEquals(1, variables.getErrors().length());
		assertEquals(
			"line 3:5 Unknown local variable 'myVariable'.",
			variables.getErrors().get(0).toString());
	}

	@Test
	public void testUnreachableCode()
	{
		Coordinate coordinate = new Coordinate(4, 0);
		Return returnStatement = new Return(coordinate);
		VariableDeclaration variableDeclaration =
			new VariableDeclaration("myVariable", PrimitiveType.DOUBLE);
		VariableDeclaration anotherDeclaration =
			new VariableDeclaration("anotherVariable", PrimitiveType.DOUBLE);
		Array<Statement> statements =
			new Array<>(returnStatement, variableDeclaration, anotherDeclaration);
		Block block = new Block(coordinate, statements);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		StatementResult result = block.compile(methodVisitor, variables);

		assertEquals(1, result.getErrors().length());
		assertEquals("line 4:0 Unreachable code.",
			result.getErrors().get(0).toString());
	}
}
