package yirgacheffe.compiler.expression;

import org.junit.Test;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NegationTest
{
	@Test
	public void testNegationOfWrongType()
	{
		Variables variables = new LocalVariables(new HashMap<>());
		Coordinate coordinate = new Coordinate(3, 6);
		This operand = new This(new ReferenceType(String.class));
		Negation negation = new Negation(coordinate, operand);

		Type type = negation.getType(variables);
		Result result = negation.compile(variables);

		assertFalse(negation.isCondition(variables));
		assertEquals(PrimitiveType.DOUBLE, type);
		assertEquals(1, result.getErrors().length());

		assertEquals(result.getErrors().get(0).toString(),
			"line 3:6 Cannot negate java.lang.String.");
	}

	@Test
	public void testNegation()
	{
		Variables variables = new LocalVariables(new HashMap<>());
		Coordinate coordinate = new Coordinate(3, 6);
		Expression operand = new Num("1");
		Negation negation = new Negation(coordinate, operand);

		Type type = negation.getType(variables);
		Result result = negation.compileCondition(variables, null, null);

		assertFalse(negation.isCondition(variables));
		assertEquals(PrimitiveType.DOUBLE, type);
		assertEquals(0, result.getErrors().length());
	}

	@Test
	public void testGettingVariableReads()
	{
		Coordinate coordinate = new Coordinate(3, 6);
		VariableRead read = new VariableRead(coordinate, "myVariable");

		Expression negation = new Negation(coordinate, read);

		Array<VariableRead> reads = negation.getVariableReads();

		assertTrue(reads.indexOf(read) >= 0);
	}
}
