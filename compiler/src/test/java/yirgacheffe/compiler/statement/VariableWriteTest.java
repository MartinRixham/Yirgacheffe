package yirgacheffe.compiler.statement;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.junit.Test;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.InvalidExpression;
import yirgacheffe.compiler.expression.Literal;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;

import static org.junit.Assert.assertEquals;

public class VariableWriteTest
{
	@Test
	public void testVariableWrite()
	{
		Expression value = new Literal(new ReferenceType(String.class), "\"sumpt\"");
		Coordinate coordinate = new Coordinate(4, 2);
		VariableWrite variableWrite =
			new VariableWrite("myVariable", value, coordinate);
		MethodNode methodVisitor = new MethodNode();

		Variables variables = new Variables();
		variables.declare("myVariable", new ReferenceType(String.class));

		StatementResult result = variableWrite.compile(methodVisitor, variables);

		assertEquals(0, result.getErrors().length());

		InsnList instructions =  methodVisitor.instructions;

		assertEquals(2, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals("sumpt", firstInstruction.cst);

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.ASTORE, secondInstruction.getOpcode());
		assertEquals(1, secondInstruction.var);
	}

	@Test
	public void testWriteInvalidExpression()
	{
		Coordinate coordinate = new Coordinate(4, 2);
		Type string = new ReferenceType(String.class);
		Expression value = new InvalidExpression(string);
		VariableWrite variableWrite =
			new VariableWrite("myVariable", value, coordinate);
		MethodNode methodVisitor = new MethodNode();

		Variables variables = new Variables();
		variables.declare("myVariable", string);

		StatementResult result = variableWrite.compile(methodVisitor, variables);

		assertEquals(1, result.getErrors().length());
	}
}
