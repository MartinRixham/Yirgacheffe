package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.assignment.BlockFieldAssignment;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.implementation.NullImplementation;
import yirgacheffe.compiler.statement.Statement;
import yirgacheffe.compiler.statement.VariableWrite;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variable;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.Map;

public class UnaryOperation implements Expression, Statement
{
	private Coordinate coordinate;

	private Expression expression;

	private boolean pre;

	private boolean increment;

	public UnaryOperation(
		Coordinate coordinate,
		Expression expression,
		boolean pre,
		boolean increment)
	{
		this.coordinate = coordinate;
		this.expression = expression;
		this.pre = pre;
		this.increment = increment;
	}

	public Type getType(Variables variables)
	{
		return this.expression.getType(variables);
	}

	public Result compile(Variables variables)
	{
		Type type = this.expression.getType(variables);
		Result result = this.checkType(type);
		boolean canOptimise = variables.canOptimise(this.expression);

		if (canOptimise)
		{
			Expression optimisedExpression =
				variables.getOptimisedExpression(this.expression);

			result = result.concat(optimisedExpression.compile(variables));
		}
		else
		{
			result = result.concat(this.expression.compile(variables));
		}

		if (type.equals(PrimitiveType.INT))
		{
			if (this.pre || !canOptimise)
			{
				result = result
					.add(new InsnNode(Opcodes.ICONST_1))
					.add(new InsnNode(this.increment ? Opcodes.IADD : Opcodes.ISUB));
			}
		}
		else
		{
			result = result.concat(type.convertTo(PrimitiveType.DOUBLE));

			if (!this.pre && !canOptimise)
			{
				result = result.add(new InsnNode(Opcodes.DUP2));
			}

			if (this.pre || !canOptimise)
			{
				result = result
					.add(new InsnNode(Opcodes.DCONST_1))
					.add(new InsnNode(this.increment ? Opcodes.DADD : Opcodes.DSUB));
			}

			if (this.pre && !canOptimise)
			{
				result = result.add(new InsnNode(Opcodes.DUP2));
			}
		}

		if (canOptimise)
		{
			return result;
		}
		else
		{
			return result.concat(this.updateVariable(variables));
		}
	}

	private Result updateVariable(Variables variables)
	{
		if (this.expression instanceof VariableRead)
		{
			VariableRead read = (VariableRead) this.expression;
			Variable variable = variables.getVariable(read.getName());

			return new Result().add(
				new VarInsnNode(Opcodes.DSTORE, variable.getIndex()));
		}

		return new Result();
	}

	public Result compile(Variables variables, Signature caller)
	{
		Type type = this.expression.getType(variables);
		Result result = this.checkType(type);

		if (!(this.expression instanceof VariableRead) ||
			variables.canOptimise(this.expression))
		{
			return result;
		}

		VariableRead read = (VariableRead) this.expression;
		Variable variable = variables.getVariable(read.getName());

		if (type.equals(PrimitiveType.INT))
		{
			result = result.add(
				new IincInsnNode(variable.getIndex(), this.increment ? 1 : -1));
		}
		else
		{
			result = result
				.concat(this.expression.compile(variables))
				.add(new InsnNode(Opcodes.DCONST_1))
				.add(new InsnNode(this.increment ? Opcodes.DADD : Opcodes.DSUB))
				.add(new VarInsnNode(Opcodes.DSTORE, variable.getIndex()));
		}

		return result;
	}

	private Result checkType(Type type)
	{
		Result result = new Result();

		if (!type.isAssignableTo(PrimitiveType.DOUBLE))
		{
			String increment = this.increment ? "increment" : "decrement";
			String message = "Cannot " + increment + " " + type + ".";

			result = result.add(new Error(this.coordinate, message));
		}

		return result;
	}

	public Result compileCondition(Variables variables, Label trueLabel, Label falseLabel)
	{
		return this.compile(variables);
	}

	public boolean isCondition(Variables variables)
	{
		return false;
	}

	public boolean returns()
	{
		return false;
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.expression.getVariableReads();
	}

	public Coordinate getCoordinate()
	{
		return this.coordinate;
	}

	public Array<VariableWrite> getVariableWrites()
	{
		return new Array<>();
	}

	public BlockFieldAssignment getFieldAssignments()
	{
		return new BlockFieldAssignment(new Array<>());
	}

	public Implementation getDelegatedInterfaces(
		Map<Delegate, Type> delegateTypes,
		Type thisType)
	{
		return new NullImplementation();
	}

	public Expression getExpression()
	{
		return this.expression;
	}

	public boolean isEmpty()
	{
		return false;
	}
}
