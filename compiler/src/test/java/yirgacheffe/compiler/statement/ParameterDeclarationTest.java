package yirgacheffe.compiler.statement;

import org.junit.Test;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.implementation.NullImplementation;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ParameterDeclarationTest
{
	@Test
	public void testParameterDeclaration()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		ParameterDeclaration parameterDeclaration =
			new ParameterDeclaration(coordinate, "myParam", PrimitiveType.DOUBLE);

		assertEquals(new Array<>(), parameterDeclaration.getVariableReads());
		assertEquals(new Array<>(), parameterDeclaration.getVariableWrites());
		assertTrue(parameterDeclaration.isEmpty());
		assertFalse(parameterDeclaration.getFieldAssignments().contains(""));
		assertTrue(parameterDeclaration.getExpression() instanceof VariableRead);

		assertEquals(
			new VariableRead(coordinate, "myParam"),
			parameterDeclaration.getExpression());

		Implementation delegatedInterfaces =
			parameterDeclaration.getDelegatedInterfaces(new HashMap<>(), new NullType());

		assertTrue(delegatedInterfaces instanceof NullImplementation);
	}
}
