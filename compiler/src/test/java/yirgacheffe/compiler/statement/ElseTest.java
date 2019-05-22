package yirgacheffe.compiler.statement;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Bool;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.InvalidExpression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class ElseTest
{
	@Test
	public void testElseStatement()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Expression condition = new Bool("true");
		Coordinate coordinate = new Coordinate(3, 5);
		Statement statement = new Return(coordinate, PrimitiveType.VOID);
		If ifStatement = new If(condition, statement);
		Else elseStatement = new Else(coordinate, ifStatement, statement);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());

		Array<Error> errors = elseStatement.compile(methodVisitor, variables, caller);

		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(6, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_1, firstInstruction.getOpcode());

		JumpInsnNode secondInstruction = (JumpInsnNode) instructions.get(1);

		assertEquals(Opcodes.IFEQ, secondInstruction.getOpcode());

		Label ifLabel = secondInstruction.label.getLabel();

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.RETURN, thirdInstruction.getOpcode());

		JumpInsnNode fourthInstruction = (JumpInsnNode) instructions.get(3);

		assertEquals(Opcodes.GOTO, fourthInstruction.getOpcode());

		Label elseLabel = fourthInstruction.label.getLabel();

		LabelNode fifthInstruction = (LabelNode) instructions.get(4);

		assertEquals(ifLabel, fifthInstruction.getLabel());

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.RETURN, sixthInstruction.getOpcode());

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
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());

		Array<Error> errors = elseStatement.compile(methodVisitor, variables, caller);

		assertFalse(elseStatement.returns());
		assertEquals(1, errors.length());
		assertEquals(
			"line 3:5 Else not preceded by if statement.",
			errors.get(0).toString());
	}

	@Test
	public void testInvalidPrecondition()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Coordinate coordinate = new Coordinate(2, 4);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());

		If ifStatement =
			new If(new InvalidExpression(PrimitiveType.BOOLEAN), new DoNothing());

		Else elseStatement = new Else(coordinate, ifStatement, new DoNothing());

		Array<Error> errors = elseStatement.compile(methodVisitor, variables, caller);

		assertEquals(1, errors.length());
	}

	@Test
	public void testInvalidStatement()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Coordinate coordinate = new Coordinate(2, 4);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());

		If precondition =
			new If(new Nothing(), new DoNothing());

		If ifStatement =
			new If(new InvalidExpression(PrimitiveType.BOOLEAN), new DoNothing());

		Else elseStatement = new Else(coordinate, precondition, ifStatement);

		Array<Error> errors = elseStatement.compile(methodVisitor, variables, caller);

		assertEquals(1, errors.length());
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
	}
}
