package yirgacheffe.compiler.function;

import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.LdcInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.type.JVMArrayType;
import yirgacheffe.compiler.type.AttemptedType;
import yirgacheffe.compiler.type.GenericType;
import yirgacheffe.compiler.type.MismatchedTypes;
import yirgacheffe.compiler.type.ParameterisedType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.TypeVariable;

public class Arguments
{
	private static final int THOUSAND = 1000;

	private Coordinate coordinate;

	private String name;

	private Array<Expression> arguments;

	private Array<Type> argumentTypes = new Array<>();

	public Arguments(
		Coordinate coordinate,
		String name,
		Array<Expression> arguments,
		Variables variables)
	{
		this.coordinate = coordinate;
		this.arguments = arguments;

		for (Expression argument: this.arguments)
		{
			this.argumentTypes.push(argument.getType(variables));
		}

		this.name = name + this.toString();
	}

	public MatchResult matches(Function function)
	{
		int exactMatches = 0;

		Array<Type> parameters = function.getParameterTypes();
		Array<Type> argumentTypes = this.argumentTypes.slice();

		if (this.hasVariableArguments(function.hasVariableArguments()))
		{
			JVMArrayType arrayType =
				(JVMArrayType) parameters.get(parameters.length() - 1);

			Type elementType = arrayType.getElementType();
			Type intersectionType = elementType;
			int argumentCount = argumentTypes.length();

			for (int i = parameters.length() - 1; i < argumentCount; i++)
			{
				Type type = argumentTypes.pop();
				intersectionType = intersectionType.intersect(type);
			}

			if (!intersectionType.isAssignableTo(elementType) &&
				!(elementType instanceof GenericType))
			{
				return new FailedMatchResult(this.coordinate, this.name);
			}
		}
		else if (parameters.length() != argumentTypes.length())
		{
			return new FailedMatchResult(this.coordinate, this.name);
		}
		else if (parameters.length() == 0)
		{
			return new SuccessfulMatchResult(
				this.coordinate,
				this.name,
				function,
				this,
				1,
				this.checkTypeParameters(function));
		}

		for (int i = 0; i < argumentTypes.length(); i++)
		{
			Type argumentType = argumentTypes.get(i);
			Type parameterType = parameters.get(i);

			if (!argumentType.isAssignableTo(parameterType) &&
				!(parameterType instanceof GenericType))
			{
				return new FailedMatchResult(this.coordinate, this.name);
			}
			else if (argumentType.toJVMType().equals(parameterType.toJVMType()))
			{
				exactMatches += THOUSAND;
			}
			else if (parameterType.isPrimitive() || parameterType instanceof JVMArrayType)
			{
				exactMatches++;
			}
		}

		return new SuccessfulMatchResult(
			this.coordinate,
			this.name,
			function,
			this,
			exactMatches,
			this.checkTypeParameters(function));
	}

	public MatchResult matches()
	{
		return new FailedMatchResult(this.coordinate, this.name);
	}

	private Array<MismatchedTypes> checkTypeParameters(Function function)
	{
		if (!(function.getOwner() instanceof ParameterisedType))
		{
			return new Array<>();
		}

		ParameterisedType owner = (ParameterisedType) function.getOwner();
		Array<java.lang.reflect.Type> parameters = function.getGenericParameterTypes();
		Array<MismatchedTypes> mismatchedParameters = new Array<>();

		for (int i = 0; i < this.argumentTypes.length(); i++)
		{
			java.lang.reflect.Type parameter =
				parameters.get(Math.min(parameters.length() - 1, i));

			if (parameter instanceof TypeVariable)
			{
				TypeVariable<?> typeVariable = (TypeVariable<?>) parameter;
				Type argumentType = this.argumentTypes.get(i);

				boolean hasTypeParameter =
					owner.hasTypeParameter(
						typeVariable.getName(),
						argumentType);

				if (!hasTypeParameter)
				{
					MismatchedTypes mismatchedTypes =
						new MismatchedTypes(
							argumentType.toString(),
							owner.getTypeParameterName(typeVariable.getName()));

					mismatchedParameters.push(mismatchedTypes);
				}
			}
			else if (parameter instanceof GenericArrayType)
			{
				Type argumentType;

				if (this.hasVariableArguments(function.hasVariableArguments()))
				{
					argumentType = this.argumentTypes.get(i);
				}
				else
				{
					argumentType =
						((JVMArrayType) this.argumentTypes.get(i)).getElementType();
				}

				GenericArrayType arrayType = (GenericArrayType) parameter;

				String name = arrayType.getGenericComponentType().getTypeName();

				boolean hasTypeParameter = owner.hasTypeParameter(name, argumentType);

				if (!hasTypeParameter)
				{
					MismatchedTypes mismatchedTypes =
						new MismatchedTypes(
							argumentType.toString(),
							owner.getTypeParameterName(name));

					mismatchedParameters.push(mismatchedTypes);
				}
			}
		}

		return mismatchedParameters;
	}

	@Override
	public String toString()
	{
		Array<String> arguments = new Array<>();

		for (Type argument : this.argumentTypes)
		{
			arguments.push(argument.toString());
		}

		return "(" + String.join(",", arguments) + ")";
	}

	public Result compile(
		Array<Type> parameters,
		boolean variableArguments,
		Variables variables)
	{
		Result result = new Result();
		int argumentCount = Math.min(this.arguments.length(), parameters.length());

		for (int i = 0; i < argumentCount; i++)
		{
			if (this.hasVariableArguments(variableArguments) &&
				i == parameters.length() - 1)
			{
				Array<Expression> arguments =
					this.arguments.slice(parameters.length() - 1);

				result = result.concat(
					this.compileVariableArguments(
						arguments,
						parameters,
						variables));
			}
			else
			{
				Expression argument = this.arguments.get(i);
				Type parameterType = parameters.get(i);

				result = result.concat(
					this.compileArgument(
						argument,
						parameterType,
						variables));
			}
		}

		for (int i = 0; i < argumentCount; i++)
		{
			variables.stackPop();
		}

		return result;
	}

	private Result compileVariableArguments(
		Array<Expression> arguments,
		Array<Type> parameters,
		Variables variables)
	{
		JVMArrayType arrayType = (JVMArrayType) parameters.get(parameters.length() - 1);
		Type elementType = arrayType.getElementType();

		int arrayLength = this.arguments.length() - parameters.length() + 1;

		Result result = new Result()
			.add(new LdcInsnNode(arrayLength))
			.concat(elementType.newArray());

		for (int i = 0; i < arrayLength; i++)
		{
			result = result
				.add(new InsnNode(Opcodes.DUP))
				.add(new LdcInsnNode(i))
				.concat(this.compileArgument(
					arguments.get(i),
					elementType,
					variables))
				.add(new InsnNode(elementType.getArrayStoreInstruction()));
		}

		return result;
	}

	private Result compileArgument(
		Expression argument,
		Type parameter,
		Variables variables)
	{
		Result result = argument.compile(variables);
		Type argumentType = argument.getType(variables);

		if (!(argumentType instanceof AttemptedType))
		{
			result = result.concat(argumentType.convertTo(parameter));
		}

		return result;
	}

	private boolean hasVariableArguments(boolean variableArguments)
	{
		if (this.argumentTypes.length() > 0)
		{
			Type lastArgumentType =
				this.argumentTypes.get(this.argumentTypes.length() - 1);

			return variableArguments && !(lastArgumentType instanceof JVMArrayType);
		}
		else
		{
			return variableArguments;
		}
	}

	public String getDescriptor(Array<Type> parameterTypes)
	{
		StringBuilder descriptor = new StringBuilder();

		for (int i = 0; i < parameterTypes.length(); i++)
		{
			Type argumentType = this.argumentTypes.get(i);

			if (argumentType instanceof AttemptedType)
			{
				descriptor.append("Ljava/lang/Object;");
			}
			else
			{
				Type parameter = parameterTypes.get(i);
				descriptor.append(parameter.toJVMType());
			}
		}

		return descriptor.toString();
	}
}
