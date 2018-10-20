package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;

import static org.junit.Assert.assertEquals;

public class LiteralTest
{
	@Test
	public void testCompilingStringLiteral()
	{
		MethodNode methodVisitor = new MethodNode();

		Literal literal = new Literal(new ReferenceType(String.class), "\"thingy\"");
		Variables variables = new Variables();

		Type type = literal.getType(variables);

		literal.compile(methodVisitor, variables);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(1, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals("thingy", firstInstruction.cst);

		assertEquals("java.lang.String", type.toFullyQualifiedType());
	}

	@Test
	public void testCompilingStringWithQuotes()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Literal literal = new Literal(new ReferenceType(String.class), "\"thi\"ngy\"");

		Type type = literal.getType(variables);

		literal.compile(methodVisitor, variables);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(1, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals("thi\"ngy", firstInstruction.cst);

		assertEquals("java.lang.String", type.toFullyQualifiedType());
	}

	@Test
	public void testCompilingZero()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Literal literal = new Literal(PrimitiveType.DOUBLE, "0");

		Type type = literal.getType(variables);

		literal.compile(methodVisitor, variables);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(1, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCONST_0, firstInstruction.getOpcode());
		assertEquals("java.lang.Double", type.toFullyQualifiedType());
	}

	@Test
	public void testCompilingOne()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Literal literal = new Literal(PrimitiveType.DOUBLE, "1");

		Type type = literal.getType(variables);

		literal.compile(methodVisitor, variables);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(1, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCONST_1, firstInstruction.getOpcode());
		assertEquals("java.lang.Double", type.toFullyQualifiedType());
	}

	@Test
	public void testCompilingInteger()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Literal literal = new Literal(PrimitiveType.DOUBLE, "2");

		Type type = literal.getType(variables);

		literal.compile(methodVisitor, variables);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(1, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(2.0, firstInstruction.cst);

		assertEquals("java.lang.Double", type.toFullyQualifiedType());
	}

	@Test
	public void testCompilingDecimal()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Literal literal = new Literal(PrimitiveType.DOUBLE, "0.5");

		Type type = literal.getType(variables);

		literal.compile(methodVisitor, variables);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(1, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(0.5, firstInstruction.cst);

		assertEquals("java.lang.Double", type.toFullyQualifiedType());
	}

	@Test
	public void testCompilingFalse()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Literal literal = new Literal(PrimitiveType.BOOLEAN, "false");

		Type type = literal.getType(variables);

		literal.compile(methodVisitor, variables);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(1, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_0, firstInstruction.getOpcode());

		assertEquals("java.lang.Boolean", type.toFullyQualifiedType());
	}

	@Test
	public void testCompilingTrue()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Literal literal = new Literal(PrimitiveType.BOOLEAN, "true");

		Type type = literal.getType(variables);

		literal.compile(methodVisitor, variables);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(1, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_1, firstInstruction.getOpcode());

		assertEquals("java.lang.Boolean", type.toFullyQualifiedType());
	}

	@Test
	public void testCompilingCharacter()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Literal literal = new Literal(PrimitiveType.CHAR, "'r'");

		Type type = literal.getType(variables);

		literal.compile(methodVisitor, variables);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(1, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals('r', firstInstruction.cst);

		assertEquals("java.lang.Character", type.toFullyQualifiedType());
	}
}
