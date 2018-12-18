package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.statement.VariableWrite;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variable;
import yirgacheffe.lang.Array;

public class VariableRead implements Expression
{
	private String name;

	private Coordinate coordinate;

	public VariableRead(String name, Coordinate coordinate)
	{
		this.name = name;
		this.coordinate = coordinate;
	}

	public Type getType(Variables variables)
	{
		return variables.getVariable(this.name).getType();
	}

	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
	{
		Variable variable = variables.getVariable(this.name);
		int loadInstruction = variable.getType().getLoadInstruction();
		int index = variable.getIndex();

		if (variables.canOptimise(this))
		{
			variables.getOptimisedExpression(this).compile(methodVisitor, variables);
		}
		else
		{
			methodVisitor.visitVarInsn(loadInstruction, index);
		}

		variables.read(this);

		return new Array<>();
	}

	public Expression getFirstOperand()
	{
		return this;
	}

	public Coordinate getCoordinate()
	{
		return this.coordinate;
	}

	public String getName()
	{
		return this.name;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof String)
		{
			return this.name.equals(other);
		}

		if (other instanceof VariableRead || other instanceof VariableWrite)
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

	public Array<VariableRead> getVariableReads()
	{
		return new Array<>(this);
	}
}
