package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TryTest
{
	@Test
	public void testCompilingTry()
	{
		Variables variables = new LocalVariables(new HashMap<>());
		Try tryExpression = new Try(new Num("1"));

		Result result = tryExpression.compileCondition(variables, null, null);

		assertEquals(1, variables.getStack().length());
		assertFalse(tryExpression.isCondition(variables));
		assertEquals(0, result.getErrors().length());
		assertTrue(tryExpression.getType(null).isAssignableTo(PrimitiveType.INT));
		assertEquals(0, tryExpression.getVariableReads().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(5, instructions.length());

		LabelNode firstIsntruction = (LabelNode) instructions.get(0);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.ICONST_1, secondInstruction.getOpcode());

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKESTATIC, thirdInstruction.getOpcode());
		assertEquals("valueOf", thirdInstruction.name);

		LabelNode fourthInstruction = (LabelNode) instructions.get(3);
		LabelNode fifthInstruction = (LabelNode) instructions.get(4);
	}
}
