package yirgacheffe.compiler.listener;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.Type.BytecodeClassLoader;
import yirgacheffe.compiler.Type.Types;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.error.ParseErrorListener;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.List;

public class ConstructorListener extends MethodListener
{
	public ConstructorListener(
		String directory,
		Types types,
		BytecodeClassLoader classLoader,
		ParseErrorListener errorListener,
		ClassWriter writer)
	{
		super(directory, types, classLoader, errorListener, writer);
	}

	@Override
	public void enterConstructorDeclaration(
		YirgacheffeParser.ConstructorDeclarationContext context)
	{
		boolean isPrivate = false;

		if (context.Modifier() == null)
		{
			String message =
				"Expected public or private access modifier " +
					"at start of constructor declaration.";

			this.errors.add(new Error(context, message));
		}
		else
		{
			isPrivate = context.Modifier().getText().equals("private");
		}

		if (!context.Identifier().getText().equals(this.className))
		{
			String message =
				"Constructor of incorrect type " + context.Identifier().getText() +
					": expected " + this.className + ".";

			this.errors.add(new Error(context.Identifier().getSymbol(), message));
		}

		this.writer.visitMethod(
			isPrivate ? Opcodes.ACC_PRIVATE : Opcodes.ACC_PUBLIC,
			"<init>",
			this.getMethodDescriptor(context.parameter()),
			null,
			null);

		this.hasDefaultConstructor = false;
	}

	private String getMethodDescriptor(
		List<YirgacheffeParser.ParameterContext> parameters)
	{
		return this.getParameterDescriptor(parameters) + "V";
	}
}
