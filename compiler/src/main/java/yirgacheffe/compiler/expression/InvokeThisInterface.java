package yirgacheffe.compiler.expression;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.function.Arguments;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.function.MatchResult;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;
import yirgacheffe.lang.Bootstrap;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class InvokeThisInterface implements Expression
{
	private Coordinate coordinate;

	private Type owner;

	private Array<Expression> arguments;

	public InvokeThisInterface(
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
		Array<Function> constructors = this.getMethods();
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
		StringBuilder descriptor = new StringBuilder("(");

		descriptor.append(this.owner.toJVMType());
		descriptor.append(arguments.getDescriptor(parameterTypes));
		descriptor.append(")");
		descriptor.append(this.owner.toJVMType());

		MethodType methodType =
			MethodType.methodType(
				CallSite.class,
				MethodHandles.Lookup.class,
				String.class,
				MethodType.class);

		Handle bootstrapMethod =
			new Handle(
				Opcodes.H_INVOKESTATIC,
				Bootstrap.class.getName().replace(".", "/"),
				"bootstrapPrivate",
				methodType.toMethodDescriptorString(),
				false);

		Result result = new Result()
			.add(new VarInsnNode(Opcodes.ALOAD, 0))
			.concat(matchResult.compileArguments(variables))
			.concat(this.coordinate.compile())
			.add(new InvokeDynamicInsnNode(
				"0this",
				descriptor.toString(),
				bootstrapMethod));

		variables.stackPush(this.owner);

		return result;
	}

	public Array<Function> getMethods()
	{
		Set<Method> methodSet =
			new HashSet<>(Arrays.asList(owner.reflectionClass().getDeclaredMethods()));

		Array<Function> namedMethods = new Array<>();

		for (Method method: methodSet)
		{
			if (method.getName().equals("0this"))
			{
				namedMethods.push(new Function(owner, method));
			}
		}

		return namedMethods;
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
