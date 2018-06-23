package yirgacheffe.repl;

import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.type.Classes;

import java.util.List;

public class Source
{
	private List<String> imports;

	private List<String> statements;

	private String expression;

	public Source(List<String> imports, List<String> statements, String expression)
	{
		this.imports = imports;
		this.statements = statements;
		this.expression = expression;
	}

	public CompilationResult compile()
	{
		String source =
			String.join("\n", this.imports) +
			"class Source\n" +
			"{\n" +
				"public String evaluate()" +
				"{" +
					String.join(" ", this.statements) +
					" return " + this.expression + ".toString();" +
				"}" +
			"}";

		Compiler compiler = new Compiler("", source);

		return compiler.compile(new Classes());
	}
}
