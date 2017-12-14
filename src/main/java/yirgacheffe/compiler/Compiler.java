package yirgacheffe.compiler;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.objectweb.asm.ClassWriter;
import yirgacheffe.compiler.listener.ExpressionListener;
import yirgacheffe.compiler.type.BytecodeClassLoader;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.error.ParseErrorListener;
import yirgacheffe.compiler.listener.ClassListener;
import yirgacheffe.compiler.listener.MethodListener;
import yirgacheffe.compiler.listener.YirgacheffeListener;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.Map;

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
		Map<String, Type> declaredTypes,
		BytecodeClassLoader classLoader)
		throws Exception
	{
		ClassWriter writer = new ClassWriter(0);
		ParseErrorListener errorListener = new ParseErrorListener();

		YirgacheffeListener listener =
			new ClassListener(
				this.sourceFile,
				declaredTypes,
				classLoader,
				errorListener,
				writer);

		this.execute(listener, errorListener);
	}

	public void compileInterface(
		Map<String, Type> declaredTypes,
		BytecodeClassLoader classLoader)
		throws Exception
	{
		ClassWriter writer = new ClassWriter(0);
		ParseErrorListener errorListener = new ParseErrorListener();

		YirgacheffeListener listener =
			new MethodListener(
				this.sourceFile,
				declaredTypes,
				classLoader,
				errorListener,
				writer);

		this.execute(listener, errorListener);
	}

	public CompilationResult compile(
		Map<String, Type> declaredTypes,
		BytecodeClassLoader classLoader)
		throws Exception
	{
		ClassWriter writer = new ClassWriter(0);
		ParseErrorListener errorListener = new ParseErrorListener();

		YirgacheffeListener listener =
			new ExpressionListener(
				this.sourceFile,
				declaredTypes,
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
		YirgacheffeParser parser = new Source(this.source).parse();
		parser.removeErrorListeners();
		parser.addErrorListener(errorListener);

		ParseTree tree = parser.compilationUnit();

		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(listener, tree);

		return listener.getCompilationResult();
	}
}
