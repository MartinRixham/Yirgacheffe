package yirgacheffe.compiler.listener;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldNode;
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

		if (context.Const() == null)
		{
			String fieldName = context.Identifier().getText();
			Type type = this.types.getType(context.type());

			int access = Opcodes.ACC_PROTECTED;

			this.classNode.fields.add(
				new FieldNode(
					access,
					fieldName,
					type.toJVMType(),
					type.getSignature(),
					null));
		}
	}
}
