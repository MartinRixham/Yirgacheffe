package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;

public class VariableDeclaration implements Statement
{
	private String name;

	private Type type;

	public VariableDeclaration(String name, Type type)
	{
		this.name = name;
		this.type = type;
	}

	@Override
	public StatementResult compile(MethodVisitor methodVisitor, Variables variables)
	{
		variables.declare(this.name, this.type);

		return new StatementResult(false);
	}
}
