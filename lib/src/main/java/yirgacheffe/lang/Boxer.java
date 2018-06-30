package yirgacheffe.lang;

public final class Boxer
{
	private Boxer()
	{
	}

	public static boolean ofValue(Boolean value)
	{
		if (value == null)
		{
			return Boolean.FALSE;
		}
		else
		{
			return value;
		}
	}

	public static char ofValue(Character value)
	{
		if (value == null)
		{
			return Character.MIN_VALUE;
		}
		else
		{
			return value;
		}
	}

	public static double ofValue(Double value)
	{
		if (value == null)
		{
			return Double.NaN;
		}
		else
		{
			return value;
		}
	}
}
