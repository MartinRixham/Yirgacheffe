package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.function.Callable;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.function.Functions;
import yirgacheffe.compiler.function.MatchResult;
import yirgacheffe.compiler.statement.StatementResult;
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

	private Callable function;

	public InvokeConstructor(
		Coordinate coordinate,
		Type owner,
		Array<Expression> arguments)
	{
		this.coordinate = coordinate;
		this.owner = owner;
		this.arguments = arguments;
	}

	@Override
	public Type check(StatementResult result)
	{
		Array<Type> argumentTypes = new Array<>();

		for (Expression argument: this.arguments)
		{
			argumentTypes.push(argument.check(result));
		}

		Constructor<?>[] constructors = this.owner.reflectionClass().getConstructors();
		Array<Callable> functions = new Array<>();
		Arguments arguments = new Arguments(argumentTypes);

		for (Constructor<?> constructor : constructors)
		{
			functions.push(new Function(this.owner, constructor));
		}

		MatchResult matchResult =
			new Functions(this.coordinate, this.owner.toFullyQualifiedType(), functions)
				.getMatchingExecutable(arguments);

		this.function = matchResult.getFunction();

		result.matchConstructor(matchResult);

		return this.owner;
	}

	@Override
	public void compile(MethodVisitor methodVisitor)
	{
		if (this.owner instanceof NullType)
		{
			return;
		}

		String typeWithSlashes =
			this.owner.toFullyQualifiedType().replace(".", "/");

		methodVisitor.visitTypeInsn(Opcodes.NEW, typeWithSlashes);
		methodVisitor.visitInsn(Opcodes.DUP);

		for (Expression argument: this.arguments)
		{
			argument.compile(methodVisitor);
		}

		methodVisitor.visitMethodInsn(
			Opcodes.INVOKESPECIAL,
			typeWithSlashes,
			"<init>",
			this.function.getDescriptor(),
			false);
	}
}
