package yirgacheffe.compiler.statement;

import org.junit.Test;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Literal;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.type.PrimitiveType;

import static org.junit.Assert.assertEquals;

public class StatementResultTest
{
	@Test
	public void testReadDeclaredVariable()
	{
		StatementResult result = new StatementResult();
		Coordinate coordinate = new Coordinate(1, 0);
		VariableRead read = new VariableRead("myVariable", coordinate);

		result.declare("myVariable", PrimitiveType.DOUBLE);
		result.read(read);

		assertEquals(0, result.getErrors().length());
	}

	@Test
	public void testReadUndeclaredVariable()
	{
		StatementResult result = new StatementResult();
		Coordinate coordinate = new Coordinate(1, 0);
		VariableRead read = new VariableRead("myVariable", coordinate);

		result.read(read);

		assertEquals(1, result.getErrors().length());

		Error error = result.getErrors().get(0);

		assertEquals("line 1:0 Unknown local variable 'myVariable'.", error.toString());
	}

	@Test
	public void testWriteDeclaredVariable()
	{
		StatementResult result = new StatementResult();
		Expression expression = new Literal(PrimitiveType.DOUBLE, "123");
		Coordinate coordinate = new Coordinate(1, 0);
		VariableWrite write = new VariableWrite("myVariable", expression, coordinate);

		result.declare("myVariable", PrimitiveType.DOUBLE);
		result.write(write);

		assertEquals(0, result.getErrors().length());
	}

	@Test
	public void testWriteUndeclaredVariable()
	{
		StatementResult result = new StatementResult();
		Expression expression = new Literal(PrimitiveType.DOUBLE, "123");
		Coordinate coordinate = new Coordinate(1, 0);
		VariableWrite write = new VariableWrite("myVariable", expression, coordinate);

		result.write(write);

		assertEquals(1, result.getErrors().length());

		Error error = result.getErrors().get(0);

		assertEquals(
			"line 1:0 Assignment to uninitialised variable 'myVariable'.",
			error.toString());
	}
}
