package yirgacheffe.compiler;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import yirgacheffe.compiler.error.ParseErrorStrategy;
import yirgacheffe.compiler.listener.FunctionCallListener;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.error.ParseErrorListener;
import yirgacheffe.compiler.listener.ClassListener;
import yirgacheffe.compiler.listener.MainMethodListener;
import yirgacheffe.compiler.listener.YirgacheffeListener;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.ArrayList;
import java.util.List;

public class Compiler
{
	private String sourceFile;

	private String source;

	public Compiler(String sourceFile, String source)
	{
		this.sourceFile = sourceFile;
		this.source = source;
	}

	public void compileClassDeclaration(Classes classes) throws Exception
	{
		YirgacheffeListener listener =
			new ClassListener(
				this.sourceFile,
				classes);

		this.execute(listener);

		listener.exportDefinedTypes();
	}

	public void compileInterface(Classes classes) throws Exception
	{
		YirgacheffeListener listener =
			new MainMethodListener(
				this.sourceFile,
				classes);

		this.execute(listener);

		listener.exportDefinedTypes();
	}

	public CompilationResult compile(Classes classes) throws Exception
	{
		YirgacheffeListener listener =
			new FunctionCallListener(this.sourceFile, classes);

		List<Error> errors = this.execute(listener);

		if (errors.size() > 0)
		{
			return new CompilationResult(this.sourceFile, errors);
		}
		else
		{
			return listener.getCompilationResult();
		}
	}

	private List<Error> execute(YirgacheffeListener listener) throws Exception
	{
		List<Error> errors = new ArrayList<>();
		ParseErrorListener errorListener = new ParseErrorListener(errors);
		YirgacheffeParser parser = new Source(this.source).parse();
		parser.removeErrorListeners();
		parser.addErrorListener(errorListener);
		parser.setErrorHandler(new ParseErrorStrategy());

		ParseTree tree = parser.compilationUnit();

		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(listener, tree);

		return errors;
	}
}
