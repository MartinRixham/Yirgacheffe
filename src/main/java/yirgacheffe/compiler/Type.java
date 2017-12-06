package yirgacheffe.compiler;

public enum Type
{
	INTEGER("I"),

	STRING("Ljava/lang/String;");

	private String jvmType;

	public static Type parse(String yirgacheffeType)
	{
		if (yirgacheffeType.equals("int"))
		{
			return Type.INTEGER;
		}
		else if (yirgacheffeType.equals("String"))
		{
			return Type.STRING;
		}
		else
		{
			String error =
				"Failed to parse type: " +
				yirgacheffeType + " is not a type.";

			throw new YirgacheffeException(error);
		}
	}

	Type(String jvmType)
	{
		this.jvmType = jvmType;
	}

	public String getJVMType()
	{
		return this.jvmType;
	}
}
