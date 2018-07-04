package yirgacheffe.compiler.type;

import yirgacheffe.lang.Array;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.HashMap;
import java.util.Map;

public class Types
{
	private Map<String, Type> types = new HashMap<>();

	public Types()
	{
		this.types.put("Void", PrimitiveType.VOID);
		this.types.put("Bool", PrimitiveType.BOOLEAN);
		this.types.put("Char", PrimitiveType.CHAR);
		this.types.put("Num", PrimitiveType.DOUBLE);
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
			Array<Type> typeParameters = new Array<>();

			for (YirgacheffeParser.TypeContext typeParameter:
				context.typeParameters().type())
			{
				typeParameters.push(this.getType(typeParameter));
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
