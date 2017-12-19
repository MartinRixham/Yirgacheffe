package yirgacheffe.lang;

public class MutableReference<T>
{
	private T reference;

	public MutableReference(T reference)
	{
		this.reference = reference;
	}

	public MutableReference()
	{
	}

	public T get()
	{
		return this.reference;
	}

	public void set(T reference)
	{
		this.reference = reference;
	}
}
