package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;

public class DivisionTest
{
	@Test
	public void testCompilingDivision()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();
		Coordinate coordinate = new Coordinate(3,  6);
		Literal firstOperand = new Literal(PrimitiveType.DOUBLE, "3");
		Literal secondOperand = new Literal(PrimitiveType.DOUBLE, "2");
		Division division = new Division(coordinate, firstOperand, secondOperand);

		Type type = division.getType(variables);

		Array<Error> errors = division.compile(methodVisitor, variables);

		assertEquals(PrimitiveType.DOUBLE, type);
		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(3, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(3.0, firstInstruction.cst);

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(Opcodes.LDC, secondInstruction.getOpcode());
		assertEquals(2.0, secondInstruction.cst);

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DDIV, thirdInstruction.getOpcode());
	}

	@Test
	public void testRemainderOfWrongType()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();
		Coordinate coordinate = new Coordinate(3,  6);
		Literal firstOperand = new Literal(PrimitiveType.DOUBLE, "3");
		This secondOperand = new This(new ReferenceType(String.class));
		Division division = new Division(coordinate, firstOperand, secondOperand);

		Type type = division.getType(variables);

		Array<Error> errors = division.compile(methodVisitor, variables);

		assertEquals(PrimitiveType.DOUBLE, type);
		assertEquals(1, errors.length());

		assertEquals(errors.get(0).toString(),
			"line 3:6 Cannot divide Num and java.lang.String.");
	}
}
