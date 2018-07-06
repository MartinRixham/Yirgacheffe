package yirgacheffe.compiler.statement;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.InvokeConstructor;
import yirgacheffe.compiler.expression.Literal;
import yirgacheffe.compiler.function.Callable;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.lang.Array;

import java.lang.reflect.Constructor;

import static org.junit.Assert.assertEquals;

public class FunctionCallTest
{
	@Test
	public void testFunctionThatReturnsDouble() throws Exception
	{
		MethodNode methodVisitor = new MethodNode();
		Constructor<Double> constructor = Double.class.getConstructor(double.class);
		Callable function = new Function(PrimitiveType.DOUBLE, constructor);
		Array<Expression> arguments = new Array<>(new Literal(PrimitiveType.DOUBLE, "3"));
		InvokeConstructor invoke = new InvokeConstructor(function, arguments);
		FunctionCall functionCall = new FunctionCall(invoke);

		functionCall.compile(methodVisitor);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(5, instructions.size());

		TypeInsnNode firstInstruction = (TypeInsnNode) instructions.get(0);

		assertEquals(Opcodes.NEW, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		LdcInsnNode thirdInstruction = (LdcInsnNode) instructions.get(2);

		assertEquals(Opcodes.LDC, thirdInstruction.getOpcode());

		MethodInsnNode fourthInstructoin = (MethodInsnNode) instructions.get(3);

		assertEquals(Opcodes.INVOKESPECIAL, fourthInstructoin.getOpcode());

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.POP2, fifthInstruction.getOpcode());
	}
}
