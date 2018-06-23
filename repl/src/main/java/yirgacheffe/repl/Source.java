package yirgacheffe.repl;

import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.type.Classes;

public class Source
{
	private String statement;

	private String expression;

	public Source(String statement, String expression)
	{
		this.statement = statement;
		this.expression = expression;
	}

	public CompilationResult compile()
	{
		String source =
			"class Source\n" +
			"{\n" +
				"public String evaluate()" +
				"{" +
					this.statement +
					" return " + this.expression + ";" +
				"}" +
			"}";

		Compiler compiler = new Compiler("", source);

		return compiler.compile(new Classes());
	}
}
