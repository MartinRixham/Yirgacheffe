package yirgacheffe.compiler.listener;

import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FieldDeclarationListener extends TypeListener
{
	protected Map<String, Type> fieldTypes = new HashMap<>();

	protected Map<String, Object> constants = new HashMap<>();

	private Set<String> fields = new HashSet<>();

	public FieldDeclarationListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void exitFieldDeclaration(YirgacheffeParser.FieldDeclarationContext context)
	{
		String identifier = context.Identifier().getText();

		if (this.fields.contains(identifier))
		{
			String message = "Duplicate field '" + identifier + "'.";

			this.errors.push(new Error(context, message));
		}
		else
		{
			this.fields.add(identifier);
		}

		if (context.type() == null)
		{
			String message = "Field declaration should start with type.";

			this.errors.push(new Error(context, message));
		}

		String fieldName = context.Identifier().getText();
		Type type = this.types.getType(context.type());

		int access = Opcodes.ACC_PROTECTED;

		if (context.Const() != null)
		{
			access |= Opcodes.ACC_STATIC | Opcodes.ACC_FINAL;
		}

		this.writer.visitField(
			access,
			fieldName,
			type.toJVMType(),
			null,
			null);

		if (context.Const() == null)
		{
			this.fieldTypes.put(fieldName, type);
		}
	}
}
