package yirgacheffe.compiler.type;

import yirgacheffe.parser.YirgacheffeParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	}

	public Type getType(YirgacheffeParser.TypeContext context)
	{
		if (context == null)
		{
			return new NullType();
		}

		Type type = this.types.get(context.primaryType().getText());

		if (type == null)
		{
			return new NullType();
		}

		if (context.typeParameters() != null)
		{
			List<Type> typeParameters = new ArrayList<>();

			for (YirgacheffeParser.TypeContext typeParameter:
				context.typeParameters().type())
			{
				typeParameters.add(this.getType(typeParameter));
			}

			return new ParameterisedType((ReferenceType) type, typeParameters);
		}
		else
		{
			return type;
		}
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
