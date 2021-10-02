package yirgacheffe.compiler.statement;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.Bool;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.implementation.NullImplementation;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ForTest
{
	@Test
	public void testEmptyForStatement()
	{
		Coordinate coordinate = new Coordinate(3, 4);
		Block block = new Block(coordinate, new Array<>());

		For forStatement =
			new For(new DoNothing(), new Nothing(), new DoNothing(), block);

		assertTrue(forStatement.getExpression() instanceof Nothing);
		assertFalse(forStatement.returns());
		assertFalse(forStatement.isEmpty());
		assertFalse(forStatement.getFieldAssignments().contains(""));

		Implementation delegatedInterfaces =
			forStatement.getDelegatedInterfaces(new HashMap<>(), new NullType());

		assertTrue(delegatedInterfaces instanceof NullImplementation);
	}

	@Test
	public void testExitConditionTrue()
	{
		Coordinate coordinate = new Coordinate(3, 4);
		Expression tru = new Bool(coordinate, "true");
		Block block = new Block(coordinate, new Array<>());

		For forStatement = new For(new DoNothing(), tru, new DoNothing(), block);

		LocalVariables variables =
			new LocalVariables(1, new HashMap<>(), new HashMap<>());

		Result result = forStatement.compile(variables, null);

		assertEquals(0, variables.getStack().length());
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(2, instructions.length());

		LabelNode firstInstruction = (LabelNode) instructions.get(0);

		Label continueLabel = firstInstruction.getLabel();

		JumpInsnNode secondInstruction = (JumpInsnNode) instructions.get(1);

		assertEquals(Opcodes.GOTO, secondInstruction.getOpcode());
		assertEquals(continueLabel, secondInstruction.label.getLabel());
	}

	@Test
	public void testExitConditionFalse()
	{
		Coordinate coordinate = new Coordinate(3, 4);
		Expression fals = new Bool(coordinate, "false");
		Block block = new Block(coordinate, new Array<>());

		For forStatement = new For(new DoNothing(), fals, new DoNothing(), block);

		LocalVariables variables =
			new LocalVariables(1, new HashMap<>(), new HashMap<>());

		Result result = forStatement.compile(variables, null);

		assertEquals(0, variables.getStack().length());
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(2, instructions.length());

		JumpInsnNode thirdInstruction = (JumpInsnNode) instructions.get(0);

		assertEquals(Opcodes.GOTO, thirdInstruction.getOpcode());

		Label exitLabel = thirdInstruction.label.getLabel();

		LabelNode fifthInstruction = (LabelNode) instructions.get(1);

		assertEquals(exitLabel, fifthInstruction.getLabel());
	}
}
