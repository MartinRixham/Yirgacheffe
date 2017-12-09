package yirgacheffe.compiler.listener;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.Type.BytecodeClassLoader;
import yirgacheffe.compiler.Type.DeclaredType;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.error.ParseErrorListener;
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
		else if (!context.Identifier().getText().equals(this.className))
		{
			String message =
				"Constructor of incorrect type " + context.Identifier().getText() +
					": expected " + this.className + ".";

			this.errors.add(new Error(context.Identifier().getSymbol(), message));
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
		List<YirgacheffeParser.ParameterContext> parameterList = parameters.parameter();

		return this.getParameterDescriptor(parameterList) + "V";
	}
}
