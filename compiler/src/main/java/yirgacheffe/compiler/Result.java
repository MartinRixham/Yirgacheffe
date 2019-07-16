package yirgacheffe.compiler;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.lang.Array;

public class Result
{
	private Array<Error> errors;

	private Array<AbstractInsnNode> instructions;

	private Array<TryCatchBlockNode> tryCatchBlocks;

	public Result()
	{
		this.errors = new Array<>();
		this.instructions = new Array<>();
		this.tryCatchBlocks = new Array<>();
	}

	private Result(
		Array<Error> errors,
		Array<AbstractInsnNode> instructions,
		Array<TryCatchBlockNode> tryCatchBlocks)
	{
		this.errors = errors;
		this.instructions = instructions;
		this.tryCatchBlocks = tryCatchBlocks;
	}

	public Result add(Error error)
	{
		return new Result(
			this.errors.concat(new Array<>(error)),
			this.instructions,
			this.tryCatchBlocks);
	}

	public Result add(AbstractInsnNode instruction)
	{
		return new Result(
			this.errors,
			this.instructions.concat(new Array<>(instruction)),
			this.tryCatchBlocks);
	}

	public Result add(TryCatchBlockNode tryCatchBlock)
	{
		return new Result(
			this.errors,
			this.instructions,
			this.tryCatchBlocks.concat(new Array<>(tryCatchBlock)));
	}

	public Result concat(Result other)
	{
		return new Result(
			this.errors.concat(other.errors),
			this.instructions.concat(other.instructions),
			this.tryCatchBlocks.concat(other.tryCatchBlocks));
	}

	public Array<Error> getErrors()
	{
		return this.errors;
	}

	public Array<AbstractInsnNode> getInstructions()
	{
		return this.instructions;
	}

	public Array<TryCatchBlockNode> getTryCatchBlocks()
	{
		return this.tryCatchBlocks;
	}
}
