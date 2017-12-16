package yirgacheffe.lang;

import java.util.Arrays;

public class Array<T>
{
	private static final int LOG_THIRTY_TWO = 5;

	private int length = 0;

	private Object[] array;

	public Array()
	{
		this.array = new Object[1 << LOG_THIRTY_TWO];
	}

	public Array(T... items)
	{
		int power = LOG_THIRTY_TWO;
		int length = items.length >> LOG_THIRTY_TWO;

		while (length > 0)
		{
			power++;
			length = length >> 1;
		}

		this.array = Arrays.copyOf(items, 1 << power);

		this.length = items.length;
	}

	public int length()
	{
		return this.length;
	}

	public String toString()
	{
		return Arrays.toString(Arrays.copyOf(this.array, this.length));
	}

	public void push(T... items)
	{
		this.grow(this.length + items.length);

		java.lang.System.arraycopy(items, 0, this.array, this.length, items.length);

		this.length = this.length + items.length;
	}

	public T pop()
	{
		if (this.length == 0)
		{
			throw new ArrayIndexOutOfBoundsException(Integer.toString(-1));
		}

		int lastIndex = --this.length;
		T poppedItem = (T) this.array[lastIndex];
		this.array[lastIndex] = null;

		return poppedItem;
	}

	public void unshift(T... items)
	{
		this.grow(this.length + items.length);

		java.lang.System.arraycopy(this.array, 0, this.array, items.length, this.length);
		java.lang.System.arraycopy(items, 0, this.array, 0, items.length);

		this.length = this.length + items.length;
	}

	public T shift()
	{
		if (this.length == 0)
		{
			throw new ArrayIndexOutOfBoundsException(Integer.toString(0));
		}

		T shiftedItem = (T) this.array[0];

		java.lang.System.arraycopy(this.array, 1, this.array, 0, this.length - 1);

		this.length--;
		this.array[this.length] = null;

		return shiftedItem;
	}

	public void sort()
	{
		Arrays.sort(this.array, 0, this.length);
	}

	public void reverse()
	{
		Object[] newArray = new Object[this.array.length];

		for (int i = 0; i < this.length; i++)
		{
			newArray[i] = this.array[this.length - i - 1];
		}

		this.array = newArray;
	}

	public Array<T> splice(int startIndex)
	{
		if (startIndex < 0)
		{
			return this.splice(startIndex, -startIndex);
		}
		else
		{
			return this.splice(startIndex, this.length - startIndex);
		}
	}

	public Array<T> splice(int startIndex, int deleteCount, T... items)
	{
		if (startIndex < 0)
		{
			startIndex = this.length + startIndex;
		}

		if (startIndex > this.length || startIndex < 0)
		{
			throw new ArrayIndexOutOfBoundsException(Integer.toString(startIndex));
		}

		if (startIndex + deleteCount > this.length)
		{
			throw new ArrayIndexOutOfBoundsException(Integer.toString(this.length));
		}

		this.grow(this.length + items.length - deleteCount);

		Object[] deleted = new Object[deleteCount];

		java.lang.System.arraycopy(this.array, startIndex, deleted, 0, deleteCount);

		java.lang.System.arraycopy(
			this.array,
			startIndex + deleteCount,
			this.array,
			startIndex + items.length,
			this.length - (startIndex + deleteCount));

		java.lang.System.arraycopy(items, 0, this.array, startIndex, items.length);

		for (int i = items.length; i < deleteCount; i++)
		{
			this.array[this.length - deleteCount + i] = null;
		}

		this.length = this.length + items.length - deleteCount;

		return new Array<T>((T[]) deleted);
	}

	public Array<T> slice(int startIndex)
	{
		return this.slice(startIndex, this.length);
	}

	public Array<T> slice(int startIndex, int endIndex)
	{
		if (startIndex < 0)
		{
			startIndex = this.length + startIndex;
		}

		if (endIndex < 0)
		{
			endIndex = this.length + endIndex;
		}

		if (startIndex >= this.length || startIndex < 0)
		{
			throw new ArrayIndexOutOfBoundsException(Integer.toString(startIndex));
		}

		if (endIndex > this.length)
		{
			throw new ArrayIndexOutOfBoundsException(Integer.toString(this.length));
		}

		if (endIndex <= startIndex)
		{
			return new Array<T>();
		}

		Object[] newArray = new Object[endIndex - startIndex];

		java.lang.System.arraycopy(
			this.array, startIndex, newArray, 0, endIndex - startIndex);

		return new Array<T>((T[]) newArray);
	}

	public Array<T> concat(Array<T> other)
	{
		Object[] newArray = new Object[this.length + other.length];

		java.lang.System.arraycopy(this.array, 0, newArray, 0, this.length);
		java.lang.System.arraycopy(other.array, 0, newArray, this.length, other.length);

		return new Array<>((T[]) newArray);
	}

	public int indexOf(T item)
	{
		return Arrays.asList(this.array).indexOf(item);
	}

	private void grow(int minimum)
	{
		int length = this.array.length;

		while (length <= minimum)
		{
			length = length << 1;
		}

		if (length > this.array.length)
		{
			this.array = Arrays.copyOf(this.array, length);
		}
	}
}
