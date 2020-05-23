package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.function.Arguments;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.function.MatchResult;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.Set;

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
		Set<Function> constructors = this.owner.reflect().getPublicConstructors();
		String name = "constructor " + this.owner;

		Arguments arguments =
			new Arguments(
				this.coordinate,
				name,
				this.arguments,
				variables);

		MatchResult matchResult = arguments.matches();

		for (Function constructor : constructors)
		{
			matchResult = matchResult.betterOf(arguments.matches(constructor));
		}

		Array<Type> parameterTypes = matchResult.getParameterTypes();

		String descriptor =
			"(" + arguments.getDescriptor(parameterTypes) + ")V";

		Result result = new Result()
			.concat(this.owner.construct(coordinate))
			.add(new TypeInsnNode(Opcodes.NEW, this.owner.toFullyQualifiedType()))
			.add(new InsnNode(Opcodes.DUP))
			.concat(matchResult.compileArguments(variables))
			.concat(this.coordinate.compile())
			.add(new MethodInsnNode(
				Opcodes.INVOKESPECIAL,
				this.owner.toFullyQualifiedType(),
				"<init>",
				descriptor,
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

	public Coordinate getCoordinate()
	{
		return this.coordinate;
	}
}
