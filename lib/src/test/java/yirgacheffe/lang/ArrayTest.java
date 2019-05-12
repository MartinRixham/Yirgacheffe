package yirgacheffe.lang;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ArrayTest
{
	@Test
	public void emptyArray()
	{
		Array<Integer> numbers = new Array<>();

		assertEquals("[]", numbers.toString());
		assertEquals(0, numbers.length());
	}

	@Test
	public void arrayWithOneItem()
	{
		Array<Integer> numbers = new Array<>(1);

		assertEquals("[1]", numbers.toString());
		assertEquals(1, numbers.length());
	}

	@Test
	public void arrayWithTwoItems()
	{
		Array<Integer> numbers = new Array<>(1, 2);

		assertEquals("[1, 2]", numbers.toString());
		assertEquals(2, numbers.length());
	}

	@Test
	public void arrayWithThirtyThreeItems()
	{
		Array<Integer> numbers =
			new Array<>(
				1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
				11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
				21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
				31, 32, 33);

		assertEquals(33, numbers.length());
	}

	@Test
	public void testGetFromEmptyArray()
	{
		Array<Integer> numbers = new Array<>();

		Exception exception = null;

		try
		{
			numbers.get(0);
		}
		catch (IndexOutOfBoundsException e)
		{
			exception = e;
		}

		assertNotNull(exception);
		assertEquals("0", exception.getMessage());
		assertEquals(0, numbers.length());
	}

	@Test
	public void testGetItemFromArrayLengthOne()
	{
		Array<Integer> numbers = new Array<>(1);

		int one = numbers.get(0);

		assertEquals(1, one);
		assertEquals(1, numbers.length());
	}

	@Test
	public void testGetFirstItemFromArrayLengthTwo()
	{
		Array<Integer> numbers = new Array<>(1, 2);

		int one = numbers.get(0);

		assertEquals(1, one);
		assertEquals(2, numbers.length());
	}

	@Test
	public void testGetSecondItemFromArrayLengthTwo()
	{
		Array<Integer> numbers = new Array<>(1, 2);

		int two = numbers.get(1);

		assertEquals(2, two);
		assertEquals(2, numbers.length());
	}

	@Test
	public void testSetFirstItem()
	{
		Array<String> numbers = new Array<>();

		numbers.set(0, "zero");

		assertEquals(1, numbers.length());
		assertEquals("zero", numbers.get(0));
	}

	@Test
	public void testSetSecondItem()
	{
		Array<String> numbers = new Array<>();

		numbers.set(1, "one");

		assertEquals(2, numbers.length());
		assertEquals("one", numbers.get(1));
	}

	@Test
	public void testSetThirtyThirdItem()
	{
		Array<String> numbers = new Array<>();

		numbers.set(32, "item");

		assertEquals(33, numbers.length());
		assertEquals("item", numbers.get(32));
	}

	@Test
	public void testReplaceFirstItem()
	{
		Array<String> numbers = new Array<>("zero", "one");

		numbers.set(0, "replaced");

		assertEquals(2, numbers.length());
		assertEquals("replaced", numbers.get(0));
	}

	@Test
	public void testPushOneItemToEmptyArray()
	{
		Array<Integer> numbers = new Array<>();

		numbers.push(1);

		assertEquals("[1]", numbers.toString());
		assertEquals(1, numbers.length());
	}

	@Test
	public void testPushTwoItemsToEmptyArray()
	{
		Array<Integer> numbers = new Array<>();

		numbers.push(new Array<>(1, 2));

		assertEquals("[1, 2]", numbers.toString());
		assertEquals(2, numbers.length());
	}

	@Test
	public void testPushSecondItemToArray()
	{
		Array<Integer> numbers = new Array<>(1);

		numbers.push(2);

		assertEquals("[1, 2]", numbers.toString());
		assertEquals(2, numbers.length());
	}

	@Test
	public void testPushThirtyThirdItemToArray()
	{
		Array<Integer> numbers =
			new Array<>(
				1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
				11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
				21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
				31);

		numbers.push(new Array<>(32, 33));

		assertEquals(33, numbers.length());
	}

	@Test
	public void testPopFromEmptyArray()
	{
		Array<Integer> numbers = new Array<>();

		Exception exception = null;

		try
		{
			numbers.pop();
		}
		catch (IndexOutOfBoundsException e)
		{
			exception = e;
		}

		assertNotNull(exception);
		assertEquals("-1", exception.getMessage());
		assertEquals(0, numbers.length());
	}

	@Test
	public void testPopItemFromArrayLengthOne()
	{
		Array<Integer> numbers = new Array<>(1);

		int one = numbers.pop();

		assertEquals(1, one);
		assertEquals(0, numbers.length());
	}

	@Test
	public void testPopItemFromArrayLengthTwo()
	{
		Array<Integer> numbers = new Array<>(1, 2);

		int two = numbers.pop();

		assertEquals(2, two);
		assertEquals(1, numbers.length());
	}

	@Test
	public void testPopTwoItemsFromArrayLengthTwo()
	{
		Array<Integer> numbers = new Array<>(1, 2);

		int two = numbers.pop();
		int one = numbers.pop();

		assertEquals(1, one);
		assertEquals(2, two);
		assertEquals(0, numbers.length());
	}

	@Test
	public void testUnshiftOneItemToEmptyArray()
	{
		Array<Integer> numbers = new Array<>();

		numbers.unshift(1);

		assertEquals("[1]", numbers.toString());
		assertEquals(1, numbers.length());
	}

	@Test
	public void testUnshiftTwoItemsToEmptyArray()
	{
		Array<Integer> numbers = new Array<>();

		numbers.unshift(1, 2);

		assertEquals("[1, 2]", numbers.toString());
		assertEquals(2, numbers.length());
	}

	@Test
	public void testUnshiftSecondItemToArray()
	{
		Array<Integer> numbers = new Array<>(1);

		numbers.unshift(2);

		assertEquals("[2, 1]", numbers.toString());
		assertEquals(2, numbers.length());
	}

	@Test
	public void testUnshiftThirdAndFourthItemsToArray()
	{
		Array<Integer> numbers = new Array<>(1, 2);

		numbers.unshift(3, 4);

		assertEquals("[3, 4, 1, 2]", numbers.toString());
		assertEquals(4, numbers.length());
	}

	@Test
	public void testUnshiftThirtyThirdItemToArray()
	{
		Array<Integer> numbers =
			new Array<>(
				1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
				11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
				21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
				31);

		numbers.unshift(32, 33);

		assertEquals(33, numbers.length());
	}

	@Test
	public void testShiftFromEmptyArray()
	{
		Array<Integer> numbers = new Array<>();

		Exception exception = null;

		try
		{
			numbers.shift();
		}
		catch (IndexOutOfBoundsException e)
		{
			exception = e;
		}

		assertNotNull(exception);
		assertEquals("0", exception.getMessage());
		assertEquals(0, numbers.length());
	}

	@Test
	public void testShiftItemFromArrayLengthOne()
	{
		Array<Integer> numbers = new Array<>(1);

		int one = numbers.shift();

		assertEquals(1, one);
		assertEquals(0, numbers.length());
	}

	@Test
	public void testShiftItemFromArrayLengthTwo()
	{
		Array<Integer> numbers = new Array<>(1, 2);

		int one = numbers.shift();

		assertEquals(1, one);
		assertEquals(1, numbers.length());
	}

	@Test
	public void testShiftTwoItemsFromArrayLengthTwo()
	{
		Array<Integer> numbers = new Array<>(1, 2);

		int one = numbers.shift();
		int two = numbers.shift();

		assertEquals(1, one);
		assertEquals(2, two);
		assertEquals(0, numbers.length());
	}

	@Test
	public void testSortEmptyArray()
	{
		Array<Integer> numbers = new Array<>();

		numbers.sort();

		assertEquals("[]", numbers.toString());
	}

	@Test
	public void testSortArrayLengthOne()
	{
		Array<Integer> numbers = new Array<>(1);

		numbers.sort();

		assertEquals("[1]", numbers.toString());
	}

	@Test
	public void testSortArrayLengthTwo()
	{
		Array<Integer> numbers = new Array<>(2, 1);

		numbers.sort();

		assertEquals("[1, 2]", numbers.toString());
	}

	@Test
	public void testSortArrayLengthThree()
	{
		Array<Integer> numbers = new Array<>(2, 3, 1);

		numbers.sort();

		assertEquals("[1, 2, 3]", numbers.toString());
	}

	@Test
	public void testReverseEmptyArray()
	{
		Array<Integer> numbers = new Array<>();

		numbers.reverse();

		assertEquals("[]", numbers.toString());
	}

	@Test
	public void testReverseArrayLengthOne()
	{
		Array<Integer> numbers = new Array<>(1);

		numbers.reverse();

		assertEquals("[1]", numbers.toString());
	}

	@Test
	public void testReverseArrayLengthTwo()
	{
		Array<Integer> numbers = new Array<>(2, 1);

		numbers.reverse();

		assertEquals("[1, 2]", numbers.toString());
	}

	@Test
	public void testReverseArrayLengthThree()
	{
		Array<Integer> numbers = new Array<>(2, 3, 1);

		numbers.reverse();

		assertEquals("[1, 3, 2]", numbers.toString());
	}

	@Test
	public void testSpliceItemFromEmptyArray()
	{
		Array<Integer> numbers = new Array<>();

		Exception exception = null;

		try
		{
			numbers.splice(0, 1);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			exception = e;
		}

		assertNotNull(exception);
		assertEquals("0", exception.getMessage());
		assertEquals(0, numbers.length());
	}

	@Test
	public void testSpliceTooManyItemsFromArray()
	{
		Array<Integer> numbers = new Array<>(1, 2);

		Exception exception = null;

		try
		{
			numbers.splice(1, 2);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			exception = e;
		}

		assertNotNull(exception);
		assertEquals("2", exception.getMessage());
		assertEquals(2, numbers.length());
	}

	@Test
	public void testSpliceStartIndexTooBig()
	{
		Array<Integer> numbers = new Array<>(1, 2);

		Exception exception = null;

		try
		{
			numbers.splice(3, 0);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			exception = e;
		}

		assertNotNull(exception);
		assertEquals("3", exception.getMessage());
		assertEquals(2, numbers.length());
	}

	@Test
	public void testSpliceStartIndexTooSmall()
	{
		Array<Integer> numbers = new Array<>(1, 2);

		Exception exception = null;

		try
		{
			numbers.splice(-3, 0);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			exception = e;
		}

		assertNotNull(exception);
		assertEquals("-1", exception.getMessage());
		assertEquals(2, numbers.length());
	}

	@Test
	public void testSpliceItemFromArrayLengthOne()
	{
		Array<Integer> numbers = new Array<>(1);

		Array<Integer> deleted = numbers.splice(0, 1);

		assertEquals("[]", numbers.toString());
		assertEquals("[1]", deleted.toString());
	}

	@Test
	public void testSpliceItemFromEndOfArrayLengthTwo()
	{
		Array<Integer> numbers = new Array<>(1, 2);

		Array<Integer> deleted = numbers.splice(1, 1);

		assertEquals("[1]", numbers.toString());
		assertEquals("[2]", deleted.toString());
	}

	@Test
	public void testSpliceItemFromBeginningOfArrayLengthTwo()
	{
		Array<Integer> numbers = new Array<>(1, 2);

		Array<Integer> deleted = numbers.splice(0, 1);

		assertEquals("[2]", numbers.toString());
		assertEquals("[1]", deleted.toString());
	}

	@Test
	public void testSpliceItemFromBeginningOfArrayLengthThree()
	{
		Array<Integer> numbers = new Array<>(1, 2, 3);

		Array<Integer> deleted = numbers.splice(0, 1);

		assertEquals("[2, 3]", numbers.toString());
		assertEquals("[1]", deleted.toString());
	}

	@Test
	public void testSpliceTwoItemsFromArrayLengthTwo()
	{
		Array<Integer> numbers = new Array<>(1, 2);

		Array<Integer> deleted = numbers.splice(0, 2);

		assertEquals("[]", numbers.toString());
		assertEquals("[1, 2]", deleted.toString());
	}

	@Test
	public void testSpliceOneItemToEmptyArray()
	{
		Array<Integer> numbers = new Array<>();

		Array<Integer> deleted = numbers.splice(0, 0, 1);

		assertEquals("[1]", numbers.toString());
		assertEquals("[]", deleted.toString());
	}

	@Test
	public void testSpliceTwoItemsToEmptyArray()
	{
		Array<Integer> numbers = new Array<>();

		Array<Integer> deleted = numbers.splice(0, 0, 1, 2);

		assertEquals("[1, 2]", numbers.toString());
		assertEquals("[]", deleted.toString());
	}

	@Test
	public void testSpliceSecondItemToArray()
	{
		Array<Integer> numbers = new Array<>(1);

		Array<Integer> deleted = numbers.splice(1, 0, 2);

		assertEquals("[1, 2]", numbers.toString());
		assertEquals("[]", deleted.toString());
	}

	@Test
	public void testShitThirtyThirdItemToArray()
	{
		Array<Integer> numbers =
			new Array<>(
				1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
				11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
				21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
				31);

		numbers.splice(31, 0, 32, 33);

		assertEquals(33, numbers.length());
	}

	@Test
	public void testSpliceReplacementItemToMiddleOfArray()
	{
		Array<Integer> numbers = new Array<>(1, 2, 3, 4);

		Array<Integer> deleted = numbers.splice(1, 1, 5);

		assertEquals("[1, 5, 3, 4]", numbers.toString());
		assertEquals("[2]", deleted.toString());
	}

	@Test
	public void testSpliceTwoItemsToBeginningOfArray()
	{
		Array<Integer> numbers = new Array<>(1, 2, 3);

		Array<Integer> deleted = numbers.splice(0, 0, 4, 5);

		assertEquals("[4, 5, 1, 2, 3]", numbers.toString());
		assertEquals("[]", deleted.toString());
	}

	@Test
	public void testSpliceAllElementsAfterIndex()
	{
		Array<Integer> numbers = new Array<>(1, 2, 3);

		Array<Integer> deleted = numbers.splice(1);

		assertEquals("[1]", numbers.toString());
		assertEquals("[2, 3]", deleted.toString());
	}

	@Test
	public void testSpliceAllElementsAfterNegativeIndex()
	{
		Array<Integer> numbers = new Array<>(1, 2, 3);

		Array<Integer> deleted = numbers.splice(-2);

		assertEquals("[1]", numbers.toString());
		assertEquals("[2, 3]", deleted.toString());
	}

	@Test
	public void testConcatEmptyArrays()
	{
		Array<Integer> one = new Array<>();
		Array<Integer> two = new Array<>();
		Array<Integer> three = one.concat(two);

		assertEquals("[]", three.toString());
	}

	@Test
	public void testConcatOneItemWithEmptyArray()
	{
		Array<Integer> one = new Array<>(1);
		Array<Integer> two = new Array<>();
		Array<Integer> three = one.concat(two);

		assertEquals("[1]", three.toString());
	}

	@Test
	public void testConcatEmptyArrayWithOneItem()
	{
		Array<Integer> one = new Array<>();
		Array<Integer> two = new Array<>(1);
		Array<Integer> three = one.concat(two);

		assertEquals("[1]", three.toString());
	}

	@Test
	public void testConcatOneItemInEachArray()
	{
		Array<Integer> one = new Array<>(1);
		Array<Integer> two = new Array<>(2);
		Array<Integer> three = one.concat(two);

		assertEquals("[1, 2]", three.toString());
	}

	@Test
	public void testConcatTwoItemsInEachArray()
	{
		Array<Integer> one = new Array<>(1, 2);
		Array<Integer> two = new Array<>(3, 4);
		Array<Integer> three = one.concat(two);

		assertEquals("[1, 2, 3, 4]", three.toString());
	}

	@Test
	public void testIndexOfEmptyArray()
	{
		Array<Integer> numbers = new Array<>();

		assertEquals(-1, numbers.indexOf(1));
	}

	@Test
	public void testIndexOfMatchingItem()
	{
		Array<Integer> numbers = new Array<>(1);

		assertEquals(0, numbers.indexOf(1));
	}

	@Test
	public void testIndexOfMismatchedItem()
	{
		Array<Integer> numbers = new Array<>(1);

		assertEquals(-1, numbers.indexOf(2));
	}

	@Test
	public void testSliceEmptyArray()
	{
		Array<Integer> numbers = new Array<>();

		Exception exception = null;

		try
		{
			numbers.slice(0, 0);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			exception = e;
		}

		assertNotNull(exception);
		assertEquals("0", exception.getMessage());
		assertEquals(0, numbers.length());
	}

	@Test
	public void testSliceArrayEndIndexTooBig()
	{
		Array<Integer> numbers = new Array<>(1);

		Exception exception = null;

		try
		{
			numbers.slice(0, 2);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			exception = e;
		}

		assertNotNull(exception);
		assertEquals("1", exception.getMessage());
	}

	@Test
	public void testSliceArrayStartIndexTooBig()
	{
		Array<Integer> numbers = new Array<>(1);

		Exception exception = null;

		try
		{
			numbers.slice(2, 3);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			exception = e;
		}

		assertNotNull(exception);
		assertEquals("2", exception.getMessage());
	}

	@Test
	public void testSliceArrayWithSingleElement()
	{
		Array<Integer> numbers = new Array<>(1);

		Array<Integer> sliced = numbers.slice(0, 1);

		assertEquals("[1]", sliced.toString());
	}

	@Test
	public void testSliceArrayStartIndexEqualsEndIndex()
	{
		Array<Integer> numbers = new Array<>(1);

		Array<Integer> sliced = numbers.slice(0, 0);

		assertEquals("[]", sliced.toString());
	}

	@Test
	public void testSliceBothElementsFromArrayLengthTwo()
	{
		Array<Integer> numbers = new Array<>(1, 2);

		Array<Integer> sliced = numbers.slice(0, 2);

		assertEquals("[1, 2]", sliced.toString());
	}

	@Test
	public void testSliceSecondElementFromArrayLengthTwo()
	{
		Array<Integer> numbers = new Array<>(1, 2);

		Array<Integer> sliced = numbers.slice(1, 2);

		assertEquals("[2]", sliced.toString());
	}

	@Test
	public void testSliceNegativeStartIndex()
	{
		Array<Integer> numbers = new Array<>(1, 2);

		Array<Integer> sliced = numbers.slice(-1, 2);

		assertEquals("[2]", sliced.toString());
	}

	@Test
	public void testSliceNegativeEndIndex()
	{
		Array<Integer> numbers = new Array<>(1, 2);

		Array<Integer> sliced = numbers.slice(0, -1);

		assertEquals("[1]", sliced.toString());
	}

	@Test
	public void testSliceEndIndexLessThanStartIndex()
	{
		Array<Integer> numbers = new Array<>(1, 2);

		Array<Integer> sliced = numbers.slice(1, 0);

		assertEquals("[]", sliced.toString());
	}

	@Test
	public void testSliceToEndOfArray()
	{
		Array<Integer> numbers = new Array<>(1, 2);

		Array<Integer> sliced = numbers.slice(1);

		assertEquals("[2]", sliced.toString());
	}

	@Test
	public void testSliceStartIndexTooSmall()
	{
		Array<Integer> numbers = new Array<>(1);

		Exception exception = null;

		try
		{
			numbers.slice(-12);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			exception = e;
		}

		assertNotNull(exception);
		assertEquals("-11", exception.getMessage());
	}

	@Test
	public void arrayToArray()
	{
		Array<Integer> numbers = new Array<>(1, 2);

		Object[] array = numbers.toArray();

		assertEquals(2, array.length);
	}
}
