package yirgacheffe.compiler.listener;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.Type.BytecodeClassLoader;
import yirgacheffe.compiler.Type.DeclaredType;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.Type.ImportedType;
import yirgacheffe.compiler.error.ParseErrorListener;
import yirgacheffe.compiler.Type.Type;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.Map;

public class FieldListener extends ConstructorListener
{
	public FieldListener(
		String directory,
		Map<String, DeclaredType> declaredTypes,
		BytecodeClassLoader classLoader,
		ParseErrorListener errorListener,
		ClassWriter writer)
	{
		super(directory, declaredTypes, classLoader, errorListener, writer);
	}

	@Override
	public void enterFieldDeclaration(YirgacheffeParser.FieldDeclarationContext context)
	{
		if (context.type() == null)
		{
			Error error =
				new Error(context, "Field declaration should start with type.");

			this.errors.add(error);
		}
		else
		{
			String typeName = context.type().getText();
			String identifier = context.Identifier().getText();
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
				type = new ImportedType(context.type());
			}

			this.writer
				.visitField(
					Opcodes.ACC_PRIVATE,
					identifier,
					type.toJVMType(),
					null,
					null);
		}
	}

	@Override
	public void enterInterfaceFieldDeclaration(
		YirgacheffeParser.InterfaceFieldDeclarationContext context)
	{
		this.errors.add(new Error(context, "Interface cannot contain field."));
	}
}
