package yirgacheffe.compiler.error;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CoordinateTest
{
	@Test
	public void testCoordinate()
	{
		Coordinate coordinate = new Coordinate(3, 5);

		assertEquals("line 3:5", coordinate.toString());
		assertTrue(coordinate.compareTo(new Coordinate(2, 4)) > 0);
		assertTrue(coordinate.compareTo(new Coordinate(5, 4)) < 0);
	}
}
