package yirgacheffe.compiler.function;

import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.type.ArrayType;
import yirgacheffe.compiler.type.IntersectionType;
import yirgacheffe.compiler.type.MismatchedTypes;
import yirgacheffe.compiler.type.ParameterisedType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.TypeVariable;

public class Arguments
{
	private static final int THOUSAND = 1000;

	private Array<Expression> arguments;

	private Array<Type> argumentTypes = new Array<>();

	public Arguments(Array<Expression> arguments, Variables variables)
	{
		this.arguments = arguments;

		for (Expression argument: this.arguments)
		{
			this.argumentTypes.push(argument.getType(variables));
		}
	}

	public Array<MismatchedTypes> checkTypeParameters(
		Array<java.lang.reflect.Type> parameters,
		ParameterisedType owner)
	{
		Array<MismatchedTypes> mismatchedParameters = new Array<>();

		for (int i = 0; i < this.argumentTypes.length(); i++)
		{
			java.lang.reflect.Type parameter =
				parameters.get(Math.min(parameters.length() - 1, i));

			if (parameter instanceof TypeVariable)
			{
				TypeVariable typeVariable = (TypeVariable) parameter;
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
				GenericArrayType arrayType = (GenericArrayType) parameter;
				Type argumentType = this.argumentTypes.get(i);

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

	public int matches(Array<Type> parameters, boolean variableArguments)
	{
		int exactMatches = 0;

		Array<Type> argumentTypes = this.argumentTypes.slice();

		if (this.hasVariableArguments(variableArguments))
		{
			ArrayType arrayType = (ArrayType) parameters.get(parameters.length() - 1);
			Type elementType = arrayType.getElementType();
			Type intersectionType = elementType;
			int argumentCount = argumentTypes.length();

			for (int i = parameters.length() - 1; i < argumentCount; i++)
			{
				Type type = argumentTypes.pop();
				intersectionType = new IntersectionType(intersectionType, type);
			}

			if (!intersectionType.isAssignableTo(elementType))
			{
				return -1;
			}
		}
		else if (parameters.length() != argumentTypes.length())
		{
			return -1;
		}
		else if (parameters.length() == 0)
		{
			return 1;
		}

		for (int i = 0; i < argumentTypes.length(); i++)
		{
			Type argumentType = argumentTypes.get(i);
			Type parameterType = parameters.get(i);

			if (!argumentType.isAssignableTo(parameterType))
			{
				return -1;
			}
			else if (argumentType.toJVMType().equals(parameterType.toJVMType()))
			{
				exactMatches += THOUSAND;
			}
			else if (parameterType.isPrimitive())
			{
				exactMatches++;
			}
		}

		return exactMatches;
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
		Variables variables,
		boolean variableArguments)
	{
		Result result = new Result();

		for (int i = 0; i < Math.min(this.arguments.length(), parameters.length()); i++)
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

		return result;
	}

	private Result compileVariableArguments(
		Array<Expression> arguments,
		Array<Type> parameters,
		Variables variables)
	{
		ArrayType arrayType = (ArrayType) parameters.get(parameters.length() - 1);
		Type elementType = arrayType.getElementType();

		int arrayLength = this.arguments.length() - parameters.length() + 1;

		Result result = new Result().add(new LdcInsnNode(arrayLength));

		if (elementType.isPrimitive())
		{
			PrimitiveType primitiveType = (PrimitiveType) elementType;
			int typeInstruction = primitiveType.getTypeInstruction();

			result = result.add(new IntInsnNode(Opcodes.NEWARRAY, typeInstruction));
		}
		else
		{
			result = result.add(
				new TypeInsnNode(Opcodes.ANEWARRAY, elementType.toFullyQualifiedType()));
		}

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

		if (argumentType.isPrimitive() &&
			parameter.isPrimitive() &&
			argumentType != parameter)
		{
			PrimitiveType argumentPrimitive = (PrimitiveType) argumentType;
			PrimitiveType parameterPrimitive = (PrimitiveType) parameter;

			return result.add(
				new InsnNode(argumentPrimitive.convertTo(parameterPrimitive)));
		}
		else if (argumentType.isPrimitive() && !parameter.isPrimitive())
		{
			String descriptor =
				"(" + argumentType.toJVMType() + ")L" +
					argumentType.toFullyQualifiedType() + ";";

			return result.add(
				new MethodInsnNode(
					Opcodes.INVOKESTATIC,
					argumentType.toFullyQualifiedType(),
					"valueOf",
					descriptor,
					false));
		}
		else
		{
			return result;
		}
	}

	private boolean hasVariableArguments(boolean variableArguments)
	{
		if (this.argumentTypes.length() > 0)
		{
			Type lastArgumentType =
				this.argumentTypes.get(this.argumentTypes.length() - 1);

			return variableArguments && !(lastArgumentType instanceof ArrayType);
		}
		else
		{
			return variableArguments;
		}
	}
}
