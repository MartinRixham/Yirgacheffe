package yirgacheffe.compiler.statement;

import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.assignment.BlockFieldAssignment;
import yirgacheffe.compiler.assignment.BranchedFieldAssignment;
import yirgacheffe.compiler.assignment.FieldAssignment;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Delegate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.FunctionSignature;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.implementation.BranchImplementation;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.implementation.NullImplementation;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variable;
import yirgacheffe.compiler.variables.PrerunVariables;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Block implements Statement
{
	private Coordinate coordinate;

	private Array<Statement> statements;

	public Block(Coordinate coordinate, Array<Statement> statements)
	{
		this.coordinate = coordinate;
		this.statements = statements;
	}

	public boolean returns()
	{
		for (Statement statement: this.statements)
		{
			if (statement.returns())
			{
				return true;
			}
		}

		return false;
	}

	public Result compile(Variables variables, Signature caller)
	{
		Array<Statement> statements = this.statements;
		Map<String, Variable> declaredVariables = new HashMap<>(variables.getVariables());
		boolean unreachableCode = false;

		if (!(variables instanceof PrerunVariables))
		{
			this.optimiseIntegers(variables);
			this.optimiseVariables(variables);
			this.optimiseTailCall(caller, variables);
		}

		Result result = new Result();

		for (int i = 0; i < statements.length(); i++)
		{
			Signature call =
				i == statements.length() - 1 ?
					caller :
					new FunctionSignature(new NullType(), "", new Array<>());

			result = result.concat(statements.get(i).compile(variables, call));

			if (statements.get(i).returns() && i < statements.length() - 1)
			{
				unreachableCode = true;
			}
		}

		if (unreachableCode)
		{
			String message = "Unreachable code.";

			result = result.add(new Error(this.coordinate, message));
		}

		variables.setVariables(declaredVariables);

		return result;
	}

	private void optimiseVariables(Variables variables)
	{
		List<VariableRead> variableReads = new ArrayList<>();
		Set<VariableWrite> variableWrites = new HashSet<>();

		for (int i = this.statements.length() - 1; i >= 0; i--)
		{
			Statement statement = this.statements.get(i);
			boolean optimised = false;

			for (VariableRead variableRead: variableReads)
			{
				if (this.canOptimise(statement, variableRead, variableReads, i))
				{
					if (!statement.isEmpty())
					{
						this.statements.splice(i, 1, new OptimisedStatement(statement));
					}

					variables.optimise(variableRead, statement.getExpression());
					optimised = true;
				}
			}

			if (!optimised)
			{
				variableWrites.addAll(this.getList(statement.getVariableWrites()));
			}

			if (statement instanceof VariableDeclaration &&
				!variableWrites.contains(statement))
			{
				this.statements.splice(i, 1, new OptimisedStatement(statement));
			}

			variableReads.removeAll(Collections.singleton(statement));
			variableReads.addAll(this.getList(statement.getVariableReads()));
		}
	}

	private <T> List<T> getList(Array<T> array)
	{
		List<T> list = new ArrayList<>(array.length());

		for (int i = 0; i < array.length(); i++)
		{
			list.add(array.get(i));
		}

		return list;
	}

	private boolean canOptimise(
		Statement statement,
		VariableRead variableRead,
		List<VariableRead> variableReads,
		int index)
	{
		if (!statement.equals(variableRead) ||
			Collections.frequency(variableReads, statement) != 1)
		{
			return false;
		}

		while (!this.statements.get(++index).getVariableReads().contains(variableRead))
		{
			if (statement.getVariableReads().contains(this.statements.get(index)))
			{
				return false;
			}
		}

		return true;
	}

	private void optimiseTailCall(Signature caller, Variables variables)
	{
		if (this.statements.length() == 0)
		{
			return;
		}

		Statement lastStatement = this.statements.pop();

		TailCall tailCall =
			new TailCall(lastStatement, caller, variables);

		this.statements.push(tailCall);
	}

	private void optimiseIntegers(Variables variables)
	{
		PrerunVariables prerunVariables = new PrerunVariables(variables);

		for (int i = 0; i < this.statements.length(); i++)
		{
			Signature call = new FunctionSignature(new NullType(), "", new Array<>());

			this.statements.get(i).compile(prerunVariables, call);
		}
	}

	public Array<VariableRead> getVariableReads()
	{
		Array<VariableRead> variableReads = new Array<>();

		for (Statement statement: this.statements)
		{
			variableReads = variableReads.concat(statement.getVariableReads());
		}

		return variableReads;
	}

	public Array<VariableWrite> getVariableWrites()
	{
		Array<VariableWrite> variableWrites = new Array<>();

		for (Statement statement: this.statements)
		{
			for (VariableWrite variableWrite: statement.getVariableWrites())
			{
				variableWrites.push(variableWrite);
			}
		}

		return variableWrites;
	}

	public FieldAssignment getFieldAssignments()
	{
		FieldAssignment assignment = new BlockFieldAssignment(new Array<>());

		for (Statement statement: this.statements)
		{
			assignment = assignment.combineWith(statement.getFieldAssignments());
		}

		if (this.returns())
		{
			return new BranchedFieldAssignment(new Array<>(), assignment);
		}
		else
		{
			return assignment;
		}
	}

	public Implementation getDelegatedInterfaces(
		Map<Delegate, Type> delegateTypes,
		Type thisType)
	{
		Implementation delegatedInterfaces = new NullImplementation();

		for (Statement statement: this.statements)
		{
			Implementation interfaces =
				statement.getDelegatedInterfaces(delegateTypes, thisType);

			delegatedInterfaces = delegatedInterfaces.intersect(interfaces);
		}

		if (this.returns())
		{
			return new BranchImplementation(delegatedInterfaces);
		}

		return delegatedInterfaces;
	}

	public Expression getExpression()
	{
		if (this.statements.length() == 0)
		{
			return new Nothing();
		}
		else
		{
			return this.statements.get(this.statements.length() - 1).getExpression();
		}
	}

	public boolean isEmpty()
	{
		for (Statement statement: this.statements)
		{
			if (!statement.isEmpty())
			{
				return false;
			}
		}

		return true;
	}
}
