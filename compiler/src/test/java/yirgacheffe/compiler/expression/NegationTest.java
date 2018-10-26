package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;

public class NegationTest
{
	@Test
	public void testNegationOfWrongType()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();
		Coordinate coordinate = new Coordinate(3, 6);
		This operand = new This(new ReferenceType(String.class));
		Negation negation = new Negation(coordinate, operand);

		Type type = negation.getType(variables);

		Array<Error> errors = negation.compile(methodVisitor, variables);

		assertEquals(PrimitiveType.DOUBLE, type);
		assertEquals(1, errors.length());

		assertEquals(errors.get(0).toString(),
			"line 3:6 Cannot negate java.lang.String.");
	}
}
