package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class VariableDeclaration implements Statement
{
	private String name;

	private Type type;

	public VariableDeclaration(String name, Type type)
	{
		this.name = name;
		this.type = type;
	}

	public boolean returns()
	{
		return false;
	}

	public Array<Error> compile(
		MethodVisitor methodVisitor,
		Variables variables,
		Signature caller)
	{
		variables.declare(this.name, this.type);

		return new Array<>();
	}

	public Expression getFirstOperand()
	{
		return new Nothing();
	}

	public Array<VariableRead> getVariableReads()
	{
		return new Array<>();
	}

	@Override
	public Array<VariableWrite> getVariableWrites()
	{
		return new Array<>();
	}

	public Expression getExpression()
	{
		return new Nothing();
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof String)
		{
			return this.name.equals(other);
		}

		if (other instanceof VariableDeclaration || other instanceof VariableWrite)
		{
			return other.equals(this.name);
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return this.name.hashCode();
	}

	public boolean isEmpty()
	{
		return true;
	}
}
