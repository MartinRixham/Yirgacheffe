package yirgacheffe.compiler.statement;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.InvalidExpression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.Num;
import yirgacheffe.compiler.expression.Streeng;
import yirgacheffe.compiler.expression.This;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FieldWriteTest
{
	private String myField;

	private int one = 1;

	@Test
	public void testSuccessfulFieldWrite()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Coordinate coordinate = new Coordinate(3, 5);
		Expression owner = new This(new ReferenceType(this.getClass()));
		Expression value = new Streeng("\"sumpt\"");
		FieldWrite fieldWrite = new FieldWrite(coordinate, "myField", owner, value);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());

		Array<Error> errors = fieldWrite.compile(methodVisitor, variables, caller);

		assertEquals(0, errors.length());

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
			"yirgacheffe/compiler/statement/FieldWriteTest",
			thirdInstruction.owner);
	}

	@Test
	public void testAssignWrongTypeToPrimitiveField()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Coordinate coordinate = new Coordinate(6, 0);
		Expression owner = new This(new ReferenceType(this.getClass()));
		Expression value = new Streeng("\"one\"");
		FieldWrite fieldWrite = new FieldWrite(coordinate, "one", owner, value);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());

		Array<Error> errors = fieldWrite.compile(methodVisitor, variables, caller);

		assertFalse(fieldWrite.returns());
		assertEquals(1, errors.length());
		assertEquals(
			"line 6:0 Cannot assign expression of type " +
				"java.lang.String to field of type Num.",
			errors.get(0).toString());
	}

	@Test
	public void testAssignWrongTypeToStringField()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Coordinate coordinate = new Coordinate(6, 0);
		Expression owner = new This(new ReferenceType(this.getClass()));
		Expression value = new Num("1.0");
		FieldWrite fieldWrite = new FieldWrite(coordinate, "myField", owner, value);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());

		Array<Error> errors = fieldWrite.compile(methodVisitor, variables, caller);

		assertEquals(1, errors.length());

		assertEquals(
			"line 6:0 Cannot assign expression of type " +
				"Num to field of type java.lang.String.",
			errors.get(0).toString());
	}

	@Test
	public void testAssignInvalidExpressionToInvalidExpression()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Coordinate coordinate = new Coordinate(6, 0);
		Type testClass = new ReferenceType(this.getClass());
		Type string = new ReferenceType(String.class);
		Expression owner = new InvalidExpression(testClass);
		Expression value = new InvalidExpression(string);
		FieldWrite fieldWrite = new FieldWrite(coordinate, "myField", owner, value);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());

		Array<Error> errors = fieldWrite.compile(methodVisitor, variables, caller);

		assertEquals(2, errors.length());
	}

	@Test
	public void testFieldWriteHasFirstOperand()
	{
		Coordinate coordinate = new Coordinate(3, 6);
		Num one = new Num("1.0");
		Num two = new Num("2");
		FieldWrite fieldWrite = new FieldWrite(coordinate, "field", one, two);

		assertTrue(fieldWrite.getExpression() instanceof Nothing);
		assertFalse(fieldWrite.isEmpty());
	}

	@Test
	public void testGettingVariableReads()
	{
		Coordinate coordinate = new Coordinate(3, 6);
		VariableRead firstOperand = new VariableRead(coordinate, "myVariable");
		VariableRead secondOperand = new VariableRead(coordinate, "myVariable");

		Statement write =
			new FieldWrite(coordinate, "var", firstOperand, secondOperand);

		Array<VariableRead> reads = write.getVariableReads();

		assertTrue(reads.indexOf(firstOperand) >= 0);
		assertTrue(reads.indexOf(secondOperand) >= 0);

		Array<VariableWrite> writes = write.getVariableWrites();

		assertEquals(0, writes.length());
	}
}
