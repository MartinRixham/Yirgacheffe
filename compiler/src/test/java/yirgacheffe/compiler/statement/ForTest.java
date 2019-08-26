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
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.Type;
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
		assertEquals(0, forStatement.getFieldAssignments().length());

		Array<Type> delegatedInterfaces =
			forStatement.getDelegatedInterfaces(new HashMap<>(), new NullType());

		assertEquals(0, delegatedInterfaces.length());
	}

	@Test
	public void testExitConditionTrue()
	{
		Expression tru = new Bool("true");
		Coordinate coordinate = new Coordinate(3, 4);
		Block block = new Block(coordinate, new Array<>());

		For forStatement = new For(new DoNothing(), tru, new DoNothing(), block);

		LocalVariables variables = new LocalVariables(new HashMap<>());
		Result result = forStatement.compile(variables, null);

		assertEquals(0, variables.getStack().length());
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		LabelNode firstInstruction = (LabelNode) instructions.get(0);

		Label continueLabel = firstInstruction.getLabel();

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.ICONST_1, secondInstruction.getOpcode());

		JumpInsnNode thirdInstruction = (JumpInsnNode) instructions.get(2);

		assertEquals(Opcodes.IFEQ, thirdInstruction.getOpcode());

		Label exitLabel = thirdInstruction.label.getLabel();

		JumpInsnNode fourthInstruction = (JumpInsnNode) instructions.get(3);

		assertEquals(Opcodes.GOTO, fourthInstruction.getOpcode());
		assertEquals(continueLabel, fourthInstruction.label.getLabel());

		LabelNode fifthInstruction = (LabelNode) instructions.get(4);

		assertEquals(exitLabel, fifthInstruction.getLabel());
	}
}
