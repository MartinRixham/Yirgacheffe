package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public class Enumeration implements Expression
{
	private Type type;

	private Expression expression;

	public Enumeration(Type type, Expression expression)
	{
		this.type = type;
		this.expression = expression;
	}

	public Type getType(Variables variables)
	{
		return this.type;
	}

	public Result compile(Variables variables)
	{
		Literal literal = (Literal) expression;

		Result result = new Result()
			.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				this.type.toFullyQualifiedType(),
				literal.getValue().toString(),
				"()" + type.toJVMType()));

		variables.stackPush(this.type);

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
		return this.expression.getVariableReads();
	}
}
