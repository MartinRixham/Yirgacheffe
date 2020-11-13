package yirgacheffe.compiler.step;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;

public class LongIntegerStep implements Stepable
{
	public Result convertType()
	{
		return new Result();
	}

	public Result duplicate()
	{
		return new Result().add(new InsnNode(Opcodes.DUP2));
	}

	public Result stepOne(boolean increment)
	{
		return new Result()
			.add(new InsnNode(Opcodes.LCONST_1))
			.add(new InsnNode(increment ? Opcodes.LADD : Opcodes.LSUB));
	}

	public Result stepOne(int index, boolean increment)
	{
		return new Result()
			.add(new VarInsnNode(Opcodes.LLOAD, index))
			.add(new InsnNode(Opcodes.LCONST_1))
			.add(new InsnNode(increment ? Opcodes.LADD : Opcodes.LSUB))
			.add(new VarInsnNode(Opcodes.LSTORE, index));
	}

	public Result store(int index)
	{
		return new Result().add(new VarInsnNode(Opcodes.LSTORE, index));
	}

	public Type getType()
	{
		return PrimitiveType.LONG;
	}
}
