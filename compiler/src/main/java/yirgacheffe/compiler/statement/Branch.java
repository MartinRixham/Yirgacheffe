package yirgacheffe.compiler.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.LabelNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.expression.Delegate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.Map;

public class Branch implements Statement
{
	private ConditionalStatement conditional;

	public Branch(ConditionalStatement conditional)
	{
		this.conditional = conditional;
	}

	public boolean returns()
	{
		return this.conditional.returns() && (this.conditional instanceof Else);
	}

	public Result compile(Variables variables, Signature caller)
	{
		Label label = this.conditional.getLabel();

		return this.conditional
			.compile(variables, caller)
			.add(new LabelNode(label));

	}

	public Array<VariableRead> getVariableReads()
	{
		return this.conditional.getVariableReads();
	}

	public Array<VariableWrite> getVariableWrites()
	{
		return this.conditional.getVariableWrites();
	}

	public Array<String> getFieldAssignments()
	{
		if (this.conditional instanceof Else)
		{
			return this.conditional.getFieldAssignments();
		}
		else
		{
			return new Array<>();
		}
	}

	public Array<Type> getDelegatedInterfaces(
		Map<Delegate, Type> delegateTypes,
		Type thisType)
	{
		if (this.conditional instanceof Else)
		{
			return this.conditional.getDelegatedInterfaces(delegateTypes, thisType);
		}
		else
		{
			return new Array<>();
		}
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
