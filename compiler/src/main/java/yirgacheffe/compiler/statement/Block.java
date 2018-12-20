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

	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
	{
		Array<Statement> statements = this.statements;
		Map<String, Variable> declaredVariables = variables.getDeclaredVariables();
		boolean unreachableCode = false;
		Array<Error> errors = new Array<>();

		this.optimise(variables);

		for (int i = 0; i < statements.length(); i++)
		{
			errors.push(statements.get(i).compile(methodVisitor, variables));

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

	private void optimise(Variables variables)
	{
		List<VariableRead> variableReads = new ArrayList<>();
		Set<VariableWrite> variableWrites = new HashSet<>();
		VariableRead nextVariable = null;

		for (int i = this.statements.length() - 1; i >= 0; i--)
		{
			Statement statement = this.statements.get(i);

			if (statement.equals(nextVariable) &&
				Collections.frequency(variableReads, statement) == 1)
			{
				this.statements.splice(i, 1);
				variables.optimise(nextVariable, (VariableWrite) statement);
			}
			else
			{
				variableWrites.addAll(
					Arrays.asList(statement.getVariableWrites().toArray()));
			}

			variableReads.removeAll(Collections.singleton(statement));

			if (statement instanceof VariableDeclaration)
			{
				if (!variableWrites.contains(statement))
				{
					this.statements.splice(i, 1);
				}
			}

			Expression expression = statement.getFirstOperand();

			if (expression instanceof VariableRead)
			{
				nextVariable = (VariableRead) expression;
			}

			variableReads.addAll(
				Arrays.asList(statement.getVariableReads().toArray()));
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

	@Override
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
}
