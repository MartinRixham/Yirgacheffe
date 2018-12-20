package yirgacheffe.compiler.statement;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.junit.Test;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Literal;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReturnTest
{
	@Test
	public void testVoidReturn()
	{
		Coordinate coordinate = new Coordinate(5, 3);
		Return returnStatement = new Return(coordinate);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Array<Error> errors = returnStatement.compile(methodVisitor, variables);

		assertTrue(returnStatement.returns());
		assertEquals(0, errors.length());

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
		Variables variables = new Variables();

		Array<Error> errors = returnStatement.compile(methodVisitor, variables);

		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(2, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCONST_1, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DRETURN, secondInstruction.getOpcode());
	}

	@Test
	public void testFailToReturnString()
	{
		Coordinate coordinate = new Coordinate(5, 3);
		Type returnType = new ReferenceType(String.class);
		Expression expression = new Literal(PrimitiveType.DOUBLE, "1");
		Return returnStatement = new Return(coordinate, returnType, expression);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Array<Error> errors = returnStatement.compile(methodVisitor, variables);

		assertEquals(1, errors.length());

		assertEquals(
			"line 5:3 Mismatched return type: " +
				"Cannot return expression of type Num " +
				"from method of return type java.lang.String.",
			errors.get(0).toString());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(2, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ACONST_NULL, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.ARETURN, secondInstruction.getOpcode());
	}

	@Test
	public void testFailToReturnBoolean()
	{
		Coordinate coordinate = new Coordinate(5, 3);
		Type returnType = PrimitiveType.BOOLEAN;
		Expression expression = new Literal(PrimitiveType.DOUBLE, "1");
		Return returnStatement = new Return(coordinate, returnType, expression);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Array<Error> errors = returnStatement.compile(methodVisitor, variables);

		assertEquals(1, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(2, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_0, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.IRETURN, secondInstruction.getOpcode());
	}

	@Test
	public void testFailToReturnDouble()
	{
		Coordinate coordinate = new Coordinate(5, 3);
		Type returnType = PrimitiveType.DOUBLE;
		Expression expression = new Literal(PrimitiveType.BOOLEAN, "1");
		Return returnStatement = new Return(coordinate, returnType, expression);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Array<Error> errors = returnStatement.compile(methodVisitor, variables);

		assertEquals(1, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(2, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCONST_0, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DRETURN, secondInstruction.getOpcode());
	}
}
