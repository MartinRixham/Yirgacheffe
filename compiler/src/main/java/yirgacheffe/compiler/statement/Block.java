package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Variable;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

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

	@Override
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
		Map<String, Variable> declaredVariables = variables.getDeclaredVariables();
		boolean unreachableCode = false;
		Array<Error> errors = new Array<>();
		StatementResult blockResult = new StatementResult();

		for (int i = 0; i < this.statements.length(); i++)
		{
			StatementResult result =
				this.statements.get(i).compile(methodVisitor, variables);

			blockResult = blockResult.add(result);

			if (this.statements.get(i).returns() && i < this.statements.length() - 1)
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
}
