package yirgacheffe.compiler;

import yirgacheffe.parser.YirgacheffeParser;

import java.util.List;

public class MethodDescriptor
{
	private List<YirgacheffeParser.ParameterContext> parameters;

	private YirgacheffeParser.TypeContext returnType;

	public MethodDescriptor(
		List<YirgacheffeParser.ParameterContext> arguments,
		YirgacheffeParser.TypeContext returnType)
	{
		this.parameters = arguments;
		this.returnType = returnType;
	}

	@Override
	public String toString()
	{
		StringBuilder descriptor = new StringBuilder("(");

		for (YirgacheffeParser.ParameterContext parameter : this.parameters)
		{
			YirgacheffeParser.TypeContext type = parameter.type();

			if (type != null)
			{
				descriptor.append(Type.parse(type.getText()).getJVMType());
			}
		}

		descriptor.append(")");
		descriptor.append(Type.parse(this.returnType.getText()).getJVMType());

		return descriptor.toString();
	}
}
