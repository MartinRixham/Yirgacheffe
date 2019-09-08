package yirgacheffe.compiler.statement;

import org.junit.Test;
import yirgacheffe.compiler.expression.Nothing;
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
		ParameterDeclaration parameterDeclaration =
			new ParameterDeclaration("myParam", PrimitiveType.DOUBLE);

		assertEquals(new Array<>(), parameterDeclaration.getVariableReads());
		assertEquals(new Array<>(), parameterDeclaration.getVariableWrites());
		assertTrue(parameterDeclaration.getExpression() instanceof Nothing);
		assertTrue(parameterDeclaration.isEmpty());

		assertFalse(parameterDeclaration.getFieldAssignments().contains(""));

		Implementation delegatedInterfaces =
			parameterDeclaration.getDelegatedInterfaces(new HashMap<>(), new NullType());

		assertTrue(delegatedInterfaces instanceof NullImplementation);
	}
}
