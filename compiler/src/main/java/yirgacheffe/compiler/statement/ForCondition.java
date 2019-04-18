package yirgacheffe.compiler.statement;

import yirgacheffe.compiler.expression.Expression;

public class ForCondition
{
	private Statement initialiser;

	private Expression exitCondition;

	private Statement incrementer;

	public ForCondition(
		Statement initialiser,
		Expression exitCondition,
		Statement incrementer)
	{
		this.initialiser = initialiser;
		this.exitCondition = exitCondition;
		this.incrementer = incrementer;
	}

	public For getStatement(Block block)
	{
		return new For(
			this.initialiser,
			this.exitCondition,
			this.incrementer,
			block);
	}
}
