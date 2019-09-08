package yirgacheffe.compiler.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.assignment.FieldAssignment;
import yirgacheffe.compiler.expression.Delegate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.Map;

public class AttemptedStatement implements Statement
{
	private Statement statement;

	public AttemptedStatement(Statement statement)
	{
		this.statement = statement;
	}

	public boolean returns()
	{
		return false;
	}

	public Result compile(Variables variables, Signature caller)
	{
		Label start = new Label();
		Label end = new Label();
		Label handler = new Label();
		Label success = new Label();

		return new Result()
			.add(new TryCatchBlockNode(
				new LabelNode(start),
				new LabelNode(end),
				new LabelNode(handler),
				null))
			.add(new LabelNode(start))
			.concat(this.statement.compile(variables, caller))
			.add(new JumpInsnNode(Opcodes.GOTO, new LabelNode(success)))
			.add(new LabelNode(end))
			.add(new LabelNode(handler))
			.add(new InsnNode(Opcodes.POP))
			.add(new LabelNode(success));
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.statement.getVariableReads();
	}

	public Array<VariableWrite> getVariableWrites()
	{
		return this.statement.getVariableWrites();
	}

	public FieldAssignment getFieldAssignments()
	{
		return this.statement.getFieldAssignments();
	}

	public Implementation getDelegatedInterfaces(
		Map<Delegate, Type> delegateTypes,
		Type thisType)
	{
		return this.statement.getDelegatedInterfaces(delegateTypes, thisType);
	}

	public Expression getExpression()
	{
		return this.statement.getExpression();
	}

	public boolean isEmpty()
	{
		return false;
	}
}
