package yirgacheffe.compiler.statement;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.junit.Test;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Literal;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;

import static org.junit.Assert.assertEquals;

public class ReturnTest
{
	@Test
	public void testVoidReturn()
	{
		Coordinate coordinate = new Coordinate(5, 3);
		Return returnStatement = new Return(coordinate);
		MethodNode methodVisitor = new MethodNode();
		StatementResult result = new StatementResult();

		returnStatement.compile(methodVisitor, result);

		assertEquals(0, result.getErrors().length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(1, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.RETURN, firstInstruction.getOpcode());
	}

	@Test
	public void testReturnNum()
	{
		Coordinate coordinate = new Coordinate(5, 3);
		Type returnType = PrimitiveType.DOUBLE;
		Expression expression = new Literal(returnType, "1");
		Return returnStatement = new Return(coordinate, returnType, expression);
		MethodNode methodVisitor = new MethodNode();
		StatementResult result = new StatementResult();

		returnStatement.compile(methodVisitor, result);

		assertEquals(0, result.getErrors().length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(2, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCONST_1, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DRETURN, secondInstruction.getOpcode());
	}
}
