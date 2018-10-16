package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variable;

public class VariableRead implements Expression
{
	private String name;

	private Variable variable;

	private Coordinate coordinate;

	public VariableRead(String name, Coordinate coordinate)
	{
		this.name = name;
		this.coordinate = coordinate;
	}

	@Override
	public Type check(Variables result)
	{
		this.variable = result.getVariable(this.name);

		result.read(this);

		return this.variable.getType();
	}

	@Override
	public ExpressionResult compile(MethodVisitor methodVisitor)
	{
		int loadInstruction = this.variable.getType().getLoadInstruction();
		int index = this.variable.getIndex();

		methodVisitor.visitVarInsn(loadInstruction, index);

		return new ExpressionResult();
	}

	public Coordinate getCoordinate()
	{
		return this.coordinate;
	}

	public String getName()
	{
		return this.name;
	}
}
