package yirgacheffe.lang;

public final class Falsyfier
{
	private Falsyfier()
	{
	}

	public static boolean isTruthy(double value)
	{
		return value != 0.0 && value == value;
	}

	public static boolean isTruthy(String value)
	{
		return value != null &&
			value.length() != 0;
	}
}
