package yirgacheffe.compiler.error;

import org.objectweb.asm.Label;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import yirgacheffe.compiler.Result;

public class Coordinate implements Comparable<Coordinate>
{
	private int line;

	private int charPosition;

	public Coordinate(int line, int charPosition)
	{
		this.line = line;
		this.charPosition = charPosition;
	}

	public Coordinate(Token token)
	{
		this.line = token.getLine();
		this.charPosition = token.getCharPositionInLine();
	}

	public Coordinate(ParserRuleContext context)
	{
		this(context.getStart());
	}

	public Result compile()
	{
		Label label = new Label();

		return new Result()
			.add(new LabelNode(label))
			.add(new LineNumberNode(this.line, new LabelNode(label)));
	}

	@Override
	public String toString()
	{
		return "line " + this.line + ":" + this.charPosition;
	}

	@Override
	public int compareTo(Coordinate other)
	{
		int comparison = Integer.compare(this.line, other.line);

		if (comparison == 0)
		{
			return Integer.compare(this.charPosition, other.charPosition);
		}
		else
		{
			return comparison;
		}
	}
}
