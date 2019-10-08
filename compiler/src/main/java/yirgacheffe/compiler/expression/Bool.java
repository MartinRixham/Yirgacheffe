package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public class Bool implements Expression, Literal
{
	private String text;

	public Bool(String text)
	{
		this.text = text;
	}

	public Type getType(Variables variables)
	{
		return this.getType();
	}

	public Type getType()
	{
		return PrimitiveType.BOOLEAN;
	}

	public Result compile(Variables variables)
	{
		variables.stackPush(this.getType(variables));

		Result result = new Result();

		if (this.text.equals("true"))
		{
			result = result.add(new InsnNode(Opcodes.ICONST_1));
		}
		else
		{
			result = result.add(new InsnNode(Opcodes.ICONST_0));
		}

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
		return this.text.equals("true");
	}
}
