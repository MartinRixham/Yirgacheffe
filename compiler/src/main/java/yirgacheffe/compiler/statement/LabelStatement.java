package yirgacheffe.compiler.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.LabelNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.expression.Delegate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.Map;

public class LabelStatement implements Statement
{
	private Label label;

	public LabelStatement(Label label)
	{
		this.label = label;
	}

	public boolean returns()
	{
		return false;
	}

	public Result compile(Variables variables, Signature caller)
	{
		return new Result().add(new LabelNode((this.label)));
	}

	public Array<VariableRead> getVariableReads()
	{
		return new Array<>();
	}

	public Array<VariableWrite> getVariableWrites()
	{
		return new Array<>();
	}

	public Array<String> getFieldAssignments()
	{
		return new Array<>();
	}

	public Array<Type> getDelegatedInterfaces(
		Map<Delegate, Type> delegateTypes,
		Type thisType)
	{
		return new Array<>();
	}

	public Expression getExpression()
	{
		return new Nothing();
	}

	public boolean isEmpty()
	{
		return true;
	}
}
