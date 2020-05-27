package yirgacheffe.compiler.function;

import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.lang.Array;

import java.util.Collection;

public class LengthMethods
{
	private static Array<ClassFunction> lengthMethods;

	static
	{
		try
		{
			lengthMethods =
				new Array(
					new ClassFunction(
						new ReferenceType(Array.class),
						Array.class.getMethod("length")),
					new ClassFunction(
						new ReferenceType(Collection.class),
						Collection.class.getMethod("size")));
		}
		catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		}
	}

	public boolean contains(Function function)
	{
		return lengthMethods.contains(function);
	}
}
