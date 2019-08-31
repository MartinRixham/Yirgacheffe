package yirgacheffe.lang;

public final class Boxer
{
	private Boxer()
	{
	}

	public static boolean toBoolean(Object value) throws Throwable
	{
		if (value instanceof Throwable)
		{
			throw ((Throwable) value);
		}
		else if (value == null)
		{
			return Boolean.FALSE;
		}
		else
		{
			return (Boolean) value;
		}
	}

	public static char toCharacter(Object value) throws Throwable
	{
		if (value instanceof Throwable)
		{
			throw ((Throwable) value);
		}
		else if (value == null)
		{
			return Character.MIN_VALUE;
		}
		else
		{
			return (Character) value;
		}
	}

	public static double toDouble(Object value) throws Throwable
	{
		if (value instanceof Throwable)
		{
			throw ((Throwable) value);
		}
		else if (value == null)
		{
			return Double.NaN;
		}
		else if (value instanceof Integer)
		{
			return ((Integer) value).doubleValue();
		}
		else if (value instanceof Long)
		{
			return ((Long) value).doubleValue();
		}
		else
		{
			return (Double) value;
		}
	}
}
