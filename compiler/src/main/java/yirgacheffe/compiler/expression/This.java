package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

public class This implements Expression
{
	private Type type;

	public This(Type type)
	{
		this.type = type;
	}

	public Type getType(Variables variables)
	{
		return this.type;
	}

	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
	{
		methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

		return new Array<>();
	}

	public Array<VariableRead> getVariableReads()
	{
		return new Array<>();
	}
}
