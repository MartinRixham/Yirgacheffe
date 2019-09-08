package yirgacheffe.compiler.statement;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.assignment.FieldAssignment;
import yirgacheffe.compiler.expression.Delegate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.InvokeThis;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.implementation.InterfaceImplementation;
import yirgacheffe.compiler.implementation.NullImplementation;
import yirgacheffe.compiler.implementation.TotalImplementation;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.Map;

public class FunctionCall implements Statement
{
	private Expression expression;

	public FunctionCall(Expression expression)
	{
		this.expression = expression;
	}

	public boolean returns()
	{
		return false;
	}

	public Result compile(Variables variables, Signature caller)
	{
		Type type = this.expression.getType(variables);

		Result result = this.expression.compile(variables);

		int width = type.width();

		if (width == 1)
		{
			result = result.add(new InsnNode(Opcodes.POP));
		}
		else if (width == 2)
		{
			result = result.add(new InsnNode(Opcodes.POP2));
		}

		variables.stackPop();

		return result;
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.expression.getVariableReads();
	}

	public Array<VariableWrite> getVariableWrites()
	{
		return new Array<>();
	}

	public FieldAssignment getFieldAssignments()
	{
		if (this.expression instanceof InvokeThis)
		{
			return new FieldAssignment(new Array<>("this"));
		}
		else
		{
			return new FieldAssignment(new Array<>());
		}
	}

	public Implementation getDelegatedInterfaces(
		Map<Delegate, Type> delegateTypes,
		Type thisType)
	{
		if (delegateTypes.containsKey(this.expression))
		{
			Array<Type> delegatedInterfaces = new Array<>();
			Type delegatedType = delegateTypes.get(this.expression);

			java.lang.reflect.Type[] interfaces =
				delegatedType.reflectionClass().getGenericInterfaces();

			Array<Type> implementedInterfaces = new Array<>();

			for (java.lang.reflect.Type tp:
				thisType.reflectionClass().getGenericInterfaces())
			{
				implementedInterfaces.push(Type.getType(tp, thisType));
			}

			for (java.lang.reflect.Type interfaceType: interfaces)
			{
				Type type = Type.getType(interfaceType, thisType);

				if (implementedInterfaces.contains(type))
				{
					delegatedInterfaces.push(type);
				}
			}

			return new InterfaceImplementation(delegatedInterfaces);
		}
		else if (this.expression instanceof InvokeThis)
		{
			return new TotalImplementation();
		}
		else
		{
			return new NullImplementation();
		}
	}

	public Expression getExpression()
	{
		return this.expression;
	}

	@Override
	public boolean equals(Object object)
	{
		return this.expression.equals(object);
	}

	@Override
	public int hashCode()
	{
		return this.expression.hashCode();
	}

	public boolean isEmpty()
	{
		return false;
	}
}
