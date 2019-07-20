package yirgacheffe.compiler.comparison;

import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.variables.Variables;

public class StringComparison implements Comparison
{
	private Comparator comparator;
	private Expression firstOperand;
	private Expression secondOperand;

	public StringComparison(
		Comparator comparator,
		Expression firstOperand,
		Expression secondOperand)
	{
		this.comparator = comparator;
		this.firstOperand = firstOperand;
		this.secondOperand = secondOperand;
	}

	public Result compile(Variables variables, Label label)
	{
		Result result =
			this.firstOperand.compile(variables)
				.concat(this.secondOperand.compile(variables))
				.add(new MethodInsnNode(
					Opcodes.INVOKEVIRTUAL,
					"java/lang/String",
					"equals",
					"(Ljava/lang/Object;)Z",
					false));

		if (this.comparator instanceof Equals)
		{
			return result.add(new JumpInsnNode(Opcodes.IFEQ, new LabelNode(label)));
		}
		else
		{
			return result.add(new JumpInsnNode(Opcodes.IFNE, new LabelNode(label)));
		}
	}
}
