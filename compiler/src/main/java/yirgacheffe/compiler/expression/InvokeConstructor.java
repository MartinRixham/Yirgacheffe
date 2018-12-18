package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.function.Callable;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.function.Functions;
import yirgacheffe.compiler.function.MatchResult;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.compiler.type.Arguments;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

import java.lang.reflect.Constructor;

public class InvokeConstructor implements Expression
{
	private Coordinate coordinate;

	private Type owner;

	private Array<Expression> arguments;

	public InvokeConstructor(
		Coordinate coordinate,
		Type owner,
		Array<Expression> arguments)
	{
		this.coordinate = coordinate;
		this.owner = owner;
		this.arguments = arguments;
	}

	public Type getType(Variables variables)
	{
		return this.owner;
	}

	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
	{
		Array<Type> argumentTypes = new Array<>();

		for (Expression argument: this.arguments)
		{
			argumentTypes.push(argument.getType(variables));
		}

		Constructor<?>[] constructors = this.owner.reflectionClass().getConstructors();

		Array<Callable> functions = new Array<>();
		Arguments arguments = new Arguments(argumentTypes);

		for (Constructor<?> constructor : constructors)
		{
			functions.push(new Function(this.owner, constructor));
		}

		MatchResult matchResult =
			new Functions(
				this.coordinate,
				this.owner.toFullyQualifiedType(),
				functions,
				true)
				.getMatchingExecutable(arguments);

		if (this.owner instanceof NullType)
		{
			return matchResult.getErrors();
		}

		String typeWithSlashes =
			this.owner.toFullyQualifiedType().replace(".", "/");

		methodVisitor.visitTypeInsn(Opcodes.NEW, typeWithSlashes);
		methodVisitor.visitInsn(Opcodes.DUP);

		Array<Error> errors = new Array<>();

		for (Expression argument: this.arguments)
		{
			errors = errors.concat(argument.compile(methodVisitor, variables));
		}

		methodVisitor.visitMethodInsn(
			Opcodes.INVOKESPECIAL,
			typeWithSlashes,
			"<init>",
			matchResult.getFunction().getDescriptor(),
			false);

		return matchResult.getErrors().concat(errors);
	}

	public Expression getFirstOperand()
	{
		if (this.arguments.length() == 0)
		{
			return this;
		}
		else
		{
			return this.arguments.get(0).getFirstOperand();
		}
	}

	public Array<VariableRead> getVariableReads()
	{
		Array<VariableRead> variableReads = new Array<>();

		for (Expression argument: this.arguments)
		{
			variableReads = variableReads.concat(argument.getVariableReads());
		}

		return variableReads;
	}
}
