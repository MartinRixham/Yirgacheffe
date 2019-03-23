package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class Num implements Expression
{
	private String text;

	public Num(String text)
	{
		this.text = text;
	}

	public Type getType(Variables variables)
	{
		return PrimitiveType.DOUBLE;
	}

	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
	{
		Double dub = new Double(this.text);

		if (dub == 0)
		{
			methodVisitor.visitInsn(Opcodes.DCONST_0);
		}
		else if (dub == 1)
		{
			methodVisitor.visitInsn(Opcodes.DCONST_1);
		}
		else
		{
			methodVisitor.visitLdcInsn(dub);
		}

		return new Array<>();
	}

	public Array<VariableRead> getVariableReads()
	{
		return new Array<>();
	}
}
