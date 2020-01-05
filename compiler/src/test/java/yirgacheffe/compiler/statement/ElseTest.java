package yirgacheffe.compiler.statement;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.Bool;
import yirgacheffe.compiler.expression.Delegate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.InvalidExpression;
import yirgacheffe.compiler.expression.InvokeThis;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.Streeng;
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

public class ElseTest
{
	@Test
	public void testElseStatement()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Expression condition = new Bool(coordinate, "true");
		Statement statement = new Return(coordinate, PrimitiveType.VOID);
		If ifStatement = new If(condition, statement);
		Else elseStatement = new Else(coordinate, ifStatement, statement);
		LocalVariables variables = new LocalVariables(1, new HashMap<>());

		Result result = elseStatement.compile(variables, caller);

		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(0, variables.getStack().length());
		assertEquals(7, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_1, firstInstruction.getOpcode());

		JumpInsnNode secondInstruction = (JumpInsnNode) instructions.get(1);
		Label ifLabel = secondInstruction.label.getLabel();

		assertEquals(Opcodes.IFEQ, secondInstruction.getOpcode());

		LabelNode thirdInstruction = (LabelNode) instructions.get(2);

		assertNotEquals(ifLabel, thirdInstruction.getLabel());

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.RETURN, fourthInstruction.getOpcode());

		JumpInsnNode fifthInstruction = (JumpInsnNode) instructions.get(4);

		assertEquals(Opcodes.GOTO, fifthInstruction.getOpcode());

		Label elseLabel = fifthInstruction.label.getLabel();

		LabelNode sixthInstruction = (LabelNode) instructions.get(5);

		assertEquals(ifLabel, sixthInstruction.getLabel());

		InsnNode seventhInstruction = (InsnNode) instructions.get(6);

		assertEquals(Opcodes.RETURN, seventhInstruction.getOpcode());

		assertNotEquals(ifLabel, elseLabel);
	}

	@Test
	public void testElseNotPrecededByIf()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Coordinate coordinate = new Coordinate(3, 5);
		Statement statement = new Return(coordinate, PrimitiveType.VOID);
		Statement ifStatement = new Block(coordinate, new Array<>());
		Else elseStatement = new Else(coordinate, ifStatement, statement);
		LocalVariables variables = new LocalVariables(1, new HashMap<>());

		Result result = elseStatement.compile(variables, caller);

		assertFalse(elseStatement.returns());
		assertEquals(1, result.getErrors().length());
		assertEquals(
			"line 3:5 Else not preceded by if statement.",
			result.getErrors().get(0).toString());
	}

	@Test
	public void testInvalidPrecondition()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Coordinate coordinate = new Coordinate(2, 4);
		LocalVariables variables = new LocalVariables(1, new HashMap<>());

		If ifStatement =
			new If(new InvalidExpression(PrimitiveType.BOOLEAN), new DoNothing());

		Else elseStatement = new Else(coordinate, ifStatement, new DoNothing());

		Result result = elseStatement.compile(variables, caller);

		assertEquals(1, result.getErrors().length());
	}

	@Test
	public void testInvalidStatement()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Coordinate coordinate = new Coordinate(2, 4);
		LocalVariables variables = new LocalVariables(1, new HashMap<>());

		If precondition =
			new If(new Nothing(), new DoNothing());

		If ifStatement =
			new If(new InvalidExpression(PrimitiveType.BOOLEAN), new DoNothing());

		Else elseStatement = new Else(coordinate, precondition, ifStatement);

		Result result = elseStatement.compile(variables, caller);

		assertEquals(1, result.getErrors().length());
	}

	@Test
	public void testGettingVariableReads()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		VariableRead read = new VariableRead(coordinate, "myVariable");
		VariableWrite write = new VariableWrite(coordinate, "var", read);
		Statement elseStatement = new Else(coordinate, write, write);

		Array<VariableRead> reads = elseStatement.getVariableReads();

		assertTrue(reads.indexOf(read) >= 0);

		Array<VariableWrite> writes = elseStatement.getVariableWrites();

		assertTrue(writes.indexOf(write) >= 0);
		assertTrue(elseStatement.getExpression() instanceof Nothing);
		assertFalse(elseStatement.isEmpty());
		assertFalse(elseStatement.getFieldAssignments().contains("var"));
	}

	@Test
	public void testFieldAssignmentFromPrecondition()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		Type string = new ReferenceType(String.class);

		FieldWrite precondition =
			new FieldWrite(coordinate, "var1", new Nothing(), new Nothing());

		FunctionCall statement =
			new FunctionCall(new InvokeThis(coordinate, string, new Array<>()));

		Statement elseStatement = new Else(coordinate, precondition, statement);

		assertTrue(elseStatement.getFieldAssignments().contains("var1"));
	}

	@Test
	public void testFieldAssignmentFromStatement()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		Type string = new ReferenceType(String.class);

		FunctionCall precondition =
			new FunctionCall(new InvokeThis(coordinate, string, new Array<>()));

		FieldWrite statement =
			new FieldWrite(coordinate, "var2", new Nothing(), new Nothing());

		Statement elseStatement = new Else(coordinate, precondition, statement);

		assertTrue(elseStatement.getFieldAssignments().contains("var2"));
	}

	@Test
	public void testFieldAssignmentFromStatementAndPrecondition()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		Type string = new ReferenceType(String.class);

		Statement precondition =
			new Block(
				coordinate,
				new Array<>(
					new FieldWrite(coordinate, "var1", new Nothing(), new Nothing()),
					new FieldWrite(coordinate, "var2", new Nothing(), new Nothing())));

		Statement statement =
			new Block(
				coordinate,
				new Array<>(
					new FieldWrite(coordinate, "var2", new Nothing(), new Nothing()),
					new FieldWrite(coordinate, "var3", new Nothing(), new Nothing())));

		Statement elseStatement = new Else(coordinate, precondition, statement);

		assertFalse(elseStatement.getFieldAssignments().contains("var1"));
		assertTrue(elseStatement.getFieldAssignments().contains("var2"));
		assertFalse(elseStatement.getFieldAssignments().contains("var3"));
	}

	@Test
	public void testDelegatedInterfaces()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		Expression condition = new Nothing();

		Delegate delegate =
			new Delegate(
				coordinate, "MyClass", new Array<>(new Streeng(coordinate, "\"\"")));

		Statement statement = new FunctionCall(delegate);
		If ifStatement = new If(condition, statement);
		Else elseStatement = new Else(coordinate, ifStatement, statement);

		Type string = new ReferenceType(String.class);
		Map<Delegate, Type> delegatedTypes = new HashMap<>();
		delegatedTypes.put(delegate, string);

		Implementation delegatedInterfaces =
			elseStatement.getDelegatedInterfaces(delegatedTypes, string);

		assertTrue(delegatedInterfaces instanceof InterfaceImplementation);
	}
}
