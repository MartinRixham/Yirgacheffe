package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

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

		literal.compile(methodVisitor, variables);

		InsnList instructions = methodVisitor.instructions;

		assertEquals("thi\"ngy", literal.getValue());
		assertEquals(1, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals("thi\"ngy", firstInstruction.cst);

		assertEquals("java/lang/String", type.toFullyQualifiedType());
	}
}
