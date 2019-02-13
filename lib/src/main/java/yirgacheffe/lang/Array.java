package yirgacheffe.lang;

import java.util.Arrays;
import java.util.Iterator;

public class Array<T> implements Iterable<T>
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

	public String join(String separator)
	{
		String[] strings = new String[this.length];

		for (int i = 0; i < this.length; i++)
		{
			strings[i] = this.array[i].toString();
		}

		return String.join(separator, strings);
	}

	public T get(double index)
	{
		int intIndex = (int) index;

		if (index < 0 || index >= this.length)
		{
			throw new ArrayIndexOutOfBoundsException(Integer.toString(intIndex));
		}

		return (T) this.array[intIndex];
	}

	public void set(int i, T item)
	{
		if (i >= this.length)
		{
			this.grow(i + 1);

			this.length = i + 1;
		}

		this.array[i] = item;
	}

	public void push(Array<T> items)
	{
		this.grow(this.length + items.length);

		java.lang.System.arraycopy(
			items.array, 0, this.array, this.length, items.length);

		this.length = this.length + items.length;
	}

	public void push(T item)
	{
		this.grow(this.length + 1);

		this.array[this.length] = item;

		this.length = this.length + 1;
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

	public Array<T> splice(double startIndex)
	{
		int intStartIndex = (int) startIndex;

		if (startIndex < 0)
		{
			return this.splice(intStartIndex, -intStartIndex);
		}
		else
		{
			return this.splice(intStartIndex, this.length - intStartIndex);
		}
	}

	public Array<T> splice(double startIndex, double deleteCount, T... items)
	{
		int intStartIndex = (int) startIndex;
		int intDeleteCount = (int) deleteCount;

		if (intStartIndex < 0)
		{
			intStartIndex = this.length + intStartIndex;
		}

		if (intStartIndex > this.length || intStartIndex < 0)
		{
			throw new ArrayIndexOutOfBoundsException(Integer.toString(intStartIndex));
		}

		if (intStartIndex + intDeleteCount > this.length)
		{
			throw new ArrayIndexOutOfBoundsException(Integer.toString(this.length));
		}

		this.grow(this.length + items.length - intDeleteCount);

		Object[] deleted = new Object[intDeleteCount];

		java.lang.System.arraycopy(this.array, intStartIndex, deleted, 0, intDeleteCount);

		java.lang.System.arraycopy(
			this.array,
			intStartIndex + intDeleteCount,
			this.array,
			intStartIndex + items.length,
			this.length - (intStartIndex + intDeleteCount));

		java.lang.System.arraycopy(items, 0, this.array, intStartIndex, items.length);

		for (int i = items.length; i < deleteCount; i++)
		{
			this.array[this.length - intDeleteCount + i] = null;
		}

		this.length = this.length + items.length - intDeleteCount;

		return new Array<T>((T[]) deleted);
	}

	public Array<T> slice(double startIndex)
	{
		int intStartIndex = (int) startIndex;

		return this.slice(intStartIndex, this.length);
	}

	public Array<T> slice(double startIndex, double endIndex)
	{
		int intStartIndex = (int) startIndex;
		int intEndIndex = (int) endIndex;

		if (intStartIndex < 0)
		{
			intStartIndex = this.length + intStartIndex;
		}

		if (intEndIndex < 0)
		{
			intEndIndex = this.length + intEndIndex;
		}

		if (intStartIndex >= this.length || intStartIndex < 0)
		{
			throw new ArrayIndexOutOfBoundsException(Integer.toString(intStartIndex));
		}

		if (intEndIndex > this.length)
		{
			throw new ArrayIndexOutOfBoundsException(Integer.toString(this.length));
		}

		if (intEndIndex <= intStartIndex)
		{
			return new Array<T>();
		}

		Object[] newArray = new Object[intEndIndex - intStartIndex];

		java.lang.System.arraycopy(
			this.array, intStartIndex, newArray, 0, intEndIndex - intStartIndex);

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

	public boolean contains(Object item)
	{
		return Arrays.asList(this.array).contains(item);
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

	@Override
	public Iterator<T> iterator()
	{
		final Object[] array = Arrays.copyOf(this.array, this.length);

		return new Iterator<T>()
		{
			private int index = 0;

			@Override
			public boolean hasNext()
			{
				return this.index < array.length;
			}

			@Override
			public T next()
			{
				return (T) array[this.index++];
			}
		};
	}

	public T[] toArray()
	{
		return (T[]) Arrays.copyOf(this.array, this.length);
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Array)
		{
			Array array = (Array) other;

			if (this.length != array.length())
			{
				return false;
			}

			for (int i = 0; i < this.length; i++)
			{
				if (!this.array[i].equals(array.get(i)))
				{
					return false;
				}
			}

			return true;
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		int hashCode = 0;

		for (Object object: this.array)
		{
			if (object != null)
			{
				hashCode += object.hashCode();
			}
		}

		return hashCode;
	}
}
