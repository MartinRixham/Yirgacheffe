package yirgacheffe.compiler.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.assignment.FieldAssignment;
import yirgacheffe.compiler.expression.Delegate;
import yirgacheffe.compiler.expression.Equation;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.implementation.NullImplementation;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.Map;

public class For implements Statement
{
	private Statement initialiser;

	private Expression exitCondition;

	private Statement incrementer;

	private Statement statement;

	public For(
		Statement initialiser,
		Expression exitCondition,
		Statement incrementer,
		Statement statement)
	{
		this.initialiser = initialiser;
		this.exitCondition = exitCondition;
		this.incrementer = incrementer;
		this.statement = statement;
	}

	public boolean returns()
	{
		return false;
	}

	public Result compile(Variables variables, Signature caller)
	{
		Label exitLabel = new Label();
		Label continueLabel = new Label();

		Result result =
			this.initialiser.compile(variables, caller)
				.add(new LabelNode(continueLabel));

		if (this.exitCondition instanceof Equation)
		{
			Equation equation = (Equation) this.exitCondition;

			result = result.concat(
				equation.compileCondition(variables, continueLabel, exitLabel));
		}
		else
		{
			result = result
				.concat(this.exitCondition.compile(variables))
				.add(new JumpInsnNode(Opcodes.IFEQ, new LabelNode(exitLabel)));
		}

		variables.stackPop();

		return result
			.concat(this.statement.compile(variables, caller))
			.concat(this.incrementer.compile(variables, caller))
			.add(new JumpInsnNode(Opcodes.GOTO, new LabelNode(continueLabel)))
			.add(new LabelNode(exitLabel));
	}
	public Array<VariableRead> getVariableReads()
	{
		return this.initialiser.getVariableReads();
	}

	public Array<VariableWrite> getVariableWrites()
	{
		return this.initialiser.getVariableWrites();
	}

	public FieldAssignment getFieldAssignments()
	{
		return new FieldAssignment(new Array<>());
	}

	public Implementation getDelegatedInterfaces(
		Map<Delegate, Type> delegateTypes,
		Type thisType)
	{
		return new NullImplementation();
	}

	public Expression getExpression()
	{
		return this.statement.getExpression();
	}

	public boolean isEmpty()
	{
		return false;
	}
}
