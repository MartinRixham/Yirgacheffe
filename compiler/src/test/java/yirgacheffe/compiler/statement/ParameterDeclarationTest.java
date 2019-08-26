package yirgacheffe.compiler.statement;

import org.junit.Test;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
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
		assertEquals(0, parameterDeclaration.getFieldAssignments().length());

		Array<Type> delegatedInterfaces =
			parameterDeclaration.getDelegatedInterfaces(new HashMap<>(), new NullType());

		assertEquals(0, delegatedInterfaces.length());
	}
}
