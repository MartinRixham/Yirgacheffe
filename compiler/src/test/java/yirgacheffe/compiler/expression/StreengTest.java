package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class StreengTest
{
	@Test
	public void testCompilingStringLiteral()
	{
		MethodNode methodVisitor = new MethodNode();

		Streeng literal = new Streeng("\"thingy\"");
		Variables variables = new Variables(new HashMap<>());

		Type type = literal.getType(variables);

		literal.compile(methodVisitor, variables);

		InsnList instructions = methodVisitor.instructions;

		assertFalse(literal.isCondition(variables));
		assertEquals("thingy", literal.getValue());
		assertEquals(1, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals("thingy", firstInstruction.cst);

		assertEquals("java/lang/String", type.toFullyQualifiedType());
	}

	@Test
	public void testCompilingStringWithQuotes()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());

		Streeng literal = new Streeng("\"thi\"ngy\"");

		Type type = literal.getType(variables);

		Array<Error> errors =
			literal.compileCondition(methodVisitor, variables, new Label());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(0, errors.length());
		assertEquals("thi\"ngy", literal.getValue());
		assertEquals(1, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals("thi\"ngy", firstInstruction.cst);

		assertEquals("java/lang/String", type.toFullyQualifiedType());
	}
}
