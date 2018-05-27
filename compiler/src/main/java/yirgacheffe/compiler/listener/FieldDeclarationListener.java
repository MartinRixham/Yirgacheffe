package yirgacheffe.compiler.listener;

import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.HashMap;
import java.util.Map;

public class FieldDeclarationListener extends ConstructorListener
{
	protected Map<String, Type> fieldTypes = new HashMap<>();

	public FieldDeclarationListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void exitFieldDeclaration(YirgacheffeParser.FieldDeclarationContext context)
	{
		if (context.type() == null)
		{
			Error error =
				new Error(context, "Field declaration should start with type.");

			this.errors.add(error);
		}

		String fieldName = context.Identifier().getText();
		Type type = this.types.getType(context.type());

		this.writer.visitField(
			Opcodes.ACC_PRIVATE,
			fieldName,
			type.toJVMType(),
			null,
			null);

		this.fieldTypes.put(fieldName, type);
	}
}
