package yirgacheffe.compiler.statement;

import org.junit.Test;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.Delegate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.Streeng;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BranchTest
{
	@Test
	public void testEmptyBranch()
	{
		Expression nothing = new Nothing();
		Branch branch = new Branch(new If(nothing, new DoNothing()));

		Expression expression = branch.getExpression();

		assertTrue(expression instanceof Nothing);
		assertFalse(branch.isEmpty());
		assertEquals(0, branch.getFieldAssignments().length());

		Array<Type> delegatedInterfaces =
			branch.getDelegatedInterfaces(new HashMap<>(), new NullType());

		assertEquals(0, delegatedInterfaces.length());
	}

	@Test
	public void testGettingVariableReads()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		VariableRead read = new VariableRead(coordinate, "myVariable");
		VariableWrite write = new VariableWrite(coordinate, "var", read);
		Statement branch = new Branch(new If(read, write));

		Array<VariableRead> reads = branch.getVariableReads();

		assertTrue(reads.indexOf(read) >= 0);

		Array<VariableWrite> writes = branch.getVariableWrites();

		assertTrue(writes.indexOf(write) >= 0);
	}

	@Test
	public void testIfHasNoDelegatedInterfaces()
	{
		Expression condition = new Nothing();
		Delegate delegate = new Delegate(new Array<>(new Streeng("\"\"")));
		Statement statement = new FunctionCall(delegate);

		If ifStatement = new If(condition, statement);
		Branch branch = new Branch(ifStatement);

		Type string = new ReferenceType(String.class);
		Map<Delegate, Type> delegatedTypes = new HashMap<>();
		delegatedTypes.put(delegate, string);

		Array<Type> delegatedInterfaces =
			branch.getDelegatedInterfaces(delegatedTypes, string);

		assertEquals(0, delegatedInterfaces.length());
	}
}
