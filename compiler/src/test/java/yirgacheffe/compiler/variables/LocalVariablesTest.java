package yirgacheffe.compiler.variables;

import org.junit.Test;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Num;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.statement.VariableWrite;
import yirgacheffe.compiler.type.PrimitiveType;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class LocalVariablesTest
{
	@Test
	public void testReadDeclaredVariable()
	{
		LocalVariables variables = new LocalVariables(new HashMap<>());
		Coordinate coordinate = new Coordinate(1, 0);
		VariableRead read = new VariableRead(coordinate, "myVariable");

		variables.declare("myVariable", PrimitiveType.DOUBLE);
		variables.read(read);

		assertEquals(0, variables.getErrors().length());
	}

	@Test
	public void testReadUndeclaredVariable()
	{
		LocalVariables variables = new LocalVariables(new HashMap<>());
		Coordinate coordinate = new Coordinate(1, 0);
		VariableRead read = new VariableRead(coordinate, "myVariable");

		variables.read(read);

		assertEquals(1, variables.getErrors().length());

		Error error = variables.getErrors().get(0);

		assertEquals("line 1:0 Unknown local variable 'myVariable'.", error.toString());
	}

	@Test
	public void testWriteDeclaredVariable()
	{
		Coordinate coordinate = new Coordinate(1, 0);
		LocalVariables variables = new LocalVariables(new HashMap<>());
		Expression expression = new Num(coordinate, "123");
		VariableWrite write = new VariableWrite(coordinate, "myVariable", expression);

		variables.declare("myVariable", PrimitiveType.DOUBLE);
		variables.write(write);

		assertEquals(0, variables.getErrors().length());
	}

	@Test
	public void testWriteUndeclaredVariable()
	{
		Coordinate coordinate = new Coordinate(1, 0);
		LocalVariables variables = new LocalVariables(new HashMap<>());
		Expression expression = new Num(coordinate, "123");
		VariableWrite write = new VariableWrite(coordinate, "myVariable", expression);

		variables.write(write);

		assertEquals(1, variables.getErrors().length());

		Error error = variables.getErrors().get(0);

		assertEquals(
			"line 1:0 Assignment to uninitialised variable 'myVariable'.",
			error.toString());
	}
}
