package yirgacheffe.compiler.statement;

import org.junit.Test;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.implementation.NullImplementation;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class
VariableDeclarationTest
{
	@Test
	public void testFirstOperandIsNothing()
	{
		VariableDeclaration variableDeclaration =
			new VariableDeclaration("var", PrimitiveType.DOUBLE);

		Expression expression = variableDeclaration.getExpression();

		assertTrue(expression instanceof Nothing);
		assertEquals(0, variableDeclaration.getVariableReads().length());
		assertEquals(0, variableDeclaration.getVariableWrites().length());
		assertTrue(variableDeclaration.isEmpty());
		assertTrue(variableDeclaration.equals("var"));
		assertEquals(variableDeclaration, variableDeclaration);
		assertEquals(0, variableDeclaration.getFieldAssignments().length());

		Implementation delegatedInterfaces =
			variableDeclaration.getDelegatedInterfaces(new HashMap<>(), new NullType());

		assertTrue(delegatedInterfaces instanceof NullImplementation);
	}
}
