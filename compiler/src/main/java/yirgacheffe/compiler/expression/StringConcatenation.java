package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class StringConcatenation implements Expression
{
	private BinaryOperation binaryOperation;

	public StringConcatenation(BinaryOperation expression)
	{
		this.binaryOperation = expression;
	}

	public Type getType(Variables variables)
	{
		return this.binaryOperation.getType(variables);
	}

	public Result compile(Variables variables)
	{
		Type string = new ReferenceType(String.class);

		if (this.binaryOperation.getType(variables).isAssignableTo(string))
		{
			String secondOperandType =
				this.binaryOperation.getSecondOperandType(variables);

			return new Result()
				.add(new TypeInsnNode(Opcodes.NEW, "java/lang/StringBuilder"))
				.add(new InsnNode(Opcodes.DUP))
				.add(new MethodInsnNode(
					Opcodes.INVOKESPECIAL,
					"java/lang/StringBuilder",
					"<init>",
					"()V",
					false))
				.concat(this.binaryOperation.compile(variables))
				.add(new MethodInsnNode(
					Opcodes.INVOKEVIRTUAL,
					"java/lang/StringBuilder",
					"append",
					"(" + secondOperandType + ")Ljava/lang/StringBuilder;",
					false))
				.add(new MethodInsnNode(
					Opcodes.INVOKEVIRTUAL,
					"java/lang/StringBuilder",
					"toString",
					"()Ljava/lang/String;",
					false));
		}
		else
		{
			return this.binaryOperation.compile(variables);
		}
	}

	public Result compileCondition(Variables variables, Label trueLabel, Label falseLabel)
	{
		return this.compile(variables);
	}

	public boolean isCondition(Variables variables)
	{
		return false;
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.binaryOperation.getVariableReads();
	}
}
