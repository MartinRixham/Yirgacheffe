package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.function.Callable;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.function.Functions;
import yirgacheffe.compiler.function.MatchResult;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.compiler.function.Arguments;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

import java.lang.reflect.Constructor;

public class InvokeConstructor implements Expression
{
	private Coordinate coordinate;

	private Type owner;

	private Array<Expression> arguments;

	public InvokeConstructor(
		Coordinate coordinate,
		Type owner,
		Array<Expression> arguments)
	{
		this.coordinate = coordinate;
		this.owner = owner;
		this.arguments = arguments;
	}

	public Type getType(Variables variables)
	{
		return this.owner;
	}

	public Result compile(Variables variables)
	{
		Constructor<?>[] constructors = this.owner.reflectionClass().getConstructors();

		Array<Callable> functions = new Array<>();
		Arguments arguments = new Arguments(this.arguments, variables);

		for (Constructor<?> constructor : constructors)
		{
			functions.push(new Function(this.owner, constructor));
		}

		MatchResult matchResult =
			new Functions(
				this.coordinate,
				this.owner.toFullyQualifiedType(),
				functions,
				true)
				.getMatchingExecutable(arguments);

		if (this.owner instanceof NullType)
		{
			return matchResult.getResult();
		}

		Callable function = matchResult.getFunction();
		boolean variableArguments = function.hasVariableArguments();
		Array<Type> parameters = function.getParameterTypes();

		return new Result()
			.add(new TypeInsnNode(Opcodes.NEW, this.owner.toFullyQualifiedType()))
			.add(new InsnNode(Opcodes.DUP))
			.concat(arguments.compile(parameters, variables, variableArguments))
			.concat(this.coordinate.compile())
			.add(new MethodInsnNode(
				Opcodes.INVOKESPECIAL,
				this.owner.toFullyQualifiedType(),
				"<init>",
				matchResult.getFunction().getDescriptor(),
				false))
			.concat(matchResult.getResult());
	}

	public Result compileCondition(Variables variables, Label trueLabel, Label falseLabel)
	{
		return this.compile(variables);
	}

	public boolean isCondition(Variables variables)
	{
		return false;
	}

	public Array<VariableRead> getVariableReads()
	{
		Array<VariableRead> variableReads = new Array<>();

		for (Expression argument: this.arguments)
		{
			variableReads = variableReads.concat(argument.getVariableReads());
		}

		return variableReads;
	}
}
