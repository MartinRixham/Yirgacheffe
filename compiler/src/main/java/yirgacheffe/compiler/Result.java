package yirgacheffe.compiler;

import org.objectweb.asm.tree.AbstractInsnNode;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.lang.Array;

public class Result
{
	private Array<Error> errors;

	private Array<AbstractInsnNode> instructions;

	public Result()
	{
		this.errors = new Array<>();
		this.instructions = new Array<>();
	}

	public Result(Array<Error> errors, Array<AbstractInsnNode> instructions)
	{
		this.errors = errors;
		this.instructions = instructions;
	}

	public Result add(Error error)
	{
		return new Result(
			this.errors.concat(new Array<>(error)),
			this.instructions);
	}

	public Result add(AbstractInsnNode instruction)
	{
		return new Result(
			this.errors,
			this.instructions.concat(new Array<>(instruction)));
	}

	public Result concat(Result other)
	{
		return new Result(
			this.errors.concat(other.errors),
			this.instructions.concat(other.instructions));
	}

	public Array<Error> getErrors()
	{
		return this.errors;
	}

	public Array<AbstractInsnNode> getInstructions()
	{
		return this.instructions;
	}
}
