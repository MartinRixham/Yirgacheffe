package yirgacheffe.compiler.comparison;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;

public class NotEquals implements Comparator
{
	@Override
	public Result compile(Label label, Type type)
	{
		Result result = new Result();

		if (!type.isPrimitive())
		{
			result = result.add(
				new JumpInsnNode(Opcodes.IF_ACMPEQ, new LabelNode(label)));
		}
		else if (type.equals(PrimitiveType.DOUBLE))
		{
			result = result
				.add(new InsnNode(Opcodes.DCMPL))
				.add(new JumpInsnNode(Opcodes.IFEQ, new LabelNode(label)));
		}
		else if (type.equals(PrimitiveType.LONG))
		{
			result = result
				.add(new InsnNode(Opcodes.LCMP))
				.add(new JumpInsnNode(Opcodes.IFEQ, new LabelNode(label)));
		}
		else
		{
			result = result.add(
				new JumpInsnNode(Opcodes.IF_ICMPEQ, new LabelNode(label)));
		}

		return result;
	}
}
