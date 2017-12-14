package yirgacheffe.compiler.type;

import yirgacheffe.parser.YirgacheffeParser;

import java.util.HashMap;
import java.util.Map;

public class Types
{
	private Map<String, Type> types;

	public Types(Map<String, Type> declaredTypes)
	{
		this.types = new HashMap<>(declaredTypes);

		this.types.put("void", new PrimitiveType("void"));
		this.types.put("bool", new PrimitiveType("bool"));
		this.types.put("char", new PrimitiveType("char"));
		this.types.put("num", new PrimitiveType("num"));
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

	public void putImportedType(String key, Type importedType)
	{
		this.types.put(key, importedType);
	}
}
