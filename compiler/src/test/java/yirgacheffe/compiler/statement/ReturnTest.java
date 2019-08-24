package yirgacheffe.compiler.statement;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.junit.Test;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.Bool;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.Num;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReturnTest
{
	@Test
	public void testVoidReturn()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Coordinate coordinate = new Coordinate(5, 3);
		Return returnStatement = new Return(coordinate, PrimitiveType.VOID);
		LocalVariables variables = new LocalVariables(new HashMap<>());
		Result result = returnStatement.compile(variables, caller);

		assertEquals(0, variables.getStack().length());
		assertTrue(returnStatement.returns());
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(1, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.RETURN, firstInstruction.getOpcode());
		assertTrue(returnStatement.getExpression() instanceof Nothing);
		assertEquals(0, returnStatement.getFieldAssignments().length());
	}

	@Test
	public void testVoidReturnWithNumReturnType()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Coordinate coordinate = new Coordinate(5, 3);
		Return returnStatement = new Return(coordinate, PrimitiveType.DOUBLE);
		LocalVariables variables = new LocalVariables(new HashMap<>());
		Result result = returnStatement.compile(variables, caller);

		assertTrue(returnStatement.returns());
		assertEquals(1, result.getErrors().length());
		assertEquals(
			"line 5:3 Mismatched return type: Cannot return expression of type Void " +
			"from method of return type Num.",
			result.getErrors().get(0).toString());
	}

	@Test
	public void testReturnNum()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Coordinate coordinate = new Coordinate(5, 3);
		Expression expression = new Num("1.0");
		Return returnStatement = new Return(coordinate, PrimitiveType.DOUBLE, expression);
		LocalVariables variables = new LocalVariables(new HashMap<>());
		Result result = returnStatement.compile(variables, caller);

		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(2, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCONST_1, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DRETURN, secondInstruction.getOpcode());
	}

	@Test
	public void testFailToReturnString()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Coordinate coordinate = new Coordinate(5, 3);
		Type returnType = new ReferenceType(String.class);
		Expression expression = new Num("1.0");
		Return returnStatement = new Return(coordinate, returnType, expression);
		LocalVariables variables = new LocalVariables(new HashMap<>());
		Result result = returnStatement.compile(variables, caller);

		assertEquals(1, result.getErrors().length());

		assertEquals(
			"line 5:3 Mismatched return type: " +
				"Cannot return expression of type Num " +
				"from method of return type java.lang.String.",
			result.getErrors().get(0).toString());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(2, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ACONST_NULL, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.ARETURN, secondInstruction.getOpcode());
	}

	@Test
	public void testFailToReturnBoolean()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Coordinate coordinate = new Coordinate(5, 3);
		Type returnType = PrimitiveType.BOOLEAN;
		Expression expression = new Num("1.0");
		Return returnStatement = new Return(coordinate, returnType, expression);
		LocalVariables variables = new LocalVariables(new HashMap<>());
		Result result = returnStatement.compile(variables, caller);

		assertEquals(1, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(2, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_0, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.IRETURN, secondInstruction.getOpcode());
	}

	@Test
	public void testFailToReturnDouble()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Coordinate coordinate = new Coordinate(5, 3);
		Expression expression = new Bool("1");
		Return returnStatement = new Return(coordinate, PrimitiveType.DOUBLE, expression);
		LocalVariables variables = new LocalVariables(new HashMap<>());
		Result result = returnStatement.compile(variables, caller);

		assertEquals(1, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(2, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCONST_0, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DRETURN, secondInstruction.getOpcode());
	}

	@Test
	public void testReturnEqualToExpression()
	{
		Coordinate coordinate = new Coordinate(2, 3);
		Expression expression = new Num("1.0");

		Return returnStatement = new Return(coordinate, PrimitiveType.DOUBLE, expression);

		assertEquals(returnStatement, expression);
		assertEquals(returnStatement.hashCode(), expression.hashCode());
	}
}
