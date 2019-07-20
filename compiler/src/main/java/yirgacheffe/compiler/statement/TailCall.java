package yirgacheffe.compiler.statement;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.InvokeMethod;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

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

		if (invocation.equals(this))
		{
			InvokeMethod invokeMethod = (InvokeMethod) invocation;

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

	public Expression getExpression()
	{
		return new Nothing();
	}

	public boolean equals(String name, Array<Expression> arguments)
	{
		Array<Type> parameters = new Array<>();

		for (Expression argument: arguments)
		{
			parameters.push(argument.getType(this.variables));
		}

		return this.caller.equals(name, parameters);
	}

	public boolean isEmpty()
	{
		return this.invocation.isEmpty();
	}
}
