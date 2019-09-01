package yirgacheffe.compiler.statement;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.Bool;
import yirgacheffe.compiler.expression.Delegate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.InvalidExpression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.Num;
import yirgacheffe.compiler.expression.Streeng;
import yirgacheffe.compiler.expression.This;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.implementation.InterfaceImplementation;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.lang.Array;

import java.util.HashMap;
import java.util.Map;

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
		LocalVariables variables = new LocalVariables(new HashMap<>());
		Result result = ifStatement.compile(variables, caller);

		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(0, variables.getStack().length());
		assertEquals(4, instructions.length());

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
		LocalVariables variables = new LocalVariables(new HashMap<>());

		If ifStatement =
			new If(new InvalidExpression(PrimitiveType.BOOLEAN), new DoNothing());

		Result result = ifStatement.compile(variables, caller);

		assertEquals(1, result.getErrors().length());
		assertFalse(ifStatement.isEmpty());
	}

	@Test
	public void testInvalidStatement()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		LocalVariables variables = new LocalVariables(new HashMap<>());

		If statement =
			new If(new InvalidExpression(PrimitiveType.BOOLEAN), new DoNothing());

		If ifStatement =
			new If(new Nothing(), statement);

		Result result = ifStatement.compile(variables, caller);

		assertEquals(1, result.getErrors().length());
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
		assertEquals(0, ifStatement.getFieldAssignments().length());
	}

	@Test
	public void testIfObject()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Expression condition = new This(new ReferenceType(Object.class));
		Coordinate coordinate = new Coordinate(3, 5);
		Statement statement = new Return(coordinate, PrimitiveType.VOID);
		If ifStatement = new If(condition, statement);
		LocalVariables variables = new LocalVariables(new HashMap<>());
		Result result = ifStatement.compile(variables, caller);

		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(4, instructions.length());

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
		LocalVariables variables = new LocalVariables(new HashMap<>());
		Result result = ifStatement.compile(variables, caller);

		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(12, instructions.length());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals("thingy", firstInstruction.cst);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		JumpInsnNode thirdInstruction = (JumpInsnNode) instructions.get(2);
		Label falseLabel = thirdInstruction.label.getLabel();

		assertEquals(Opcodes.IFNULL, thirdInstruction.getOpcode());

		MethodInsnNode fourthInstruction = (MethodInsnNode) instructions.get(3);

		assertEquals(Opcodes.INVOKEVIRTUAL, fourthInstruction.getOpcode());
		assertEquals("java/lang/String", fourthInstruction.owner);
		assertEquals("length", fourthInstruction.name);
		assertEquals("()I", fourthInstruction.desc);

		JumpInsnNode fifthInstruction = (JumpInsnNode) instructions.get(4);
		Label trueLabel = fifthInstruction.label.getLabel();

		assertEquals(Opcodes.GOTO, fifthInstruction.getOpcode());

		LabelNode sixthInstruction = (LabelNode) instructions.get(5);

		assertEquals(falseLabel, sixthInstruction.getLabel());

		InsnNode seventhInstruction = (InsnNode) instructions.get(6);

		assertEquals(Opcodes.POP, seventhInstruction.getOpcode());

		InsnNode eighthInstruction = (InsnNode) instructions.get(7);

		assertEquals(Opcodes.ICONST_0, eighthInstruction.getOpcode());

		LabelNode ninthInstruction = (LabelNode) instructions.get(8);

		assertEquals(trueLabel, ninthInstruction.getLabel());

		JumpInsnNode tenthInstruction = (JumpInsnNode) instructions.get(9);
		Label label = tenthInstruction.label.getLabel();

		assertEquals(Opcodes.IFEQ, tenthInstruction.getOpcode());

		LabelNode eleventhInstruction = (LabelNode) instructions.get(10);

		assertNotEquals(label, eleventhInstruction.getLabel());

		InsnNode twelfthInstruction = (InsnNode) instructions.get(11);

		assertEquals(Opcodes.RETURN, twelfthInstruction.getOpcode());
	}

	@Test
	public void testIfDouble()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Expression condition = new Num("1.1");
		Coordinate coordinate = new Coordinate(3, 5);
		Statement statement = new Return(coordinate, PrimitiveType.VOID);
		If ifStatement = new If(condition, statement);
		LocalVariables variables = new LocalVariables(new HashMap<>());
		Result result = ifStatement.compile(variables, caller);

		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(8, instructions.length());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(1.1, firstInstruction.cst);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DCONST_1, secondInstruction.getOpcode());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DCMPL, thirdInstruction.getOpcode());

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.ICONST_1, fourthInstruction.getOpcode());

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.IADD, fifthInstruction.getOpcode());

		JumpInsnNode sixthInstruction = (JumpInsnNode) instructions.get(5);
		Label label = sixthInstruction.label.getLabel();

		assertEquals(Opcodes.IFEQ, sixthInstruction.getOpcode());

		LabelNode seventhInstruction = (LabelNode) instructions.get(6);

		assertNotEquals(label, seventhInstruction.getLabel());

		InsnNode eighthInstruction = (InsnNode) instructions.get(7);

		assertEquals(Opcodes.RETURN, eighthInstruction.getOpcode());
	}

	@Test
	public void testDelegatedInterfaces() throws Exception
	{
		Coordinate coordinate = new Coordinate(3, 6);
		Expression condition = new Nothing();

		Delegate delegate =
			new Delegate(coordinate, "MyClass", new Array<>(new Streeng("\"\"")));

		Statement statement = new FunctionCall(delegate);
		If ifStatement = new If(condition, statement);

		Type string = new ReferenceType(String.class);
		Map<Delegate, Type> delegatedTypes = new HashMap<>();
		delegatedTypes.put(delegate, string);

		Implementation delegatedInterfaces =
			ifStatement.getDelegatedInterfaces(delegatedTypes, string);

		assertTrue(delegatedInterfaces instanceof InterfaceImplementation);
	}
}
