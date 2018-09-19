package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.type.Type;

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
	public boolean compile(MethodVisitor methodVisitor, StatementResult result)
	{
		result.declare(this.name, this.type);

		return false;
	}
}
