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

	public VariableRead(Coordinate coordinate, String name)
	{
		this.name = name;
		this.coordinate = coordinate;
	}

	public Type getType(Variables variables)
	{
		if (variables.canOptimise(this))
		{
			return variables.getOptimisedExpression(this).getType(variables);
		}
		else
		{
			return variables.getVariable(this.name).getType();
		}
	}

	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
	{
		if (variables.hasConstant(this.name))
		{
			methodVisitor.visitLdcInsn(variables.getConstant(this.name));
		}
		else
		{
			Variable variable = variables.getVariable(this.name);

			if (variables.canOptimise(this))
			{
				variables.getOptimisedExpression(this).compile(methodVisitor, variables);
			}
			else
			{
				int loadInstruction = variable.getType().getLoadInstruction();
				int index = variable.getIndex();

				methodVisitor.visitVarInsn(loadInstruction, index);
				variables.read(this);
			}
		}

		return new Array<>();
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
