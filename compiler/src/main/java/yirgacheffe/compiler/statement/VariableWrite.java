package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.type.Type;

public class VariableWrite implements Statement
{
	private int index;

	private Expression expression;

	public VariableWrite(int index, Expression expression)
	{
		this.index = index;
		this.expression = expression;
	}

	@Override
	public void compile(MethodVisitor methodVisitor)
	{
		this.expression.compile(methodVisitor);

		Type type = this.expression.getType();

		methodVisitor.visitVarInsn(type.getStoreInstruction(), this.index);
	}
}
