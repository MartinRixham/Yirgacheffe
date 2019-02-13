package yirgacheffe.compiler.listener;

import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.parser.YirgacheffeParser;

import java.lang.reflect.Method;

public class ImplementationListener extends ClassListener
{
	public ImplementationListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void exitImplementation(YirgacheffeParser.ImplementationContext context)
	{
		if (context.type().size() == 0)
		{
			String message = "Missing implemented type.";

			this.errors.push(new Error(context, message));
		}

		for (YirgacheffeParser.TypeContext typeContext: context.type())
		{
			Type type = this.types.getType(typeContext);

			if (type instanceof PrimitiveType)
			{
				String message = "Cannot implement primitive type " + type + ".";

				this.errors.push(new Error(typeContext, message));
			}
			else if (!type.reflectionClass().isInterface())
			{
				String message = "Cannot implement concrete type " + type + ".";

				this.errors.push(new Error(typeContext, message));
			}
			else
			{
				Method[] methods = type.reflectionClass().getMethods();

				for (Method method: methods)
				{
					this.interfaceMethods.push(new Function(type, method));
				}

				this.interfaces.push(type);
			}
		}
	}
}
