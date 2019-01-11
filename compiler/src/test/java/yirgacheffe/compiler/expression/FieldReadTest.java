package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FieldReadTest
{
	@Test
	public void testCompilingFieldRead()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Type owner = new ReferenceType(String.class);

		FieldRead fieldRead =
			new FieldRead(
				new This(owner),
				"length",
				PrimitiveType.DOUBLE);

		Type type = fieldRead.getType(variables);

		fieldRead.compile(methodVisitor, variables);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(2, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		FieldInsnNode secondInstruction = (FieldInsnNode) instructions.get(1);

		assertEquals(Opcodes.GETFIELD, secondInstruction.getOpcode());
		assertEquals("java/lang/String", secondInstruction.owner);
		assertEquals("length", secondInstruction.name);
		assertEquals("D", secondInstruction.desc);

		assertEquals("java.lang.Double", type.toFullyQualifiedType());
	}

	@Test
	public void testGettingVariableReads()
	{
		Coordinate coordinate = new Coordinate(3, 6);
		VariableRead read = new VariableRead(coordinate, "myVariable");

		Expression fieldRead = new FieldRead(read, "myField", PrimitiveType.DOUBLE);

		Array<VariableRead> reads = fieldRead.getVariableReads();

		assertTrue(reads.indexOf(read) >= 0);
	}
}
