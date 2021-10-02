package yirgacheffe.compiler.statement;

import org.junit.Test;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.function.FunctionSignature;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.implementation.NullImplementation;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class StoreConstantTest
{
	@Test
	public void testStoreConstant()
	{
		Statement storeConstant =
			new StoreConstant(new NullType(), new Nothing(), new Nothing());

		Signature caller = new FunctionSignature(new NullType(), "method", new Array<>());
		LocalVariables variables =
			new LocalVariables(1, new HashMap<>(), new HashMap<>());

		Result result = storeConstant.compile(variables, caller);

		assertNotNull(result.getErrors());
		assertFalse(storeConstant.returns());
		assertFalse(storeConstant.isEmpty());
		assertFalse(storeConstant.getFieldAssignments().contains(""));

		Implementation delegatedInterfaces =
			storeConstant.getDelegatedInterfaces(new HashMap<>(), new NullType());

		assertTrue(delegatedInterfaces instanceof NullImplementation);
	}

	@Test
	public void testFirstOperandIsNothing()
	{
		Statement storeConstant =
			new StoreConstant(new NullType(), new Nothing(), new Nothing());

		Expression expression = storeConstant.getExpression();

		assertTrue(expression instanceof Nothing);
		assertEquals(0, storeConstant.getVariableReads().length());
		assertEquals(0, storeConstant.getVariableWrites().length());
	}
}
