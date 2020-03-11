package yirgacheffe.compiler.assignment;

import org.junit.Test;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BlockFieldAssignmentTest
{
	@Test
	public void testBlockFieldAssignment()
	{
		FieldAssignment blockFieldAssignment =
			new BlockFieldAssignment(new Array<>("thingy"));

		FieldAssignment fieldAssignment =
			new BlockFieldAssignment(new Array<>("sumpt"));

		assertTrue(blockFieldAssignment.combineWith(new Array<>()).contains("thingy"));

		assertTrue(
			blockFieldAssignment.combineWith(fieldAssignment).contains("thingy"));

		assertFalse(
			blockFieldAssignment.combineWith(
				new Array<>(), fieldAssignment).contains("thingy"));

		assertTrue(
			blockFieldAssignment.intersect(new Array<>("thingy")).contains("thingy"));

		assertFalse(
			blockFieldAssignment.intersect(new Array<>("sumpt")).contains("sumpt"));

		assertFalse(
			blockFieldAssignment.intersect(fieldAssignment).contains("sumpt"));

		assertFalse(
			blockFieldAssignment.intersect(
				new Array<>("sumpt"), fieldAssignment).contains("sumpt"));
	}
}
