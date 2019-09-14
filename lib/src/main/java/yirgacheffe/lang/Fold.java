package yirgacheffe.lang;

public class Fold<T extends Combinable<S>, S>
{
	private Array<? extends T> array;

	public Fold(Array<? extends T> array)
	{
		this.array = array;
	}

	public S with(S item)
	{
		for (int i = 0; i < this.array.length(); i++)
		{
			item = this.array.get(i).combineWith(item);
		}

		return item;
	}
}
