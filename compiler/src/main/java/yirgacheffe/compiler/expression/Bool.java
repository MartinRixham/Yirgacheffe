package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class Bool implements Expression, Literal
{
	private String text;

	public Bool(String text)
	{
		this.text = text;
	}

	public Type getType(Variables variables)
	{
		return PrimitiveType.BOOLEAN;
	}

	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
	{
		if (this.text.equals("true"))
		{
			methodVisitor.visitInsn(Opcodes.ICONST_1);
		}
		else
		{
			methodVisitor.visitInsn(Opcodes.ICONST_0);
		}

		return new Array<>();
	}

	public Array<Error> compileCondition(
		MethodVisitor methodVisitor,
		Variables variables,
		Label label)
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

	public Object getValue()
	{
		return this.text.equals("true");
	}
}
