package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class Char implements Expression
{
	private String text;

	public Char(String text)
	{
		this.text = text;
	}

	public Type getType(Variables variables)
	{
		return PrimitiveType.CHAR;
	}

	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
	{
		methodVisitor.visitLdcInsn(this.text.charAt(1));

		return new Array<>();
	}

	public Array<VariableRead> getVariableReads()
	{
		return new Array<>();
	}
}
