package yirgacheffe.compiler.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.LabelNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.assignment.BlockFieldAssignment;
import yirgacheffe.compiler.assignment.FieldAssignment;
import yirgacheffe.compiler.expression.Delegate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.implementation.BranchImplementation;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.implementation.NullImplementation;
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
		return this.conditional instanceof Else && this.conditional.returns();
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

	public FieldAssignment getFieldAssignments()
	{
		if (this.conditional instanceof Else || this.conditional.returns())
		{
			return this.conditional.getFieldAssignments();
		}
		else
		{
			return new BlockFieldAssignment(new Array<>());
		}
	}

	public Implementation getDelegatedInterfaces(
		Map<Delegate, Type> delegateTypes,
		Type thisType)
	{
		Implementation implementation =
			this.conditional.getDelegatedInterfaces(delegateTypes, thisType);

		if (this.conditional instanceof Else ||
			implementation instanceof BranchImplementation)
		{
			return implementation;
		}
		else
		{
			return new NullImplementation();
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
