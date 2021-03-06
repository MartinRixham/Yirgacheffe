package yirgacheffe.compiler.statement;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.assignment.FieldAssignment;
import yirgacheffe.compiler.expression.Delegate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.Parameterisable;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.Map;

public class TailCall implements Statement
{
	private Statement invocation;

	private Signature caller;

	private Variables variables;

	public TailCall(Statement invocation, Signature caller, Variables variables)
	{
		this.invocation = invocation;
		this.caller = caller;
		this.variables = variables;
	}

	public boolean returns()
	{
		return this.invocation.returns();
	}

	public Result compile(Variables variables, Signature caller)
	{
		Expression invocation = this.invocation.getExpression();

		if (variables.canOptimise(invocation))
		{
			invocation = variables.getOptimisedExpression(invocation);
		}

		if (invocation.equals(this) && !(this.invocation instanceof AttemptedStatement))
		{
			Parameterisable invokeMethod = (Parameterisable) invocation;

			Result result = invokeMethod.compileArguments(variables);

			Array<Type> parameters = invokeMethod.getParameters(variables);

			int width = this.getWidth(parameters);

			for (int i = parameters.length() - 1; i >= 0; i--)
			{
				int storeInstruction = parameters.get(i).getStoreInstruction();

				width -= parameters.get(i).width();

				result = result.add(new VarInsnNode(storeInstruction, width));
			}

			return result.add(new JumpInsnNode(
				Opcodes.GOTO,
				new LabelNode(caller.getLabel())));
		}
		else
		{
			return this.invocation.compile(variables, caller);
		}
	}

	private int getWidth(Array<Type> parameters)
	{
		int width = 1;

		for (Type type: parameters)
		{
			width += type.width();
		}

		return width;
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.invocation.getVariableReads();
	}

	public Array<VariableWrite> getVariableWrites()
	{
		return this.invocation.getVariableWrites();
	}

	public FieldAssignment getFieldAssignments()
	{
		return this.invocation.getFieldAssignments();
	}

	public Implementation getDelegatedInterfaces(
		Map<Delegate, Type> delegateTypes,
		Type thisType)
	{
		return this.invocation.getDelegatedInterfaces(delegateTypes, thisType);
	}

	public Expression getExpression()
	{
		return new Nothing();
	}

	public boolean equals(String name, Array<Expression> arguments)
	{
		Array<String> parameters = new Array<>();

		for (Expression argument: arguments)
		{
			parameters.push(argument.getType(this.variables).toJVMType());
		}

		return this.caller.equals(new Array<>(name, parameters));
	}

	public boolean isEmpty()
	{
		return this.invocation.isEmpty();
	}
}
