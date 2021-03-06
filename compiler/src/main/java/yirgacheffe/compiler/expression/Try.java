package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.type.AttemptedType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public class Try implements Expression
{
	private Expression expression;

	public Try(Expression expression)
	{
		this.expression = expression;
	}

	public Type getType(Variables variables)
	{
		return new AttemptedType(this.expression.getType(variables));
	}

	public Result compile(Variables variables)
	{
		Type type = this.expression.getType(variables);
		Label start = new Label();
		Label end = new Label();
		Label handler = new Label();
		int variableCount = variables.nextVariableIndex();
		Array<Type> stack = new Array<>(variables.getStack());
		Result result = new Result();

		for (int i = stack.length() - 1; i >= 0; i--)
		{
			Type variableType = stack.get(i);

			result = result.add(
				new VarInsnNode(
					variableType.getStoreInstruction(),
					variableCount));

			variableCount += variableType.width();
		}

		result = result
			.add(new TryCatchBlockNode(
				new LabelNode(start),
				new LabelNode(end),
				new LabelNode(handler),
				null))
			.add(new LabelNode(start))
			.concat(this.expression.compile(variables))
			.concat(type.convertTo(new ReferenceType(Object.class)))
			.add(new LabelNode(end))
			.add(new LabelNode(handler));

		for (Type variableType: stack)
		{
			variableCount -= variableType.width();

			result = result
				.add(new VarInsnNode(
					variableType.getLoadInstruction(),
					variableCount))
				.add(new InsnNode(Opcodes.SWAP));
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
		return this.expression.getVariableReads();
	}

	public Coordinate getCoordinate()
	{
		return this.expression.getCoordinate();
	}
}
