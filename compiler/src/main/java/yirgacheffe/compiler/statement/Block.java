package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.Variable;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

	public Array<Error> compile(
		MethodVisitor methodVisitor,
		Variables variables,
		Signature caller)
	{
		Array<Statement> statements = this.statements;
		Map<String, Variable> declaredVariables = variables.getDeclaredVariables();
		boolean unreachableCode = false;
		Array<Error> errors = new Array<>();

		this.optimiseVariables(variables);
		this.optimiseTailCall(caller, variables);

		for (int i = 0; i < statements.length(); i++)
		{
			Signature call =
				i == statements.length() - 1 ? caller : new Signature("", new Array<>());

			errors.push(statements.get(i).compile(methodVisitor, variables, call));

			if (statements.get(i).returns() && i < statements.length() - 1)
			{
				unreachableCode = true;
			}
		}

		if (unreachableCode)
		{
			String message = "Unreachable code.";

			errors.push(new Error(this.coordinate, message));
		}

		variables.setDeclaredVariables(declaredVariables);

		return errors;
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
				if (statement.equals(variableRead) &&
					Collections.frequency(variableReads, statement) == 1)
				{
					this.statements.splice(i, 1);
					variables.optimise(variableRead, (VariableWrite) statement);
					optimised = true;
				}
			}

			if (!optimised)
			{
				variableWrites.addAll(
						Arrays.asList(statement.getVariableWrites().toArray()));
			}

			variableReads.removeAll(Collections.singleton(statement));

			if (statement instanceof VariableDeclaration &&
				!variableWrites.contains(statement))
			{
					this.statements.splice(i, 1);
			}

			variableReads.addAll(
				Arrays.asList(statement.getVariableReads().toArray()));
		}
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
			if (statement instanceof VariableWrite)
			{
				variableWrites.push((VariableWrite) statement);
			}
		}

		return variableWrites;
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
