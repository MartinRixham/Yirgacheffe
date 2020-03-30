package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.function.Arguments;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.function.MatchResult;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.lang.reflect.Method;

public class InvokeInterfaceConstructor implements Expression
{
	private Coordinate coordinate;

	private Type owner;

	private Array<Expression> arguments;

	public InvokeInterfaceConstructor(
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
		String name = "constructor " + this.owner;

		Arguments arguments =
			new Arguments(
				this.coordinate,
				name,
				this.arguments,
				variables);

		MatchResult matchResult = arguments.matches();

		for (Function method: this.getMethods())
		{
			matchResult = matchResult.betterOf(arguments.matches(method));
		}

		Array<Type> parameterTypes = matchResult.getParameterTypes();

		String descriptor =
			"(" + arguments.getDescriptor(parameterTypes) + ")" +
				this.owner.toJVMType();

		Result result = new Result()
			.concat(matchResult.compileArguments(variables))
			.concat(this.coordinate.compile())
			.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				this.owner.toFullyQualifiedType(),
				"0this",
				descriptor,
				true));

		variables.stackPush(this.owner);

		return result;
	}

	private Array<Function> getMethods()
	{
		Array<Function> constructorMethods = new Array<>();
		Method[] methods = this.owner.reflectionClass().getMethods();

		for (Method method: methods)
		{
			if (method.getName().equals("0this"))
			{
				constructorMethods.push(new Function(owner, method));
			}
		}

		return constructorMethods;
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

	public Coordinate getCoordinate()
	{
		return this.coordinate;
	}
}
