package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
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
		Coordinate coordinate = new Coordinate(3, 4);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Type owner = new ReferenceType(String.class);

		FieldRead fieldRead =
			new FieldRead(
				coordinate,
				new This(owner),
				"length",
				PrimitiveType.DOUBLE);

		Type type = fieldRead.getType(variables);

		fieldRead.compile(methodVisitor, variables);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(4, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		LabelNode secondInstruction = (LabelNode) instructions.get(1);
		Label label = secondInstruction.getLabel();

		LineNumberNode thirdInstruction = (LineNumberNode) instructions.get(2);

		assertEquals(3, thirdInstruction.line);
		assertEquals(label, thirdInstruction.start.getLabel());

		FieldInsnNode fourthInstruction = (FieldInsnNode) instructions.get(3);

		assertEquals(Opcodes.GETFIELD, fourthInstruction.getOpcode());
		assertEquals("java/lang/String", fourthInstruction.owner);
		assertEquals("length", fourthInstruction.name);
		assertEquals("D", fourthInstruction.desc);

		assertEquals("java.lang.Double", type.toFullyQualifiedType());
	}

	@Test
	public void testGettingVariableReads()
	{
		Coordinate coordinate = new Coordinate(3, 6);
		VariableRead read = new VariableRead(coordinate, "myVariable");

		Expression fieldRead =
			new FieldRead(coordinate, read, "myField", PrimitiveType.DOUBLE);

		Array<VariableRead> reads = fieldRead.getVariableReads();

		assertTrue(reads.indexOf(read) >= 0);
	}
}
