package yirgacheffe.compiler.type;

import yirgacheffe.parser.YirgacheffeParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parameters
{
	private List<Type> parameterTypes;

	public Parameters(Type[] parameterTypes)
	{
		this.parameterTypes = Arrays.asList(parameterTypes);
	}

	public Parameters(
		List<YirgacheffeParser.ParameterContext> parameterList, Types types)
	{
		this.parameterTypes = new ArrayList<>();

		for (YirgacheffeParser.ParameterContext parameter : parameterList)
		{
			YirgacheffeParser.TypeContext typeContext = parameter.type();

			if (typeContext != null)
			{
				this.parameterTypes.add(types.getType(typeContext));
			}
		}
	}

	public String getDescriptor()
	{
		StringBuilder descriptor = new StringBuilder("(");

		for (Type type : this.parameterTypes)
		{
			descriptor.append(type.toJVMType());
		}

		descriptor.append(")");

		return descriptor.toString();
	}

	@Override
	public String toString()
	{
		StringBuilder descriptor = new StringBuilder("(");

		for (Type type : this.parameterTypes)
		{
			descriptor.append(type);
		}

		descriptor.append(")");

		return descriptor.toString();
	}
}
