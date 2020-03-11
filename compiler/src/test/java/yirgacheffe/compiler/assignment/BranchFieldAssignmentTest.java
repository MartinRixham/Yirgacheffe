package yirgacheffe.compiler.assignment;

import org.junit.Test;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BranchFieldAssignmentTest
{
	@Test
	public void testBranchFieldAssignment()
	{
		FieldAssignment branchFieldAssignment =
			new BranchedFieldAssignment(
				new Array<>("thingy"),
				new TotalFieldAssignment());

		FieldAssignment fieldAssignment =
			new BlockFieldAssignment(new Array<>("sumpt"));

		assertTrue(branchFieldAssignment.combineWith(new Array<>()).contains("thingy"));

		assertTrue(
			branchFieldAssignment.combineWith(fieldAssignment).contains("thingy"));

		assertFalse(
			branchFieldAssignment.combineWith(
				new Array<>(), fieldAssignment).contains("thingy"));

		assertTrue(
			branchFieldAssignment.intersect(new Array<>("thingy")).contains("thingy"));

		assertFalse(
			branchFieldAssignment.intersect(new Array<>("sumpt")).contains("sumpt"));

		assertFalse(
			branchFieldAssignment.intersect(fieldAssignment).contains("sumpt"));

		assertFalse(
			branchFieldAssignment.intersect(
				new Array<>("sumpt"), fieldAssignment).contains("sumpt"));
	}
}
