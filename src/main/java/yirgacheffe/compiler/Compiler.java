package yirgacheffe.compiler;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.objectweb.asm.ClassWriter;
import yirgacheffe.parser.YirgacheffeLexer;
import yirgacheffe.parser.YirgacheffeParser;

import java.io.InputStream;

public class Compiler
{
	private String directory;

	private InputStream source;

	public Compiler(String directory, InputStream source)
	{
		this.directory = directory;
		this.source = source;
	}

	public CompilationResult compile() throws Exception
	{
		ClassWriter writer = new ClassWriter(0);
		ParseErrorListener errorListener = new ParseErrorListener();
		YirgacheffeListener listener =
			new MethodListener(this.directory, errorListener, writer);
		CharStream input = new ANTLRInputStream(this.source);
		YirgacheffeLexer lexer = new YirgacheffeLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		YirgacheffeParser parser = new YirgacheffeParser(tokens);
		parser.removeErrorListeners();
		parser.addErrorListener(errorListener);

		ParseTree tree = parser.compilationUnit();

		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(listener, tree);

		return listener.getCompilationResult();
	}
}
