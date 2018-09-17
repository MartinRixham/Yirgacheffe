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
import yirgacheffe.compiler.type.PrimitiveType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ElseTest
{
	@Test
	public void testElseStatement()
	{
		Expression condition = new Literal(PrimitiveType.BOOLEAN, "true");
		Coordinate coordinate = new Coordinate(3, 5);
		Statement statement = new Return(coordinate);
		If ifStatement = new If(condition, statement);
		Else elseStatement = new Else(ifStatement, statement);
		MethodNode methodVisitor = new MethodNode();
		StatementResult result = new StatementResult();

		elseStatement.compile(methodVisitor, result);

		assertEquals(0, result.getErrors().length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(7, instructions.size());

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

		LabelNode seventhInstruction = (LabelNode) instructions.get(6);

		assertEquals(elseLabel, seventhInstruction.getLabel());

		assertNotEquals(ifLabel, elseLabel);
	}
}
