package yirgacheffe.compiler.statement;

import org.junit.Test;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.InvokeConstructor;
import yirgacheffe.compiler.expression.Literal;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;

public class FunctionCallTest
{
	@Test
	public void testFunctionThatReturnsDouble()
	{
		Coordinate coordinate = new Coordinate(1, 0);
		MethodNode methodVisitor = new MethodNode();
		Array<Expression> arguments =
			new Array<>(new Literal(PrimitiveType.DOUBLE, "3"));

		InvokeConstructor invoke =
			new InvokeConstructor(
				coordinate,
				PrimitiveType.DOUBLE,
				arguments);

		FunctionCall functionCall = new FunctionCall(invoke);
		Variables variables = new Variables();

		functionCall.compile(methodVisitor, variables);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(5, instructions.size());

		TypeInsnNode firstInstruction = (TypeInsnNode) instructions.get(0);
		InsnNode secondInstruction = (InsnNode) instructions.get(1);
		LdcInsnNode thirdInstruction = (LdcInsnNode) instructions.get(2);
		MethodInsnNode fourthInstruction = (MethodInsnNode) instructions.get(3);
		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.NEW, firstInstruction.getOpcode());
		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());
		assertEquals(Opcodes.LDC, thirdInstruction.getOpcode());
		assertEquals(Opcodes.INVOKESPECIAL, fourthInstruction.getOpcode());
		assertEquals(Opcodes.POP2, fifthInstruction.getOpcode());
	}

	@Test
	public void testGettingFunctionCallFirstOperand()
	{
		Literal one = new Literal(PrimitiveType.DOUBLE, "1");

		FunctionCall functionCall =
			new FunctionCall(new Expression()
			{
				public Expression getFirstOperand()
				{
					return one;
				}

				public Type getType(Variables variables)
				{
					return new NullType();
				}

				public Array<Error> compile(
					MethodVisitor methodVisitor,
					Variables variables)
				{
					return new Array<>();
				}

				public Array<VariableRead> getVariableReads()
				{
					return new Array<>();
				}
			});

		assertEquals(one, functionCall.getFirstOperand());
	}
}
