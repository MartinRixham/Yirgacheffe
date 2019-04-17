package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class Streeng implements Expression
{
	private java.lang.String text;

	public Streeng(java.lang.String text)
	{
		this.text = text;
	}

	public Type getType(Variables variables)
	{
		return new ReferenceType(String.class);
	}

	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
	{
		methodVisitor.visitLdcInsn(this.text.substring(1, this.text.length() - 1));

		return new Array<>();
	}

	public Array<VariableRead> getVariableReads()
	{
		return new Array<>();
	}
}