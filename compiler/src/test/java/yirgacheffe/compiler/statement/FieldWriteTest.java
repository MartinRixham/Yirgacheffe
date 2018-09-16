package yirgacheffe.compiler.statement;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Literal;
import yirgacheffe.compiler.expression.This;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;

import static org.junit.Assert.assertEquals;

public class FieldWriteTest
{
	private String myField;

	private int one = 1;

	@Test
	public void testSuccessfulFieldWrite()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		Expression owner = new This(new ReferenceType(this.getClass()));
		Expression value = new Literal(new ReferenceType(String.class), "\"sumpt\"");
		FieldWrite fieldWrite = new FieldWrite(coordinate, "myField", owner, value);
		MethodNode methodVisitor = new MethodNode();
		StatementResult result = new StatementResult();

		fieldWrite.compile(methodVisitor, result);

		assertEquals(0, result.getErrors().length());

		InsnList instructions =  methodVisitor.instructions;

		assertEquals(3, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(Opcodes.LDC, secondInstruction.getOpcode());
		assertEquals("sumpt", secondInstruction.cst);

		FieldInsnNode thirdInstruction = (FieldInsnNode) instructions.get(2);

		assertEquals(Opcodes.PUTFIELD, thirdInstruction.getOpcode());
		assertEquals("myField", thirdInstruction.name);
		assertEquals("Ljava/lang/String;", thirdInstruction.desc);
		assertEquals(
			"yirgacheffe.compiler.statement.FieldWriteTest",
			thirdInstruction.owner);
	}

	@Test
	public void testAssignWrongTypeToPrimitiveField()
	{
		Coordinate coordinate = new Coordinate(6, 0);
		Expression owner = new This(new ReferenceType(this.getClass()));
		Expression value = new Literal(new ReferenceType(String.class), "\"one\"");
		FieldWrite fieldWrite = new FieldWrite(coordinate, "one", owner, value);
		MethodNode methodVisitor = new MethodNode();
		StatementResult result = new StatementResult();

		fieldWrite.compile(methodVisitor, result);

		assertEquals(1, result.getErrors().length());

		assertEquals(
			"line 6:0 Cannot assign expression of type " +
				"java.lang.String to field of type Num.",
			result.getErrors().get(0).toString());
	}

	@Test
	public void testAssignWrongTypeToStringField()
	{
		Coordinate coordinate = new Coordinate(6, 0);
		Expression owner = new This(new ReferenceType(this.getClass()));
		Expression value = new Literal(PrimitiveType.DOUBLE, "1");
		FieldWrite fieldWrite = new FieldWrite(coordinate, "myField", owner, value);
		MethodNode methodVisitor = new MethodNode();
		StatementResult result = new StatementResult();

		fieldWrite.compile(methodVisitor, result);

		assertEquals(1, result.getErrors().length());

		assertEquals(
			"line 6:0 Cannot assign expression of type " +
				"Num to field of type java.lang.String.",
			result.getErrors().get(0).toString());
	}
}
