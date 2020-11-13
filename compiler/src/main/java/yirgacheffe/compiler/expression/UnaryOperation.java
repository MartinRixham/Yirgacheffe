package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.assignment.BlockFieldAssignment;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.implementation.NullImplementation;
import yirgacheffe.compiler.statement.Statement;
import yirgacheffe.compiler.statement.VariableWrite;
import yirgacheffe.compiler.step.FloatStep;
import yirgacheffe.compiler.step.IntegerStep;
import yirgacheffe.compiler.step.LongIntegerStep;
import yirgacheffe.compiler.step.Stepable;
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
		Type type = this.expression.getType(variables);

		return this.getStepper(type).getType();
	}

	public Result compile(Variables variables)
	{
		Type type = this.expression.getType(variables);
		Stepable stepper = this.getStepper(type);
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

		result = result.concat(stepper.convertType());

		if (!this.pre && !canOptimise)
		{
			result = result.concat(stepper.duplicate());
		}

		if (this.pre || !canOptimise)
		{
			result = result.concat(stepper.stepOne(this.increment));
		}

		if (this.pre && !canOptimise)
		{
			result = result.concat(stepper.duplicate());
		}

		if (canOptimise)
		{
			return result;
		}
		else
		{
			return result.concat(this.updateVariable(stepper, variables));
		}
	}

	private Result updateVariable(Stepable stepper, Variables variables)
	{
		if (this.expression instanceof VariableRead)
		{
			VariableRead read = (VariableRead) this.expression;
			Variable variable = variables.getVariable(read.getName());

			return new Result().concat(stepper.store(variable.getIndex()));
		}

		return new Result();
	}

	public Result compile(Variables variables, Signature caller)
	{
		Type type = this.expression.getType(variables);
		Stepable stepper = this.getStepper(type);
		Result result = this.checkType(type);

		if (!(this.expression instanceof VariableRead) ||
			variables.canOptimise(this.expression))
		{
			return result;
		}

		VariableRead read = (VariableRead) this.expression;
		Variable variable = variables.getVariable(read.getName());

		return this.getErrors(variables)
			.concat(stepper.stepOne(variable.getIndex(), this.increment));
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

	private Result getErrors(Variables variables)
	{
		Result result = new Result();

		for (Error error: this.expression.compile(variables).getErrors())
		{
			result = result.add(error);
		}

		return result;
	}

	private Stepable getStepper(Type type)
	{
		if (type.equals(PrimitiveType.INT))
		{
			return new IntegerStep();
		}
		else if (type.equals(PrimitiveType.LONG))
		{
			return new LongIntegerStep();
		}
		else
		{
			return new FloatStep(type);
		}
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
