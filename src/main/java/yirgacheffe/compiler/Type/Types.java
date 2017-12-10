package yirgacheffe.compiler.Type;

import yirgacheffe.parser.YirgacheffeParser;

import java.util.HashMap;
import java.util.Map;

public class Types
{
	private Map<String, ImportedType> importedTypes = new HashMap<>();

	private Map<String, DeclaredType> declaredTypes;

	public Types(Map<String, DeclaredType> declaredTypes)
	{
		this.declaredTypes = declaredTypes;
	}

	public Type getType(YirgacheffeParser.TypeContext context)
	{
		if (context == null)
		{
			return new NullType();
		}

		String typeName = context.getText();
		Type type;

		if (this.importedTypes.containsKey(typeName))
		{
			type = this.importedTypes.get(typeName);
		}
		else if (this.declaredTypes.containsKey(typeName))
		{
			type = this.declaredTypes.get(typeName);
		}
		else
		{
			type = new ImportedType(context);
		}

		return type;
	}

	public boolean containsKey(String key)
	{
		return
			this.importedTypes.containsKey(key) ||
			this.declaredTypes.containsKey(key);
	}

	public void putImportedType(String key, ImportedType importedType)
	{
		this.importedTypes.put(key, importedType);
	}

	public void putDeclaredType(String key, DeclaredType declaredType)
	{
		this.declaredTypes.put(key, declaredType);
	}
}
