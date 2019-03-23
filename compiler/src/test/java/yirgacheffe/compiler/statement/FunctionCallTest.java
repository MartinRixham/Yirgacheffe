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
import yirgacheffe.compiler.expression.Num;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
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
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Coordinate coordinate = new Coordinate(1, 0);
		MethodNode methodVisitor = new MethodNode();
		Array<Expression> arguments =
			new Array<>(new Num("3"));

		InvokeConstructor invoke =
			new InvokeConstructor(
				coordinate,
				PrimitiveType.DOUBLE,
				arguments);

		FunctionCall functionCall = new FunctionCall(invoke);
		Variables variables = new Variables();

		functionCall.compile(methodVisitor, variables, caller);

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
	public void testGettingVariableReads()
	{
		Coordinate coordinate = new Coordinate(2, 4);
		VariableRead variable = new VariableRead(coordinate, "var");

		Expression expression =
			new Expression()
			{
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

					return new Array<>(variable);
				}
			};

		FunctionCall functionCall = new FunctionCall(expression);

		assertEquals(expression.hashCode(), functionCall.hashCode());
		assertEquals(1, functionCall.getVariableReads().length());
		assertEquals(variable, functionCall.getVariableReads().get(0));
	}
}
