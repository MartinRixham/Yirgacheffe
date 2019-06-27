package yirgacheffe.compiler.comparison;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;

public class LessThanOrEqual implements Comparator
{
	@Override
	public Result compile(Label label, Type type)
	{
		Result result = new Result();

		if (type.equals(PrimitiveType.DOUBLE))
		{
			result = result
				.add(new InsnNode(Opcodes.DCMPG))
				.add(new JumpInsnNode(Opcodes.IFGT, new LabelNode(label)));
		}
		else if (type.equals(PrimitiveType.LONG))
		{
			result = result
				.add(new InsnNode(Opcodes.LCMP))
				.add(new JumpInsnNode(Opcodes.IFGT, new LabelNode(label)));
		}
		else
		{
			result = result.add(
				new JumpInsnNode(Opcodes.IF_ICMPGT, new LabelNode(label)));
		}

		return result;
	}
}
