package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
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

		methodVisitor.visitVarInsn(loadInstruction, index);

		variables.read(this);

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
}
