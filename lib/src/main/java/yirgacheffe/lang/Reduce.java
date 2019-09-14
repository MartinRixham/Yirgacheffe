package yirgacheffe.lang;

public class Reduce<T extends Combinable<T>>
{
	private Array<? extends T> array;

	public Reduce(Array<? extends T> array)
	{
		this.array = array;
	}

	public T to()
	{
		T item = this.array.get(0);

		for (int i = 1; i < this.array.length(); i++)
		{
			item = this.array.get(i).combineWith(item);
		}

		return item;
	}
}
