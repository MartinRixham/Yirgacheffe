package yirgacheffe.compiler.type;

import yirgacheffe.parser.YirgacheffeParser;

import java.util.HashMap;
import java.util.Map;

public class Types
{
	private Map<String, Type> types = new HashMap<>();

	public Types()
	{
		this.types.put("void", PrimitiveType.VOID);
		this.types.put("bool", PrimitiveType.BOOLEAN);
		this.types.put("char", PrimitiveType.CHAR);
		this.types.put("num", PrimitiveType.DOUBLE);
		this.types.put("String", new StringType());
	}

	public Type getType(YirgacheffeParser.TypeContext context)
	{
		if (context == null)
		{
			return new NullType();
		}

		Type type = this.types.get(context.getText());

		if (type == null)
		{
			return new NullType();
		}

		return  type;
	}

	public boolean containsKey(String key)
	{
		return this.types.containsKey(key);
	}

	public void put(String key, Type importedType)
	{
		this.types.put(key, importedType);
	}
}
