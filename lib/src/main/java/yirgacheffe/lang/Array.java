package yirgacheffe.lang;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class Array<T> implements Iterable<T>
{
	private static final int LOG_THIRTY_TWO = 5;

	private int length = 0;

	private Object[] array;

	public static <T> Array<T> fromArray(T[] array)
	{
		return new Array<>(array);
	}

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

	public Array(Array<T> array)
	{
		this.array = Arrays.copyOf(array.array, array.array.length);
		this.length = array.length;
	}

	public Array(Collection<T> collection)
	{
		this.array = collection.toArray();
		this.length = collection.size();
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

	public T get(int index)
	{
		if (index < 0 || index >= this.length)
		{
			throw new ArrayIndexOutOfBoundsException(Integer.toString(index));
		}

		return (T) this.array[index];
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

	public Array<T> slice()
	{
		return new Array<>(this);
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
			return new Array<>();
		}

		Object[] newArray = new Object[endIndex - startIndex];

		java.lang.System.arraycopy(
			this.array, startIndex, newArray, 0, endIndex - startIndex);

		return new Array<>((T[]) newArray);
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
		return Arrays.asList(Arrays.copyOf(this.array, this.length)).contains(item);
	}

	private void grow(int minimum)
	{
		int newLength = this.array.length;

		while (newLength <= minimum)
		{
			newLength = newLength << 1;
		}

		if (newLength > this.array.length)
		{
			this.array = Arrays.copyOf(this.array, newLength);
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

	public Object[] toArray()
	{
		return Arrays.copyOf(this.array, this.length);
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Array)
		{
			Array<?> array = (Array) other;

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
