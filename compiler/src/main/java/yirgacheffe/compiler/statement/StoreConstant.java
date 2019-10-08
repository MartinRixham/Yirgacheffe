package yirgacheffe.compiler.statement;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.assignment.FieldAssignment;
import yirgacheffe.compiler.expression.Delegate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.implementation.NullImplementation;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.Map;

public class StoreConstant implements Statement
{
	private Type owner;

	private Expression key;

	private Expression value;

	public StoreConstant(Type owner, Expression key, Expression value)
	{
		this.owner = owner;
		this.key = key;
		this.value = value;
	}

	public boolean returns()
	{
		return false;
	}

	public Result compile(Variables variables, Signature caller)
	{
		Type object = new ReferenceType(Object.class);

		return new Result()
			.add(new FieldInsnNode(
				Opcodes.GETSTATIC,
				this.owner.toFullyQualifiedType(),
				"values",
				"Ljava/util/Map;"))
			.concat(this.key.compile(variables))
			.concat(this.key.getType(variables).convertTo(object))
			.concat(this.value.compile(variables))
			.add(new MethodInsnNode(
				Opcodes.INVOKEINTERFACE,
				"java/util/Map",
				"put",
				"(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"));
	}

	public Array<VariableRead> getVariableReads()
	{
		return new Array<>();
	}

	public Array<VariableWrite> getVariableWrites()
	{
		return new Array<>();
	}

	public FieldAssignment getFieldAssignments()
	{
		return new FieldAssignment(new Array<>());
	}

	public Implementation getDelegatedInterfaces(
		Map<Delegate, Type> delegateTypes,
		Type thisType)
	{
		return new NullImplementation();
	}

	public Expression getExpression()
	{
		return new Nothing();
	}

	public boolean isEmpty()
	{
		return false;
	}
}
