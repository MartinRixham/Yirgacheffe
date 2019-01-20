package yirgacheffe.compiler.statement;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.junit.Test;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.InvokeMethod;
import yirgacheffe.compiler.expression.Literal;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.This;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

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
		Signature caller = new Signature("caller", new Array<>());
		Variables variables = new Variables();

		TailCall tailCall = new TailCall(invocation, caller, variables);

		assertFalse(tailCall.returns());
		assertTrue(tailCall.getExpression() instanceof Nothing);
		assertEquals(new Array<>(), tailCall.getVariableReads());
		assertEquals(new Array<>(), tailCall.getVariableWrites());
	}

	@Test
	public void testCompilingTailCall()
	{
		Coordinate coordinate = new Coordinate(4, 6);
		Expression number = new Literal(PrimitiveType.DOUBLE, "1");
		Expression string = new Literal(new ReferenceType(String.class), "\"\"");

		Array<Expression> arguments = new Array<>(number, string);

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				"method",
				"MyClass",
				new This(new ReferenceType(this.getClass())),
				arguments);

		Statement invocation = new FunctionCall(invokeMethod);
		Type stringType = new ReferenceType(String.class);
		Array<Type> parameters = new Array<>(PrimitiveType.DOUBLE, stringType);
		Signature caller = new Signature("method", parameters);
		Variables variables = new Variables();

		TailCall tailCall = new TailCall(invocation, caller, variables);
		MethodNode methodVisitor = new MethodNode();

		Array<Error> errors = tailCall.compile(methodVisitor, variables, caller);

		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(5, instructions.size());

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