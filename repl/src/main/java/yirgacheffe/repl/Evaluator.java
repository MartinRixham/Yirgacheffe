package yirgacheffe.repl;

import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.lang.Array;

public class Evaluator
{
	private Array<String> imports;

	private Array<String> statements;

	private String expression;

	public Evaluator(Array<String> imports, Array<String> statements, String expression)
	{
		this.imports = imports;
		this.statements = statements;
		this.expression = expression;
	}

	public CompilationResult compile()
	{
		String source =
			String.join("\n", this.imports) +
			"\nclass Source\n" +
			"{\n" +
				"public String evaluate()" +
				"{" +
					String.join(" ", this.statements) +
					" return " + this.expression + ".toString();" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);

		return compiler.compile(new Classes());
	}
}
