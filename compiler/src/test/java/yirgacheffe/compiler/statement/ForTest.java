package yirgacheffe.compiler.statement;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Bool;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ForTest
{
	@Test
	public void testEmptyForStatement()
	{
		For forStatement =
			new For(new DoNothing(), new Nothing(), new DoNothing(), new Array<>());

		assertTrue(forStatement.getExpression() instanceof Nothing);
		assertFalse(forStatement.returns());
		assertFalse(forStatement.isEmpty());
	}

	@Test
	public void testExitConditionTrue()
	{
		Expression tru = new Bool("true");

		For forStatement = new For(new DoNothing(), tru, new DoNothing(), new Array<>());

		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Array<Error> errors = forStatement.compile(methodVisitor, variables, null);

		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

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
