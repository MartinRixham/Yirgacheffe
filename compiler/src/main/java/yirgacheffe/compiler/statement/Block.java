package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.type.Variable;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

	public StatementResult compile(MethodVisitor methodVisitor, Variables variables)
	{
		Array<Statement> statements = this.statements;
		Map<String, Variable> declaredVariables = variables.getDeclaredVariables();
		boolean unreachableCode = false;
		Array<Error> errors = new Array<>();
		StatementResult blockResult = new StatementResult();

		this.optimise(statements, variables);

		for (int i = 0; i < statements.length(); i++)
		{
			StatementResult result =
				statements.get(i).compile(methodVisitor, variables);

			blockResult = blockResult.add(result);

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

		return new StatementResult(errors).add(blockResult);
	}

	public void optimise(Array<Statement> statements, Variables variables)
	{
		List<VariableRead> variableReads = new ArrayList<>();
		VariableRead nextVariable = null;

		for (int i = this.statements.length() - 1; i >= 1; i--)
		{
			Statement statement = statements.get(i);
			Expression expression = statement.getFirstOperand();
			VariableRead variableRead = null;

			variableReads.addAll(
				Arrays.asList(statement.getVariableReads().toArray()));

			if (expression instanceof VariableRead)
			{
				variableRead = (VariableRead) expression;
				nextVariable = variableRead;
			}
			else if (statement instanceof VariableWrite)
			{
				if (statement.equals(nextVariable) &&
					Collections.frequency(variableReads, statement) == 1)
				{
					statements.splice(i, 1);
					variables.optimise(nextVariable, statement.getFirstOperand());
				}

				variableReads.removeAll(Collections.singleton(statement));
			}

			if (variableRead != nextVariable)
			{
				nextVariable = null;
			}
		}
	}

	public Expression getFirstOperand()
	{
		if (this.statements.length() == 0)
		{
			return new Nothing();
		}
		else
		{
			return this.statements.get(0).getFirstOperand();
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
}
