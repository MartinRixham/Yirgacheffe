package yirgacheffe.compiler.statement;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.assignment.BlockFieldAssignment;
import yirgacheffe.compiler.assignment.FieldAssignment;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Delegate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.implementation.NullImplementation;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variable;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.Map;

public class ParameterDeclaration implements Statement
{
	private Coordinate coordinate;

	private String name;

	private Type type;

	public ParameterDeclaration(Coordinate coordinate, String name, Type type)
	{
		this.coordinate = coordinate;
		this.name = name;
		this.type = type;
	}

	public boolean returns()
	{
		return false;
	}

	public Result compile(Variables variables, Signature caller)
	{
		variables.declare(this.name, this.type);

		if (this.type.equals(PrimitiveType.VOID))
		{
			String message = "Cannot declare parameter of type Void.";

			return new Result().add(new Error(this.coordinate, message));
		}
		else if (this.type.hasParameter())
		{
			Variable variable = variables.getVariable(this.name);

			return new Result()
				.add(new VarInsnNode(
					variable.getType().getLoadInstruction(),
					variable.getIndex()))
				.add(new LdcInsnNode(String.join(",", this.type.getSignatureTypes())))
				.add(new MethodInsnNode(
					Opcodes.INVOKESTATIC,
					"yirgacheffe/lang/Bootstrap",
					"cacheObjectSignature",
					"(Ljava/lang/Object;Ljava/lang/String;)V",
					false));
		}

		return new Result();
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
		return new BlockFieldAssignment(new Array<>());
	}

	public Implementation getDelegatedInterfaces(
		Map<Delegate, Type> delegateTypes,
		Type thisType)
	{
		return new NullImplementation();
	}

	public Expression getExpression()
	{
		return new VariableRead(this.coordinate, this.name);
	}

	public boolean isEmpty()
	{
		return true;
	}

	@Override
	public boolean equals(Object other)
	{
		return other.equals(this.name);
	}

	@Override
	public int hashCode()
	{
		return this.name.hashCode();
	}
}
