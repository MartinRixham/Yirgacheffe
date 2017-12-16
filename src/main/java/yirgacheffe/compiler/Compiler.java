package yirgacheffe.compiler;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import yirgacheffe.compiler.listener.FunctionCallListener;
import yirgacheffe.compiler.error.ParseErrorListener;
import yirgacheffe.compiler.listener.ClassListener;
import yirgacheffe.compiler.listener.MethodListener;
import yirgacheffe.compiler.listener.YirgacheffeListener;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.parser.YirgacheffeParser;

public class Compiler
{
	private String sourceFile;

	private String source;

	public Compiler(String sourceFile, String source)
	{
		this.sourceFile = sourceFile;
		this.source = source;
	}

	public void compileClassDeclaration(
		Classes classes)
		throws Exception
	{
		YirgacheffeListener listener =
			new ClassListener(
				this.sourceFile,
				classes);

		this.execute(listener);

		listener.exportDefinedTypes();
	}

	public void compileInterface(
		Classes classes)
		throws Exception
	{
		YirgacheffeListener listener =
			new MethodListener(
				this.sourceFile,
				classes);

		this.execute(listener);

		listener.exportDefinedTypes();
	}

	public CompilationResult compile(
		Classes classes)
		throws Exception
	{
		YirgacheffeListener listener =
			new FunctionCallListener(
				this.sourceFile,
				classes);

		ParseErrorListener errors = this.execute(listener);

		return listener.getCompilationResult(errors);
	}

	private ParseErrorListener execute(
		YirgacheffeListener listener)
		throws Exception
	{
		ParseErrorListener errorListener = new ParseErrorListener();
		YirgacheffeParser parser = new Source(this.source).parse();
		parser.removeErrorListeners();
		parser.addErrorListener(errorListener);

		ParseTree tree = parser.compilationUnit();

		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(listener, tree);

		return errorListener;
	}
}
