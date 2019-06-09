package yirgacheffe.compiler.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.junit.Test;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Bool;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.InvalidExpression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.Num;
import yirgacheffe.compiler.expression.Streeng;
import yirgacheffe.compiler.expression.This;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class IfTest
{
	@Test
	public void testIfStatement()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Expression condition = new Bool("true");
		Coordinate coordinate = new Coordinate(3, 5);
		Statement statement = new Return(coordinate, PrimitiveType.VOID);
		If ifStatement = new If(condition, statement);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());

		Array<Error> errors = ifStatement.compile(methodVisitor, variables, caller);

		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(4, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_1, firstInstruction.getOpcode());

		JumpInsnNode secondInstruction = (JumpInsnNode) instructions.get(1);
		Label label = secondInstruction.label.getLabel();

		assertEquals(Opcodes.IFEQ, secondInstruction.getOpcode());

		LabelNode thirdInstruction = (LabelNode) instructions.get(2);
		assertNotEquals(label, thirdInstruction.getLabel());

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.RETURN, fourthInstruction.getOpcode());
	}

	@Test
	public void testInvalidCondition()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());

		If ifStatement =
			new If(new InvalidExpression(PrimitiveType.BOOLEAN), new DoNothing());

		Array<Error> errors = ifStatement.compile(methodVisitor, variables, caller);

		assertEquals(1, errors.length());
		assertFalse(ifStatement.isEmpty());
	}

	@Test
	public void testInvalidStatement()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());

		If statement =
			new If(new InvalidExpression(PrimitiveType.BOOLEAN), new DoNothing());

		If ifStatement =
			new If(new Nothing(), statement);

		Array<Error> errors = ifStatement.compile(methodVisitor, variables, caller);

		assertEquals(1, errors.length());
	}

	@Test
	public void testGettingVariableReads()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		VariableRead read = new VariableRead(coordinate, "myVariable");
		VariableWrite write = new VariableWrite(coordinate, "var", read);
		Statement ifStatement = new If(read, write);

		Array<VariableRead> reads = ifStatement.getVariableReads();

		assertTrue(reads.indexOf(read) >= 0);

		Array<VariableWrite> writes = ifStatement.getVariableWrites();

		assertTrue(writes.indexOf(write) >= 0);
		assertTrue(ifStatement.getExpression() instanceof Nothing);
	}

	@Test
	public void testIfObject()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Expression condition = new This(new ReferenceType(Object.class));
		Coordinate coordinate = new Coordinate(3, 5);
		Statement statement = new Return(coordinate, PrimitiveType.VOID);
		If ifStatement = new If(condition, statement);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());

		Array<Error> errors = ifStatement.compile(methodVisitor, variables, caller);

		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(4, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		JumpInsnNode secondInstruction = (JumpInsnNode) instructions.get(1);
		Label label = secondInstruction.label.getLabel();

		assertEquals(Opcodes.IFNULL, secondInstruction.getOpcode());

		LabelNode thirdInstruction = (LabelNode) instructions.get(2);
		assertNotEquals(label, thirdInstruction.getLabel());

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.RETURN, fourthInstruction.getOpcode());
	}

	@Test
	public void testIfString()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Expression condition = new Streeng("\"thingy\"");
		Coordinate coordinate = new Coordinate(3, 5);
		Statement statement = new Return(coordinate, PrimitiveType.VOID);
		If ifStatement = new If(condition, statement);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());

		Array<Error> errors = ifStatement.compile(methodVisitor, variables, caller);

		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(5, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals("thingy", firstInstruction.cst);

		MethodInsnNode secondInstruction = (MethodInsnNode) instructions.get(1);

		assertEquals(Opcodes.INVOKESTATIC, secondInstruction.getOpcode());
		assertEquals("yirgacheffe/lang/Falsyfier", secondInstruction.owner);
		assertEquals("isTruthy", secondInstruction.name);
		assertEquals("(Ljava/lang/String;)Z", secondInstruction.desc);

		JumpInsnNode thirdInstruction = (JumpInsnNode) instructions.get(2);
		Label label = thirdInstruction.label.getLabel();

		assertEquals(Opcodes.IFEQ, thirdInstruction.getOpcode());

		LabelNode fourthInstruction = (LabelNode) instructions.get(3);

		assertNotEquals(label, fourthInstruction.getLabel());

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.RETURN, fifthInstruction.getOpcode());
	}

	@Test
	public void testIfDouble()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Expression condition = new Num("1.1");
		Coordinate coordinate = new Coordinate(3, 5);
		Statement statement = new Return(coordinate, PrimitiveType.VOID);
		If ifStatement = new If(condition, statement);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());

		Array<Error> errors = ifStatement.compile(methodVisitor, variables, caller);

		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(5, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(1.1, firstInstruction.cst);

		MethodInsnNode secondInstruction = (MethodInsnNode) instructions.get(1);

		assertEquals(Opcodes.INVOKESTATIC, secondInstruction.getOpcode());
		assertEquals("yirgacheffe/lang/Falsyfier", secondInstruction.owner);
		assertEquals("isTruthy", secondInstruction.name);
		assertEquals("(D)Z", secondInstruction.desc);

		JumpInsnNode thirdInstruction = (JumpInsnNode) instructions.get(2);
		Label label = thirdInstruction.label.getLabel();

		assertEquals(Opcodes.IFEQ, thirdInstruction.getOpcode());

		LabelNode fourthInstruction = (LabelNode) instructions.get(3);

		assertNotEquals(label, fourthInstruction.getLabel());

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.RETURN, fifthInstruction.getOpcode());
	}
}
