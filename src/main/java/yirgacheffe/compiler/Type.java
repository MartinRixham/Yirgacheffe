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
		else
		{
			return Type.STRING;
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
