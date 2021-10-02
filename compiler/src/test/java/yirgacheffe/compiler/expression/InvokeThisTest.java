package yirgacheffe.compiler.expression;

import org.objectweb.asm.Opcodes;
import org.junit.Test;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InvokeThisTest
{
	@Test
	public void testCompilation()
	{
		Coordinate coordinate = new Coordinate(3, 3);
		Type string = new ReferenceType(String.class);
		Array<Expression> arguments = new Array<>(new Streeng(coordinate, "\"thingy\""));
		InvokeThis invokeThis = new InvokeThis(coordinate, string, arguments);
		Variables variables = new LocalVariables(1, new HashMap<>(), new HashMap<>());

		Result result = invokeThis.compileCondition(variables, null, null);

		assertEquals(0, result.getErrors().length());
		assertFalse(invokeThis.isCondition(variables));
		assertEquals(coordinate, invokeThis.getCoordinate());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(5, instructions.length());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(Opcodes.LDC, secondInstruction.getOpcode());
		assertEquals("thingy", secondInstruction.cst);

		assertTrue(instructions.get(2) instanceof LabelNode);
		assertTrue(instructions.get(3) instanceof LineNumberNode);

		InvokeDynamicInsnNode fifthInstruction =
			(InvokeDynamicInsnNode) instructions.get(4);

		assertEquals(Opcodes.INVOKEDYNAMIC, fifthInstruction.getOpcode());
		assertEquals("0this", fifthInstruction.name);
		assertEquals("(Ljava/lang/String;Ljava/lang/String;)V", fifthInstruction.desc);
	}
}
