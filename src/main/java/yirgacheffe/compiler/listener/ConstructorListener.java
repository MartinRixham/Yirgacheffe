package yirgacheffe.compiler.listener;

import org.objectweb.asm.ClassWriter;
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
	public void enterConstructorIdentifier(
		YirgacheffeParser.ConstructorIdentifierContext context)
	{
		if (!context.getText().equals(this.className))
		{
			String message =
				"Constructor of incorrect type " + context.getText() +
					": expected " + this.className + ".";

			this.errors.add(new Error(context, message));
		}

		this.hasDefaultConstructor = false;
	}
}
