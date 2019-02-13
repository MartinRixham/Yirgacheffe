package yirgacheffe.compiler.error;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ErrorTest
{
	@Test
	public void testError()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		Error error = new Error(coordinate, "message");

		assertEquals("line 3:5 message", error.toString());

		Error otherError = new Error(coordinate, "");

		assertEquals(0, error.compareTo(otherError));
	}
}
