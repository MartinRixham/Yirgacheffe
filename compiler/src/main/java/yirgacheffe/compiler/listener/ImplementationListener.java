package yirgacheffe.compiler.listener;

import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.Set;

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

			if (type.isPrimitive())
			{
				String message = "Cannot implement primitive type " + type + ".";

				this.errors.push(new Error(typeContext, message));
			}
			else if (!type.reflect().isInterface())
			{
				String message = "Cannot implement concrete type " + type + ".";

				this.errors.push(new Error(typeContext, message));
			}
			else
			{
				this.interfaces.push(type);
			}
		}

		this.getInterfaceMethods();
	}

	private void getInterfaceMethods()
	{
		for (java.lang.reflect.Type type:
			this.thisType.reflect().getGenericInterfaces())
		{
			Type interfaceType = Type.getType(type, thisType);

			Set<Function> methods = interfaceType.reflect().getPublicMethods();

			for (Function method: methods)
			{
				if (!method.isStatic())
				{
					this.interfaceMethods.push(method);
				}
			}
		}
	}
}
