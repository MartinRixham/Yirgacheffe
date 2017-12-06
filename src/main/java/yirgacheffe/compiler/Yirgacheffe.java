package yirgacheffe.compiler;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.objectweb.asm.ClassWriter;
import yirgacheffe.parser.YirgacheffeLexer;
import yirgacheffe.parser.YirgacheffeParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public final class Yirgacheffe
{
	private String source;

	public Yirgacheffe(String source)
	{
		this.source = source;
	}

	public CompilationResult compile() throws Exception
	{
		ClassWriter writer = new ClassWriter(0);
		YirgacheffeListener listener = new YirgacheffeListener(writer);
		InputStream stream = new ByteArrayInputStream(this.source.getBytes());
		CharStream input = new ANTLRInputStream(stream);
		YirgacheffeLexer lexer = new YirgacheffeLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		YirgacheffeParser parser = new YirgacheffeParser(tokens);

		ParseTree tree = parser.compilationUnit();

		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(listener, tree);

		return listener.getCompilationResult();
	}

	public static void main(String[] args) throws Exception
	{
		CompilationResult result = new Yirgacheffe(args[0]).compile();

		if (result.isSuccessful())
		{
			System.out.write(result.getBytecode());
		}
		else
		{
			System.err.print(result.getErrors());
		}
	}
}
