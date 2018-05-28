package yirgacheffe.compiler.listener;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.type.Classes;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExpressionListenerTest
{
	@Test
	public void testLocalVariableRead()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{\n" +
					"String myVariable = \"thingy\";\n" +
					"String anotherVariable = myVariable;\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;
		MethodNode method = methods.get(0);

		InsnList instructions = method.instructions;

		assertEquals(5, instructions.size());

		assertEquals(Opcodes.ALOAD, instructions.get(2).getOpcode());
	}

	@Test
	public void testUnknownVariableRead()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{\n" +
					"String myVariable = unknownVariable;\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 4:20 Unknown local variable 'unknownVariable'.\n",
			result.getErrors());
	}

	@Test
	public void testAssignVariableFromFunction()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()\n" +
				"{\n" +
					"Num myVariable = this.getOne();\n" +
				"}\n" +
				"public Num getOne()\n" +
				"{\n" +
					"return 1;\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);
		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;
		MethodNode firstMethod = methods.get(0);

		assertEquals("method", firstMethod.name);
		assertEquals(2, firstMethod.maxStack);
		assertEquals(3, firstMethod.maxLocals);

		InsnList instructions = firstMethod.instructions;

		assertEquals(4, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		MethodInsnNode secondInstruction = (MethodInsnNode) instructions.get(1);

		assertEquals(Opcodes.INVOKEVIRTUAL, secondInstruction.getOpcode());
		assertEquals("MyClass", secondInstruction.owner);
		assertEquals("getOne", secondInstruction.name);

		VarInsnNode thirdInstruction = (VarInsnNode) instructions.get(2);

		assertEquals(Opcodes.DSTORE, thirdInstruction.getOpcode());
		assertEquals(1, thirdInstruction.var);

		MethodNode secondMethod = methods.get(1);

		assertEquals("getOne", secondMethod.name);
		assertEquals(2, secondMethod.maxStack);
		assertEquals(1, secondMethod.maxLocals);

		instructions = secondMethod.instructions;

		assertEquals(2, instructions.size());

		LdcInsnNode first = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, first.getOpcode());
		assertEquals(1.0, first.cst);

		InsnNode second = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DRETURN, second.getOpcode());
	}

	@Test
	public void testGenericLocalVariableInitialisation()
	{
		String source =
			"import java.util.List;" +
			"import java.util.ArrayList;" +
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{\n" +
					"List<Integer> list = new ArrayList<Integer>();\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;
		MethodNode firstMethod = methods.get(0);

		assertEquals(2, firstMethod.maxStack);
		assertEquals(2, firstMethod.maxLocals);
	}

	@Test
	public void testObjectConstructedWithObject()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public String method()" +
				"{\n" +
					"return new String(new String(\"thingy\"));\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;
		MethodNode firstMethod = methods.get(0);

		assertEquals(5, firstMethod.maxStack);
		assertEquals(1, firstMethod.maxLocals);
	}
}
