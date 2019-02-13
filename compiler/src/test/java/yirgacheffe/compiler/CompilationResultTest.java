package yirgacheffe.compiler;

import org.junit.Test;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;

public class CompilationResultTest
{
	@Test
	public void testCompilationResult()
	{
		Error firstError = new Error(new Coordinate(3, 4), "error");
		Error secondError = new Error(new Coordinate(5, 4), "error");
		Array<Error> errors = new Array<>(secondError, firstError);
		CompilationResult compilationResult = new CompilationResult("", errors);

		assertEquals(
			"line 3:4 error\nline 5:4 error\n", compilationResult.getErrors());
	}
}
