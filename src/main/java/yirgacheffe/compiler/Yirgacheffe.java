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
import java.util.ArrayList;
import java.util.List;

public final class Yirgacheffe
{
	private String[] sourceTokens;

	private String source;

	private static final int LENGTH_OF_NON_EMPTY_BLOCK = 3;

	public Yirgacheffe(String source)
	{
		this.sourceTokens = source.split("\\s+");
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
		parser.removeErrorListeners();

		ParseTree tree = parser.compilationUnit();

		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(listener, tree);

		if (this.sourceTokens.length > LENGTH_OF_NON_EMPTY_BLOCK)
		{
			new Block(this.getBlockSource()).compile(writer);
		}

		return listener.getCompilationResult();
	}

	private String getBlockSource()
	{
		List<String> source = new ArrayList<>();
		boolean flag = false;

		for (String token: this.sourceTokens)
		{
			if (token.equals("}"))
			{
				flag = false;
			}

			if (flag)
			{
				source.add(token);
			}

			if (token.equals("{"))
			{
				flag = true;
			}
		}

		return String.join(" ", source);
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
			System.err.println(result.getErrors());
		}
	}
}
