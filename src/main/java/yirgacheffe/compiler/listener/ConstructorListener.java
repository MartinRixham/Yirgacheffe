package yirgacheffe.compiler.listener;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.type.BytecodeClassLoader;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.error.ParseErrorListener;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.Map;

public class ConstructorListener extends MethodListener
{
	public ConstructorListener(
		String sourceFile,
		Map<String, Type> declaredTypes,
		BytecodeClassLoader classLoader,
		ParseErrorListener errorListener,
		ClassWriter writer)
	{
		super(sourceFile, declaredTypes, classLoader, errorListener, writer);
	}

	@Override
	public void exitConstructorDeclaration(
		YirgacheffeParser.ConstructorDeclarationContext context)
	{
		if (!context.Identifier().getText().equals(this.className))
		{
			String message =
				"Constructor of incorrect type " + context.Identifier().getText() +
					": expected " + this.className + ".";

			this.errors.add(new Error(context.Identifier().getSymbol(), message));
		}

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

		String descriptor = this.getParameterDescriptor(context.parameter()) + "V";

		this.methodVisitor =
			this.writer.visitMethod(
				isPrivate ? Opcodes.ACC_PRIVATE : Opcodes.ACC_PUBLIC,
				"<init>",
				descriptor,
				null,
				null);

		this.hasDefaultConstructor = false;
	}
}
