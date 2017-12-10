package yirgacheffe.compiler.listener;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.Type.BytecodeClassLoader;
import yirgacheffe.compiler.Type.Types;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.error.ParseErrorListener;
import yirgacheffe.compiler.Type.Type;
import yirgacheffe.parser.YirgacheffeParser;

public class FieldListener extends ConstructorListener
{
	public FieldListener(
		String directory,
		Types types,
		BytecodeClassLoader classLoader,
		ParseErrorListener errorListener,
		ClassWriter writer)
	{
		super(directory, types, classLoader, errorListener, writer);
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
			String identifier = context.Identifier().getText();
			Type type = this.types.getType(context.type());

			this.writer
				.visitField(
					Opcodes.ACC_PRIVATE,
					identifier,
					type.toJVMType(),
					null,
					null);

			if (!(context.expression() == null))
			{
				if (!this.checkTypes(type, context.expression()))
				{
					this.errors.add(new Error(context, ""));
				}

				this.assignment = context;
			}
		}
	}

	private boolean checkTypes(
		Type fieldType,
		YirgacheffeParser.ExpressionContext expression)
	{
		YirgacheffeParser.LiteralContext literal = expression.literal();
		String jvmType = fieldType.toJVMType();

		if (literal.BooleanLiteral() != null)
		{
			return jvmType.equals("B");
		}
		else
		{
			return true;
		}
	}

	@Override
	public void enterInterfaceFieldDeclaration(
		YirgacheffeParser.InterfaceFieldDeclarationContext context)
	{
		this.errors.add(new Error(context, "Interface cannot contain field."));
	}
}
