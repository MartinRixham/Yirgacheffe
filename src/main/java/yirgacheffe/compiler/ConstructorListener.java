package yirgacheffe.compiler;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.List;
import java.util.Map;

public class ConstructorListener extends MethodListener
{
	public ConstructorListener(
		String directory,
		Map<String, DeclaredType> declaredTypes,
		BytecodeClassLoader classLoader,
		ParseErrorListener errorListener,
		ClassWriter writer)
	{
		super(directory, declaredTypes, classLoader, errorListener, writer);
	}

	@Override
	public void enterConstructorDeclaration(
		YirgacheffeParser.ConstructorDeclarationContext context)
	{
		if (context.Modifier() == null)
		{
			String message =
				"Expected public or private access modifier " +
					"at start of constructor declaration.";

			this.errors.add(new Error(context, message));
		}
		else
		{
			this.writer.visitMethod(
				Opcodes.ACC_PUBLIC,
				"<init>",
				this.getMethodDescriptor(context.parameters()),
				null,
				null);

			this.hasDefaultConstructor = false;
		}
	}

	private String getMethodDescriptor(YirgacheffeParser.ParametersContext parameters)
	{
		StringBuilder descriptor = new StringBuilder("(");
		List<YirgacheffeParser.ParameterContext> parameterList = parameters.parameter();

		for (YirgacheffeParser.ParameterContext parameter : parameterList)
		{
			YirgacheffeParser.TypeContext typeContext = parameter.type();

			if (typeContext != null)
			{
				Type type = this.getType(typeContext);

				descriptor.append(type.toJVMType());
			}
		}

		descriptor.append(")V");

		return descriptor.toString();
	}

	private Type getType(YirgacheffeParser.TypeContext context)
	{
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
}
