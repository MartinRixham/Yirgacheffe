package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

public class FieldRead implements Expression
{
	private Coordinate coordinate;

	private Expression owner;

	private String name;

	private Type type;

	public FieldRead(Coordinate coordinate, Expression owner, String name, Type type)
	{
		this.coordinate = coordinate;
		this.owner = owner;
		this.name = name;
		this.type = type;
	}

	public Type getType(Variables variables)
	{
		return this.type;
	}

	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
	{
		Array<Error> errors = this.owner.compile(methodVisitor, variables);

		this.coordinate.compile(methodVisitor);

		methodVisitor.visitFieldInsn(
			Opcodes.GETFIELD,
			this.owner.getType(variables).toFullyQualifiedType().replace(".", "/"),
			this.name,
			this.type.toJVMType());

		return errors;
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.owner.getVariableReads();
	}
}
