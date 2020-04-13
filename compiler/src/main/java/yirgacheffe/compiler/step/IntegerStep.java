package yirgacheffe.compiler.step;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;

public class IntegerStep implements Stepable
{
	public Result convertType()
	{
		return new Result();
	}

	public Result duplicate()
	{
		return new Result().add(new InsnNode(Opcodes.DUP));
	}

	public Result stepOne(boolean increment)
	{
		return new Result()
			.add(new InsnNode(Opcodes.ICONST_1))
			.add(new InsnNode(increment ? Opcodes.IADD : Opcodes.ISUB));
	}

	public Result stepOne(int index, boolean increment)
	{
		return new Result().add(new IincInsnNode(index, increment ? 1 : -1));
	}

	public Result store(int index)
	{
		return new Result().add(new VarInsnNode(Opcodes.ISTORE, index));
	}
}
