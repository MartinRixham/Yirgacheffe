package yirgacheffe.compiler.step;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;

public class FloatStep implements Stepable
{
	private Type type;

	public FloatStep(Type type)
	{
		this.type = type;
	}

	public Result convertType()
	{
		return this.type.convertTo(PrimitiveType.DOUBLE);
	}

	public Result duplicate()
	{
		return new Result().add(new InsnNode(Opcodes.DUP2));
	}

	public Result stepOne(boolean increment)
	{
		return new Result()
			.add(new InsnNode(Opcodes.DCONST_1))
			.add(new InsnNode(increment ? Opcodes.DADD : Opcodes.DSUB));
	}

	public Result stepOne(int index, boolean increment)
	{
		return new Result()
			.add(new VarInsnNode(Opcodes.DLOAD, index))
			.add(new InsnNode(Opcodes.DCONST_1))
			.add(new InsnNode(increment ? Opcodes.DADD : Opcodes.DSUB))
			.add(new VarInsnNode(Opcodes.DSTORE, index));
	}

	public Result store(int index)
	{
		return new Result().add(new VarInsnNode(Opcodes.DSTORE, index));
	}
}
