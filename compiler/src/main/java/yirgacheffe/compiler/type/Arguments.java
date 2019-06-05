package yirgacheffe.compiler.type;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.lang.Array;

import java.lang.reflect.TypeVariable;

public class Arguments
{
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

	public int matches(Array<Type> parameterTypes)
	{
		int exactMatches = 0;

		if (parameterTypes.length() != this.argumentTypes.length())
		{
			return -1;
		}

		for (int i = 0; i < parameterTypes.length(); i++)
		{
			Type argumentType = this.argumentTypes.get(i);

			if (!argumentType.isAssignableTo(parameterTypes.get(i)))
			{
				return -1;
			}

			if (argumentType.toJVMType().equals(parameterTypes.get(i).toJVMType()))
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
		Variables variables)
	{
		Array<Error> errors = new Array<>();

		for (int i = 0; i < Math.min(this.arguments.length(), parameters.length()); i++)
		{
			Expression argument = this.arguments.get(i);
			Type argumentType = this.argumentTypes.get(i);
			Type parameterType = parameters.get(i);

			errors.push(argument.compile(methodVisitor, variables));

			if (argumentType.isPrimitive() &&
				parameterType.isPrimitive() &&
				argumentType != parameterType)
			{
				PrimitiveType argumentPrimitive = (PrimitiveType) argumentType;
				PrimitiveType parameterPrimitive = (PrimitiveType) parameterType;

				methodVisitor.visitInsn(argumentPrimitive.convertTo(parameterPrimitive));
			}

			if (argumentType.isPrimitive() &&
				!parameters.get(i).isPrimitive())
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
		}

		return errors;
	}
}
