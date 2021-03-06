package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.LdcInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public class Char implements Expression, Literal
{
	private Coordinate coordinate;

	private String text;

	public Char(Coordinate coordinate, String text)
	{
		this.coordinate = coordinate;
		this.text = text;
	}

	public Type getType(Variables variables)
	{
		return this.getType();
	}

	public Type getType()
	{
		return PrimitiveType.CHAR;
	}

	public Result compile(Variables variables)
	{
		Result result = new Result();

		result = result.add(new LdcInsnNode(this.getValue()));

		variables.stackPush(this.getType(variables));

		return result;
	}

	public Result compileCondition(Variables variables, Label trueLabel, Label falseLabel)
	{
		return this.compile(variables);
	}

	public boolean isCondition(Variables variables)
	{
		return false;
	}

	public Array<VariableRead> getVariableReads()
	{
		return new Array<>();
	}

	public Object getValue()
	{
		return this.text.charAt(1);
	}

	public Coordinate getCoordinate()
	{
		return this.coordinate;
	}
}
