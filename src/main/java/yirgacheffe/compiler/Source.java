package yirgacheffe.compiler;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import yirgacheffe.parser.YirgacheffeLexer;
import yirgacheffe.parser.YirgacheffeParser;

public class Source
{
	private String source;

	public Source(String source)
	{
		this.source = source;
	}

	public YirgacheffeParser parse()
	{
		CharStream charStream = CharStreams.fromString(this.source);
		YirgacheffeLexer lexer = new YirgacheffeLexer(charStream);
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		return new YirgacheffeParser(tokens);
	}
}
