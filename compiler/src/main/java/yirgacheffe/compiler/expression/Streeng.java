package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.LdcInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public class Streeng implements Expression, Literal
{
	private Coordinate coordinate;

	private String text;

	public Streeng(Coordinate coordinate, String text)
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
		return new ReferenceType(String.class);
	}

	public Result compile(Variables variables)
	{
		variables.stackPush(this.getType(variables));

		return new Result().add(new LdcInsnNode(this.getValue()));
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

	public Coordinate getCoordinate()
	{
		return this.coordinate;
	}

	public Object getValue()
	{
		return this.text.substring(1, this.text.length() - 1);
	}
}
