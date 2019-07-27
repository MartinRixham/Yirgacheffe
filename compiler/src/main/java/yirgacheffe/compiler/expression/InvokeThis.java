package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.function.Arguments;
import yirgacheffe.compiler.function.FailedMatchResult;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.function.MatchResult;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.lang.reflect.Constructor;

public class InvokeThis implements Expression
{
	private Coordinate coordinate;

	private Type owner;

	private Array<Expression> arguments;

	public InvokeThis(
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
		return PrimitiveType.VOID;
	}

	public Result compile(Variables variables)
	{
		Constructor<?>[] constructors = this.owner.reflectionClass().getConstructors();
		Arguments arguments = new Arguments(this.arguments, variables);
		MatchResult matchResult = new FailedMatchResult();

		for (Constructor<?> constructor : constructors)
		{
			Function function = new Function(this.owner, constructor);

			matchResult = matchResult.betterOf(arguments.matches(function));
		}

		Array<Type> parameterTypes = matchResult.getParameterTypes();
		StringBuilder descriptor = new StringBuilder("(");

		descriptor.append(arguments.getDescriptor(parameterTypes));
		descriptor.append(")V");

		Result result = new Result()
			.add(new VarInsnNode(Opcodes.ALOAD, 0))
			.concat(matchResult.compileArguments(variables))
			.concat(this.coordinate.compile())
			.add(new MethodInsnNode(
				Opcodes.INVOKESPECIAL,
				this.owner.toFullyQualifiedType(),
				"<init>",
				descriptor.toString(),
				false));

		variables.stackPush(this.owner);

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
