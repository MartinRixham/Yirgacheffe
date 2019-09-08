package yirgacheffe.compiler.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.LabelNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.assignment.FieldAssignment;
import yirgacheffe.compiler.expression.Delegate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.operator.BooleanOperator;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.Map;

public class If implements ConditionalStatement
{
	private Expression condition;

	private Statement statement;

	private Label falseLabel = new Label();

	public If(Expression condition, Statement statement)
	{
		this.condition = condition;
		this.statement = statement;
	}

	public boolean returns()
	{
		return this.statement.returns();
	}

	public Result compile(Variables variables, Signature caller)
	{
		Result result = new Result();
		Type type = this.condition.getType(variables);

		Label trueLabel = new Label();

		if (this.condition.isCondition(variables))
		{
			result = result
				.concat(this.condition.compileCondition(
					variables,
					trueLabel,
					this.falseLabel));
		}
		else
		{
			result = result
				.concat(this.condition.compile(variables))
				.concat(type.compare(BooleanOperator.AND, this.falseLabel));
		}

		variables.stackPop();

		return result
			.add(new LabelNode(trueLabel))
			.concat(this.statement.compile(variables, caller));
	}

	public Label getLabel()
	{
		return this.falseLabel;
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.condition.getVariableReads()
			.concat(this.statement.getVariableReads());
	}

	public Array<VariableWrite> getVariableWrites()
	{
		return this.statement.getVariableWrites();
	}

	public FieldAssignment getFieldAssignments()
	{
		return this.statement.getFieldAssignments();
	}

	public Implementation getDelegatedInterfaces(
		Map<Delegate, Type> delegateTypes,
		Type thisType)
	{
		return this.statement.getDelegatedInterfaces(delegateTypes, thisType);
	}

	public Expression getExpression()
	{
		return new Nothing();
	}

	public boolean isEmpty()
	{
		return false;
	}
}
