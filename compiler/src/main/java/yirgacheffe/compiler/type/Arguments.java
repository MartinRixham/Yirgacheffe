package yirgacheffe.compiler.type;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.lang.Array;

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

		for (int i = 0; i < parameters.length(); i++)
		{
			if (parameters.get(i) instanceof TypeVariable)
			{
				TypeVariable typeVariable = (TypeVariable) parameters.get(i);
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

	public Array<Error> compile(
		Array<Type> parameters,
		MethodVisitor methodVisitor,
		Variables variables,
		boolean variableArguments)
	{
		Array<Error> errors = new Array<>();

		for (int i = 0; i < Math.min(this.arguments.length(), parameters.length()); i++)
		{
			if (this.hasVariableArguments(variableArguments) &&
				i == parameters.length() - 1)
			{
				Array<Expression> arguments =
					this.arguments.slice(parameters.length() - 1);

				Array<Error> argumentErrors =
					this.compileVariableArguments(
						arguments,
						parameters,
						methodVisitor,
						variables);

				errors = errors.concat(argumentErrors);
			}
			else
			{
				Expression argument = this.arguments.get(i);
				Type parameterType = parameters.get(i);

				Array<Error> argumentErrors =
					this.compileArgument(
						argument,
						parameterType,
						methodVisitor,
						variables);

				errors = errors.concat(argumentErrors);
			}
		}

		return errors;
	}

	private Array<Error> compileVariableArguments(
		Array<Expression> arguments,
		Array<Type> parameters,
		MethodVisitor methodVisitor,
		Variables variables)
	{
		ArrayType arrayType = (ArrayType) parameters.get(parameters.length() - 1);
		Type elementType = arrayType.getElementType();

		int arrayLength = this.arguments.length() - parameters.length() + 1;

		methodVisitor.visitLdcInsn(arrayLength);

		if (elementType.isPrimitive())
		{
			PrimitiveType primitiveType = (PrimitiveType) elementType;
			int typeInstruction = primitiveType.getTypeInstruction();

			methodVisitor.visitIntInsn(Opcodes.NEWARRAY, typeInstruction);
		}
		else
		{
			methodVisitor.visitTypeInsn(
				Opcodes.ANEWARRAY,
				elementType.toFullyQualifiedType());
		}

		for (int i = 0; i < arrayLength; i++)
		{
			methodVisitor.visitInsn(Opcodes.DUP);
			methodVisitor.visitLdcInsn(i);

			this.compileArgument(
				arguments.get(i),
				elementType,
				methodVisitor,
				variables);

			methodVisitor.visitInsn(elementType.getArrayStoreInstruction());
		}

		return new Array<>();
	}

	private Array<Error> compileArgument(
		Expression argument,
		Type parameter,
		MethodVisitor methodVisitor,
		Variables variables)
	{
		Array<Error> errors = argument.compile(methodVisitor, variables);
		Type argumentType = argument.getType(variables);

		if (argumentType.isPrimitive() &&
			parameter.isPrimitive() &&
			argumentType != parameter)
		{
			PrimitiveType argumentPrimitive = (PrimitiveType) argumentType;
			PrimitiveType parameterPrimitive = (PrimitiveType) parameter;

			methodVisitor.visitInsn(argumentPrimitive.convertTo(parameterPrimitive));
		}
		else if (argumentType.isPrimitive() && !parameter.isPrimitive())
		{
			String descriptor =
				"(" + argumentType.toJVMType() + ")L" +
					argumentType.toFullyQualifiedType() + ";";

			methodVisitor.visitMethodInsn(
				Opcodes.INVOKESTATIC,
				argumentType.toFullyQualifiedType(),
				"valueOf",
				descriptor,
				false);
		}

		return errors;
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
