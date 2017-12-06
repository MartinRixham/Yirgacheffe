package yirgacheffe.compiler;

import org.antlr.v4.runtime.tree.TerminalNode;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.List;

public class MethodDescriptor
{
	private List<YirgacheffeParser.ArgumentContext> arguments;

	private TerminalNode returnType;

	public MethodDescriptor(
		List<YirgacheffeParser.ArgumentContext> arguments,
		TerminalNode returnType)
	{
		this.arguments = arguments;
		this.returnType = returnType;
	}

	@Override
	public String toString()
	{
		StringBuilder descriptor = new StringBuilder("(");

		for (YirgacheffeParser.ArgumentContext argument: this.arguments)
		{
			TerminalNode type = argument.Type();

			descriptor.append(Type.parse(type.getText()).getJVMType());
		}

		descriptor.append(")");
		descriptor.append(Type.parse(this.returnType.getText()).getJVMType());

		return descriptor.toString();
	}
}
