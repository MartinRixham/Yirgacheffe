package yirgacheffe.compiler.main;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.objectweb.asm.ClassWriter;
import yirgacheffe.compiler.Type.BytecodeClassLoader;
import yirgacheffe.compiler.Type.DeclaredType;
import yirgacheffe.compiler.Type.Types;
import yirgacheffe.compiler.error.ParseErrorListener;
import yirgacheffe.compiler.listener.ClassListener;
import yirgacheffe.compiler.listener.StatementListener;
import yirgacheffe.compiler.listener.YirgacheffeListener;
import yirgacheffe.parser.YirgacheffeLexer;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.Map;

public class Compiler
{
	private String directory;

	private String source;

	public Compiler(String directory, String source)
	{
		this.directory = directory;
		this.source = source;
	}

	public CompilationResult compileClassDeclaration(
		Map<String, DeclaredType> declaredTypes,
		BytecodeClassLoader classLoader)
		throws Exception
	{
		ClassWriter writer = new ClassWriter(0);
		ParseErrorListener errorListener = new ParseErrorListener();

		YirgacheffeListener listener =
			new ClassListener(
				this.directory,
				new Types(declaredTypes),
				classLoader,
				errorListener,
				writer);

		return this.execute(listener, errorListener);
	}

	public CompilationResult compile(
		Map<String, DeclaredType> declaredTypes,
		BytecodeClassLoader classLoader)
		throws Exception
	{
		ClassWriter writer = new ClassWriter(0);
		ParseErrorListener errorListener = new ParseErrorListener();

		YirgacheffeListener listener =
			new StatementListener(
				this.directory,
				new Types(declaredTypes),
				classLoader,
				errorListener,
				writer);

		return this.execute(listener, errorListener);
	}

	private CompilationResult execute(
		YirgacheffeListener listener,
		ParseErrorListener errorListener)
		throws Exception
	{
		CharStream charStream = CharStreams.fromString(this.source);
		YirgacheffeLexer lexer = new YirgacheffeLexer(charStream);
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
