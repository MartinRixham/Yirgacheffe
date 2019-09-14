package yirgacheffe.compiler.error;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import yirgacheffe.lang.Combinable;

public class Error implements Comparable<Error>, Combinable<StringBuilder>
{
	private Coordinate coordinate;

	private String message;

	public Error(Coordinate coordinate, String message)
	{
		this.coordinate = coordinate;
		this.message = message;
	}

	public Error(Token token, String message)
	{
		this.coordinate = new Coordinate(token);
		this.message = message;
	}

	public Error(ParserRuleContext context, String message)
	{
		this.coordinate = new Coordinate(context);
		this.message = message;
	}

	public int compareTo(Error other)
	{
		return this.coordinate.compareTo(other.coordinate);
	}

	public StringBuilder combineWith(StringBuilder builder)
	{
		return builder
			.append(this.coordinate)
			.append(" ")
			.append(this.message)
			.append("\n");
	}

	@Override
	public String toString()
	{
		return this.coordinate + " " + this.message;
	}
}
