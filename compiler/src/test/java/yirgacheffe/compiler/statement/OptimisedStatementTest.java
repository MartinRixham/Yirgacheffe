package yirgacheffe.compiler.statement;

import org.junit.Test;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.Delegate;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.Streeng;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.implementation.InterfaceImplementation;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OptimisedStatementTest
{
	@Test
	public void testOptimisedStatement()
	{
		Statement nothing = new DoNothing();
		Statement optimisedStatement = new OptimisedStatement(nothing);

		assertEquals(new Array<>(), optimisedStatement.getVariableReads());
		assertEquals(new Array<>(), optimisedStatement.getVariableWrites());
		assertTrue(optimisedStatement.getExpression() instanceof  Nothing);
		assertTrue(optimisedStatement.isEmpty());
		assertEquals(0, optimisedStatement.getFieldAssignments().length());
	}

	@Test
	public void testDelegatedInterfaces()
	{
		Coordinate coordinate = new Coordinate(2, 6);

		Delegate delegate =
			new Delegate(coordinate, "MyClass", new Array<>(new Streeng("\"\"")));

		Statement statement = new FunctionCall(delegate);
		Statement optimisedStatement = new OptimisedStatement(statement);

		Type string = new ReferenceType(String.class);
		Map<Delegate, Type> delegatedTypes = new HashMap<>();
		delegatedTypes.put(delegate, string);

		Implementation delegatedInterfaces =
			optimisedStatement.getDelegatedInterfaces(delegatedTypes, string);

		assertTrue(delegatedInterfaces instanceof InterfaceImplementation);
	}
}
