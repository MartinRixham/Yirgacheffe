package yirgacheffe.compiler.statement;

import org.junit.Test;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.Streeng;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.implementation.NullImplementation;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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

	@Test
	public void testEqualVariables()
	{
		Coordinate coordinate = new Coordinate(1, 0);
		Type string = new ReferenceType(String.class);

		ParameterDeclaration firstVariable =
			new ParameterDeclaration(coordinate, "var", string);

		ParameterDeclaration secondVariable =
			new ParameterDeclaration(coordinate, "var", string);

		assertEquals(firstVariable, secondVariable);
		assertEquals(firstVariable.hashCode(), secondVariable.hashCode());
	}

	@Test
	public void testNotEqualToString()
	{
		Coordinate coordinate = new Coordinate(1, 0);
		Type string = new ReferenceType(String.class);

		ParameterDeclaration firstVariable =
			new ParameterDeclaration(coordinate, "var", string);

		Object secondVariable = new Object();

		assertNotEquals(firstVariable, secondVariable);
		assertNotEquals(firstVariable.hashCode(), secondVariable.hashCode());
	}

	@Test
	public void testEqualToVariableRead()
	{
		Coordinate coordinate = new Coordinate(1, 0);
		Streeng string = new Streeng(coordinate, "\"my string\"");

		ParameterDeclaration variableWrite =
			new ParameterDeclaration(coordinate, "myVar", string.getType());

		VariableRead variableRead = new VariableRead(coordinate, "myVar");

		assertEquals(variableWrite, variableRead);
		assertEquals(variableWrite.hashCode(), variableRead.hashCode());
	}
}
