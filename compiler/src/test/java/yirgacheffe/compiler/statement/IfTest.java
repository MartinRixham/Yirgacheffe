package yirgacheffe.compiler.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.junit.Test;
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

public class IfTest
{
	@Test
	public void testIfStatement()
	{
		Expression condition = new Literal(PrimitiveType.BOOLEAN, "true");
		Coordinate coordinate = new Coordinate(3, 5);
		Statement statement = new Return(coordinate);
		If ifStatement = new If(condition, statement);
		MethodNode methodVisitor = new MethodNode();
		StatementResult result = new StatementResult();

		ifStatement.compile(methodVisitor, result);

		assertEquals(0, result.getErrors().length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(4, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_1, firstInstruction.getOpcode());

		JumpInsnNode secondInstruction = (JumpInsnNode) instructions.get(1);

		assertEquals(Opcodes.IFEQ, secondInstruction.getOpcode());

		Label label = secondInstruction.label.getLabel();

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.RETURN, thirdInstruction.getOpcode());

		LabelNode fourthInstruction = (LabelNode) instructions.get(3);

		assertEquals(label, fourthInstruction.getLabel());
	}
}
