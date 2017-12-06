package yirgacheffe.compiler;

import org.antlr.v4.runtime.tree.TerminalNode;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.List;

public class MethodDescriptor
{
	private List<YirgacheffeParser.ParameterContext> parameters;

	private TerminalNode returnType;

	public MethodDescriptor(
		List<YirgacheffeParser.ParameterContext> arguments,
		TerminalNode returnType)
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
			TerminalNode type = parameter.Type();

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
