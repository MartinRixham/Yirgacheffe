package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class Nothing implements Expression
{
	public Type getType(Variables variables)
	{
		return PrimitiveType.VOID;
	}

	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
	{
		return new Array<>();
	}

	public Array<Error> compileCondition(
		MethodVisitor methodVisitor,
		Variables variables,
		Label trueLabel,
		Label falseLabel)
	{
		return this.compile(methodVisitor, variables);
	}

	public boolean isCondition(Variables variables)
	{
		return false;
	}

	public Array<VariableRead> getVariableReads()
	{
		return new Array<>();
	}
}
