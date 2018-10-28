package yirgacheffe.compiler;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.error.ParseErrorStrategy;
import yirgacheffe.compiler.error.ParseErrorListener;
import yirgacheffe.compiler.listener.BinaryOperationListener;
import yirgacheffe.compiler.listener.ClassListener;
import yirgacheffe.compiler.listener.FieldDeclarationListener;
import yirgacheffe.compiler.listener.YirgacheffeListener;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.lang.Array;
import yirgacheffe.parser.YirgacheffeParser;

public class Compiler
{
	private String sourceFile;

	private ParseTree tree;

	private Array<Error> errors;

	public Compiler(String sourceFile, String source)
	{
		this.sourceFile = sourceFile;
		this.errors = new Array<>();

		ParseErrorListener errorListener = new ParseErrorListener(this.errors);
		YirgacheffeParser parser = new Source(source).parse();
		parser.removeErrorListeners();
		parser.addErrorListener(errorListener);
		parser.setErrorHandler(new ParseErrorStrategy());

		this.tree = parser.compilationUnit();
	}

	public void compileClassDeclaration(Classes classes)
	{
		YirgacheffeListener listener = new ClassListener(this.sourceFile, classes);

		this.execute(listener);

		listener.exportDefinedTypes();
	}

	public void compileInterface(Classes classes)
	{
		YirgacheffeListener listener =
			new FieldDeclarationListener(this.sourceFile, classes);

		this.execute(listener);

		listener.exportDefinedTypes();
	}

	public CompilationResult compile(Classes classes)
	{
		YirgacheffeListener listener =
			new BinaryOperationListener(this.sourceFile, classes);

		Array<Error> errors = this.execute(listener);

		if (errors.length() > 0)
		{
			return new CompilationResult(this.sourceFile, errors);
		}
		else
		{
			return listener.getCompilationResult();
		}
	}

	private Array<Error> execute(YirgacheffeListener listener)
	{
		ParseTreeWalker walker = new ParseTreeWalker();

		walker.walk(listener, this.tree);

		return this.errors;
	}
}
