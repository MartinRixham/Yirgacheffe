package yirgacheffe.compiler.statement;

import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.assignment.BlockFieldAssignment;
import yirgacheffe.compiler.assignment.FieldAssignment;
import yirgacheffe.compiler.expression.Delegate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.implementation.NullImplementation;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.Map;

public class OpenBlock implements Statement
{
	public boolean returns()
	{
		return false;
	}

	public Result compile(Variables variables, Signature caller)
	{
		return new Result();
	}

	public Array<VariableRead> getVariableReads()
	{
		return new Array<>();
	}

	public Array<VariableWrite> getVariableWrites()
	{
		return new Array<>();
	}

	public FieldAssignment getFieldAssignments()
	{
		return new BlockFieldAssignment(new Array<>());
	}

	public Implementation getDelegatedInterfaces(
		Map<Delegate, Type> delegateTypes,
		Type thisType)
	{
		return new NullImplementation();
	}

	public Expression getExpression()
	{
		return new Nothing();
	}

	public boolean isEmpty()
	{
		return true;
	}
}
