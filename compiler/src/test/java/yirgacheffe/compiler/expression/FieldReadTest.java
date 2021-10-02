package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FieldReadTest
{
	@Test
	public void testCompilingFieldRead()
	{
		Coordinate coordinate = new Coordinate(3, 4);
		Variables variables = new LocalVariables(1, new HashMap<>(), new HashMap<>());

		Type owner = new ReferenceType(System.class);

		FieldRead fieldRead =
			new FieldRead(
				coordinate,
				new This(coordinate, owner),
				"out");

		Type type = fieldRead.getType(variables);
		Result result = fieldRead.compileCondition(variables, null, null);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(1, variables.getStack().length());
		assertEquals(0, result.getErrors().length());
		assertFalse(fieldRead.isCondition(variables));
		assertEquals(4, instructions.length());

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
		assertEquals("java/lang/System", fourthInstruction.owner);
		assertEquals("out", fourthInstruction.name);
		assertEquals("Ljava/io/PrintStream;", fourthInstruction.desc);

		assertEquals("java/io/PrintStream", type.toFullyQualifiedType());
	}

	@Test
	public void testGettingVariableReads()
	{
		Coordinate coordinate = new Coordinate(3, 6);
		VariableRead read = new VariableRead(coordinate, "myVariable");

		Expression fieldRead =
			new FieldRead(coordinate, read, "myField");

		Array<VariableRead> reads = fieldRead.getVariableReads();

		assertTrue(reads.indexOf(read) >= 0);
		assertEquals(coordinate, fieldRead.getCoordinate());
	}
}
