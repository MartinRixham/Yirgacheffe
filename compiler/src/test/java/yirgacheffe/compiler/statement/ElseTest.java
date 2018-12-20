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
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Literal;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class ElseTest
{
	@Test
	public void testElseStatement()
	{
		Expression condition = new Literal(PrimitiveType.BOOLEAN, "true");
		Coordinate coordinate = new Coordinate(3, 5);
		Statement statement = new Return(coordinate);
		If ifStatement = new If(condition, statement);
		Else elseStatement = new Else(coordinate, ifStatement, statement);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		StatementResult result = elseStatement.compile(methodVisitor, variables);

		assertEquals(0, result.getErrors().length());

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
		Coordinate coordinate = new Coordinate(3, 5);
		Statement statement = new Return(coordinate);
		Statement ifStatement = new Block(coordinate, new Array<>());
		Else elseStatement = new Else(coordinate, ifStatement, statement);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		StatementResult result = elseStatement.compile(methodVisitor, variables);

		assertFalse(elseStatement.returns());
		assertEquals(1, result.getErrors().length());
		assertEquals(
			"line 3:5 Else not preceded by if statement.",
			result.getErrors().get(0).toString());
	}

	@Test
	public void testElseHasNoFirstOperand()
	{
		Coordinate coordinate = new Coordinate(2, 4);
		Statement doNothing = new DoNothing();
		Else elseStatement = new Else(coordinate, doNothing, doNothing);

		assertTrue(elseStatement.getFirstOperand() instanceof Nothing);
	}

	@Test
	public void testGettingVariableReads()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		VariableRead read = new VariableRead("myVariable", coordinate);
		VariableWrite write = new VariableWrite(coordinate, "var", read);
		Statement elseStatement = new Else(coordinate, write, write);

		Array<VariableRead> reads = elseStatement.getVariableReads();

		assertTrue(reads.indexOf(read) >= 0);

		Array<VariableWrite> writes = elseStatement.getVariableWrites();

		assertTrue(writes.indexOf(write) >= 0);
		assertEquals(read, elseStatement.getFirstOperand());
	}
}
