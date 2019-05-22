package yirgacheffe.compiler.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.junit.Test;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Bool;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.InvalidExpression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IfTest
{
	@Test
	public void testIfStatement()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Expression condition = new Bool("true");
		Coordinate coordinate = new Coordinate(3, 5);
		Statement statement = new Return(coordinate, PrimitiveType.VOID);
		If ifStatement = new If(condition, statement);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());

		Array<Error> errors = ifStatement.compile(methodVisitor, variables, caller);

		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(3, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_1, firstInstruction.getOpcode());

		JumpInsnNode secondInstruction = (JumpInsnNode) instructions.get(1);

		assertEquals(Opcodes.IFEQ, secondInstruction.getOpcode());

		Label label = secondInstruction.label.getLabel();

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.RETURN, thirdInstruction.getOpcode());
	}

	@Test
	public void testInvalidCondition()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());

		If ifStatement =
			new If(new InvalidExpression(PrimitiveType.BOOLEAN), new DoNothing());

		Array<Error> errors = ifStatement.compile(methodVisitor, variables, caller);

		assertEquals(1, errors.length());
		assertFalse(ifStatement.isEmpty());
	}

	@Test
	public void testInvalidStatement()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());

		If statement =
			new If(new InvalidExpression(PrimitiveType.BOOLEAN), new DoNothing());

		If ifStatement =
			new If(new Nothing(), statement);

		Array<Error> errors = ifStatement.compile(methodVisitor, variables, caller);

		assertEquals(1, errors.length());
	}

	@Test
	public void testGettingVariableReads()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		VariableRead read = new VariableRead(coordinate, "myVariable");
		VariableWrite write = new VariableWrite(coordinate, "var", read);
		Statement ifStatement = new If(read, write);

		Array<VariableRead> reads = ifStatement.getVariableReads();

		assertTrue(reads.indexOf(read) >= 0);

		Array<VariableWrite> writes = ifStatement.getVariableWrites();

		assertTrue(writes.indexOf(write) >= 0);
		assertTrue(ifStatement.getExpression() instanceof Nothing);
	}
}
