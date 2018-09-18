package yirgacheffe.compiler.statement;

import org.objectweb.asm.Label;

public interface ConditionalStatement extends Statement
{
	Label getLabel();
}
