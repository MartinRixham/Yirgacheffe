package yirgacheffe.compiler.statement;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.junit.Test;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.InvokeMethod;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.Num;
import yirgacheffe.compiler.expression.Streeng;
import yirgacheffe.compiler.expression.This;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TailCallTest
{
	public void method(double dub, String string)
	{
	}

	@Test
	public void testTailCall()
	{
		Statement invocation = new DoNothing();
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Variables variables = new LocalVariables(1, new HashMap<>());

		TailCall tailCall = new TailCall(invocation, caller, variables);

		assertEquals(0, variables.getStack().length());
		assertFalse(tailCall.returns());
		assertTrue(tailCall.getExpression() instanceof Nothing);
		assertEquals(new Array<>(), tailCall.getVariableReads());
		assertEquals(new Array<>(), tailCall.getVariableWrites());
		assertFalse(tailCall.getFieldAssignments().contains(""));
	}

	@Test
	public void testCompilingTailCall()
	{
		Coordinate coordinate = new Coordinate(4, 6);
		Expression number = new Num(coordinate, "1.0");
		Expression string = new Streeng(coordinate, "\"\"");

		Array<Expression> arguments = new Array<>(number, string);

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				"method",
				"MyClass",
				new This(coordinate, new ReferenceType(this.getClass())),
				arguments);

		Statement invocation = new FunctionCall(invokeMethod);
		Type stringType = new ReferenceType(String.class);
		Array<Type> parameters = new Array<>(PrimitiveType.DOUBLE, stringType);
		Signature caller = new Signature(new NullType(), "method", parameters);
		LocalVariables variables = new LocalVariables(1, new HashMap<>());
		TailCall tailCall = new TailCall(invocation, caller, variables);
		Result result = tailCall.compile(variables, caller);

		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(5, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCONST_1, firstInstruction.getOpcode());

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(Opcodes.LDC, secondInstruction.getOpcode());
		assertEquals("", secondInstruction.cst);

		VarInsnNode thirdInstruction = (VarInsnNode) instructions.get(2);

		assertEquals(Opcodes.ASTORE, thirdInstruction.getOpcode());
		assertEquals(3, thirdInstruction.var);

		VarInsnNode fourthInstruction = (VarInsnNode) instructions.get(3);

		assertEquals(Opcodes.DSTORE, fourthInstruction.getOpcode());
		assertEquals(1, fourthInstruction.var);

		JumpInsnNode fifthInstruction = (JumpInsnNode) instructions.get(4);

		assertEquals(Opcodes.GOTO, fifthInstruction.getOpcode());
	}
}
