package yirgacheffe.compiler.expression;

import org.junit.Test;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class EnumerationTest
{
	@Test
	public void testGettingConstant()
	{
		Variables variables = new LocalVariables(new HashMap<>());
		Type type = new ReferenceType(this.getClass());
		Expression expression = new Streeng("thingy");

		Expression enumeration = new Enumeration(type, expression);

		Result result = enumeration.compileCondition(variables, null, null);

		Array<VariableRead> reads = enumeration.getVariableReads();

		assertEquals(0, result.getErrors().length());
		assertFalse(enumeration.isCondition(variables));
		assertEquals(0, reads.length());
	}
}
