package yirgacheffe.compiler.listener;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.type.Classes;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BooleanExpressionListenerTest
{
	@Test
	public void testEquals()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Bool method()" +
				"{\n" +
					"return 1.0 == 2.0 == 3.0;\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List methods = classNode.methods;
		MethodNode method = (MethodNode) methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(21, instructions.size());

		JumpInsnNode fourthInstruction = (JumpInsnNode) instructions.get(3);

		assertEquals(Opcodes.IFNE, fourthInstruction.getOpcode());
	}

	@Test
	public void testNotEquals()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Bool method()" +
				"{\n" +
					"return 1.0 != 2.0 != 3.0;\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List methods = classNode.methods;
		MethodNode method = (MethodNode) methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(21, instructions.size());

		JumpInsnNode fourthInstruction = (JumpInsnNode) instructions.get(3);

		assertEquals(Opcodes.IFEQ, fourthInstruction.getOpcode());
	}

	@Test
	public void testLessThan()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Bool method()" +
				"{\n" +
					"return 1.0 < 2.0;\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List methods = classNode.methods;
		MethodNode method = (MethodNode) methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(12, instructions.size());

		JumpInsnNode fourthInstruction = (JumpInsnNode) instructions.get(3);

		assertEquals(Opcodes.IFGE, fourthInstruction.getOpcode());
	}

	@Test
	public void testGreaterThan()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Bool method()" +
				"{\n" +
					"return 1.0 > 2.0;\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List methods = classNode.methods;
		MethodNode method = (MethodNode) methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(12, instructions.size());

		JumpInsnNode fourthInstruction = (JumpInsnNode) instructions.get(3);

		assertEquals(Opcodes.IFLE, fourthInstruction.getOpcode());
	}

	@Test
	public void testLessThanOrEqual()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Bool method()" +
				"{\n" +
					"return 1.0 <= 2.0;\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List methods = classNode.methods;
		MethodNode method = (MethodNode) methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(12, instructions.size());

		JumpInsnNode fourthInstruction = (JumpInsnNode) instructions.get(3);

		assertEquals(Opcodes.IFGT, fourthInstruction.getOpcode());
	}

	@Test
	public void testGreaterThanOrEqual()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Bool method()" +
				"{\n" +
					"return 1.0 >= 2.0;\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List methods = classNode.methods;
		MethodNode method = (MethodNode) methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(12, instructions.size());

		JumpInsnNode fourthInstruction = (JumpInsnNode) instructions.get(3);

		assertEquals(Opcodes.IFLT, fourthInstruction.getOpcode());
	}
}
