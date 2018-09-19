package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.type.Variable;
import yirgacheffe.lang.Array;

import java.util.Map;

public class Block implements Statement
{
	private Array<Statement> statements;

	public Block(Array<Statement> statements)
	{
		this.statements = statements;
	}

	@Override
	public boolean compile(MethodVisitor methodVisitor, StatementResult result)
	{
		Map<String, Variable> declaredVariables = result.getDeclaredVariables();
		boolean returns = false;

		for (Statement statement: this.statements)
		{
			returns = statement.compile(methodVisitor, result) || returns;
		}

		result.setDeclaredVariables(declaredVariables);

		return returns;
	}
}
