package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Variable;
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
	public boolean compile(MethodVisitor methodVisitor, StatementResult result)
	{
		Map<String, Variable> declaredVariables = result.getDeclaredVariables();
		boolean returns = false;
		boolean unreachableCode = false;

		for (int i = 0; i < this.statements.length(); i++)
		{
			returns = this.statements.get(i).compile(methodVisitor, result) || returns;

			if (returns && i < this.statements.length() - 1)
			{
				unreachableCode = true;
			}
		}

		if (unreachableCode)
		{
			String message = "Unreachable code.";

			result.error(new Error(this.coordinate, message));
		}

		result.setDeclaredVariables(declaredVariables);

		return returns;
	}
}
