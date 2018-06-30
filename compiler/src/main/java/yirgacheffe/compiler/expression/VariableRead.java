package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;

public class VariableRead implements Expression
{
	private int loadInstruction;

	private int index;

	public VariableRead(int loadInstruction, int index)
	{
		this.loadInstruction = loadInstruction;
		this.index = index;
	}

	@Override
	public void compile(MethodVisitor methodVisitor)
	{
		methodVisitor.visitVarInsn(this.loadInstruction, this.index);
	}

	@Override
	public int getStackHeight()
	{
		return 0;
	}
}
