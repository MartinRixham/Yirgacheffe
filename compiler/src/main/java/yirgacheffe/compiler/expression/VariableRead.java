package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.type.Variable;

public class VariableRead implements Expression
{
	private Variable variable;

	public VariableRead(Variable variable)
	{
		this.variable = variable;
	}

	@Override
	public void compile(MethodVisitor methodVisitor)
	{
		methodVisitor.visitVarInsn(
			this.variable.getType().getLoadInstruction(),
			this.variable.getIndex());
	}
}
