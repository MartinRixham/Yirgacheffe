package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;

import static org.junit.Assert.assertEquals;

public class NumTest
{
	@Test
	public void testCompilingZero()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Num literal = new Num("0.0");

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

		Num literal = new Num("1.0");

		Type type = literal.getType(variables);

		literal.compile(methodVisitor, variables);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(1, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCONST_1, firstInstruction.getOpcode());
		assertEquals("java.lang.Double", type.toFullyQualifiedType());
	}
	@Test
	public void testCompilingIntegerZero()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Num literal = new Num("0");

		Type type = literal.getType(variables);

		literal.compile(methodVisitor, variables);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(1, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_0, firstInstruction.getOpcode());
		assertEquals("java.lang.Integer", type.toFullyQualifiedType());
	}

	@Test
	public void testCompilingIntegerOne()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Num literal = new Num("1");

		Type type = literal.getType(variables);

		literal.compile(methodVisitor, variables);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(1, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_1, firstInstruction.getOpcode());
		assertEquals("java.lang.Integer", type.toFullyQualifiedType());
	}

	@Test
	public void testCompilingInteger()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Num literal = new Num("2.0");

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

		Num literal = new Num("0.5");

		Type type = literal.getType(variables);

		literal.compile(methodVisitor, variables);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(1, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(0.5, firstInstruction.cst);

		assertEquals("java.lang.Double", type.toFullyQualifiedType());
	}
}
