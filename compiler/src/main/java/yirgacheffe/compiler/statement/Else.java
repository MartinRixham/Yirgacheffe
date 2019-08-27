package yirgacheffe.compiler.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Delegate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.Map;

public class Else implements ConditionalStatement
{
	private Coordinate coordinate;

	private Statement precondition;

	private Statement statement;

	private Label label = new Label();

	public Else(Coordinate coordinate, Statement precondition, Statement statement)
	{
		this.coordinate = coordinate;
		this.precondition = precondition;
		this.statement = statement;
	}

	public boolean returns()
	{
		return this.precondition.returns() && this.statement.returns();
	}

	public Result compile(Variables variables, Signature caller)
	{
		Result result =
			this.precondition.compile(variables, caller)
				.add(new JumpInsnNode(Opcodes.GOTO, new LabelNode(this.label)));

		if (this.precondition instanceof If)
		{
			If ifStatement = (If) this.precondition;

			result = result.add(new LabelNode(ifStatement.getLabel()));
		}
		else
		{
			String message = "Else not preceded by if statement.";

			result = result.add(new Error(this.coordinate, message));
		}

		return result.concat(this.statement.compile(variables, caller));
	}

	public Label getLabel()
	{
		return this.label;
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.statement.getVariableReads()
			.concat(this.precondition.getVariableReads());
	}

	public Array<VariableWrite> getVariableWrites()
	{
		return this.statement.getVariableWrites()
			.concat(this.precondition.getVariableWrites());
	}

	public Array<String> getFieldAssignments()
	{
		Array<String> preconditionAssignments = this.precondition.getFieldAssignments();
		Array<String> statementAssignments = this.statement.getFieldAssignments();

		if (preconditionAssignments.contains("this"))
		{
			return statementAssignments;
		}
		else if (statementAssignments.contains("this"))
		{
			return preconditionAssignments;
		}
		else
		{
			Array<String> assignments = new Array<>();

			for (String assignment: preconditionAssignments)
			{
				if (statementAssignments.contains(assignment))
				{
					assignments.push(assignment);
				}
			}

			return assignments;
		}
	}

	public Array<Type> getDelegatedInterfaces(
		Map<Delegate, Type> delegateTypes,
		Type thisType)
	{
		Array<Type> delegatedInterface = new Array<>();

		Array<Type> preconditionInterfaces =
			this.precondition.getDelegatedInterfaces(delegateTypes, thisType);

		Array<Type> statementInterfaces =
			this.statement.getDelegatedInterfaces(delegateTypes, thisType);

		for (Type type: preconditionInterfaces)
		{
			if (statementInterfaces.contains(type))
			{
				delegatedInterface.push(type);
			}
		}

		return delegatedInterface;
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
