package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class ParameterDeclaration implements Statement
{
	private String name;

	private Type type;

	public ParameterDeclaration(String name, Type type)
	{
		this.name = name;
		this.type = type;
	}

	public boolean returns()
	{
		return false;
	}

	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
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
}
