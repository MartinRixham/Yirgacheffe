package yirgacheffe.compiler.statement;

import org.junit.Test;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParameterDeclarationTest
{
	@Test
	public void testParameterDeclaration()
	{
		ParameterDeclaration parameterDeclaration =
			new ParameterDeclaration("myParam", PrimitiveType.DOUBLE);

		assertTrue(parameterDeclaration.getFirstOperand() instanceof Nothing);
		assertEquals(new Array<>(), parameterDeclaration.getVariableReads());
		assertEquals(new Array<>(), parameterDeclaration.getVariableWrites());
		assertTrue(parameterDeclaration.getExpression() instanceof Nothing);
		assertTrue(parameterDeclaration.isEmpty());
	}
}
