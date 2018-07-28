package yirgacheffe.compiler.error;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class Error implements Comparable<Error>
{
	private Coordinate coordinate;

	private String message;

	public Error(Coordinate coordinate, ErrorMessage message)
	{
		this.coordinate = coordinate;
		this.message = message.toString();
	}

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

	@Override
	public String toString()
	{
		return this.coordinate + " " + this.message;
	}

	@Override
	public int compareTo(Error other)
	{
		return this.coordinate.compareTo(other.coordinate);
	}
}
