package yirgacheffe.compiler;

import org.objectweb.asm.ClassWriter;

public class YirgacheffeClassWriter extends ClassWriter
{
	public YirgacheffeClassWriter(int flags)
	{
		super(flags);
	}

	@Override
	protected String getCommonSuperClass(final String left, final String right)
	{
		return "java/lang/Object";
	}
}
