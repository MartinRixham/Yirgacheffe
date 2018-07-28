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
	public void compile(MethodVisitor methodVisitor, StatementResult result)
	{
		Map<String, Variable> declaredVariables = result.getDeclaredVariables();

		for (Statement statement: this.statements)
		{
			statement.compile(methodVisitor, result);
		}

		result.setDeclaredVariables(declaredVariables);
	}
}
