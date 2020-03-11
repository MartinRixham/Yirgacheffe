package yirgacheffe.compiler.assignment;

import org.junit.Test;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertTrue;

public class TotalFieldAssignmentTest
{
	@Test
	public void testTotalFieldAssignment()
	{
		FieldAssignment totalFieldAssignment = new TotalFieldAssignment();

		FieldAssignment blockFieldAssignment =
			new BlockFieldAssignment(new Array<>("sumpt"));

		assertTrue(totalFieldAssignment.combineWith(new Array<>()).contains("thingy"));

		assertTrue(
			totalFieldAssignment.combineWith(blockFieldAssignment).contains("thingy"));

		assertTrue(
			totalFieldAssignment.combineWith(
				new Array<>(), blockFieldAssignment).contains("thingy"));

		assertTrue(
			totalFieldAssignment.intersect(new Array<>("sumpt")).contains("sumpt"));

		assertTrue(
			totalFieldAssignment.intersect(blockFieldAssignment).contains("sumpt"));

		assertTrue(
			totalFieldAssignment.intersect(
				new Array<>("sumpt"), blockFieldAssignment).contains("sumpt"));
	}
}
