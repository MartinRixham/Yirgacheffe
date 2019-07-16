package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
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
		return this.expression.getType(variables);
	}

	public Result compile(Variables variables)
	{
		Type type = this.getType(variables);
		Label start = new Label();
		Label end = new Label();
		Label handler = new Label();
		int variableCount = variables.variableCount();
		Array<Type> stack = new Array<>(variables.getStack());
		Result result = new Result();

		for (int i = stack.length() - 1; i >= 0; i--)
		{
			Type variableType = stack.get(i);
			variableCount += variableType.width();

			result = result.add(
				new VarInsnNode(
					variableType.getStoreInstruction(),
					variableCount));
		}

		result = result
			.add(new TryCatchBlockNode(
				new LabelNode(start),
				new LabelNode(end),
				new LabelNode(handler),
				"java/lang/Throwable"))
			.add(new LabelNode(start))
			.concat(this.expression.compile(variables))
			.concat(type.convertTo(new ReferenceType(Object.class)))
			.add(new LabelNode(end))
			.add(new LabelNode(handler));

		for (Type variableType: stack)
		{
			result = result
				.add(new VarInsnNode(
					variableType.getLoadInstruction(),
					variableCount))
				.add(new InsnNode(Opcodes.SWAP));

			variableCount -= variableType.width();
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
}
