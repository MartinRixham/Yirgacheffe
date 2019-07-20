package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public class This implements Expression
{
	private Type type;

	public This(Type type)
	{
		this.type = type;
	}

	public Type getType(Variables variables)
	{
		return this.type;
	}

	public Result compile(Variables variables)
	{
		variables.stackPush(this.getType(variables));

		return new Result().add(new VarInsnNode(Opcodes.ALOAD, 0));
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
}
