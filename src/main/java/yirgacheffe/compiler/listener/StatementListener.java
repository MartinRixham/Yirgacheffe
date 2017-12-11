package yirgacheffe.compiler.listener;

import org.objectweb.asm.ClassWriter;
import yirgacheffe.compiler.Type.BytecodeClassLoader;
import yirgacheffe.compiler.Type.Types;
import yirgacheffe.compiler.error.ParseErrorListener;

public class StatementListener extends FieldListener
{
	public StatementListener(
		String directory,
		Types types,
		BytecodeClassLoader classLoader,
		ParseErrorListener errorListener,
		ClassWriter writer)
	{
		super(directory, types, classLoader, errorListener, writer);
	}
}
