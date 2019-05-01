package yirgacheffe.compiler.listener;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConstructorListenerTest
{
	@Test
	public void testMainClassHasDefaultConstructor()
	{
		String source = "class MyClass { main method(Array<String> args) {} }";
		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List methods = classNode.methods;

		assertEquals(3, methods.size());

		MethodNode constructor = (MethodNode) methods.get(2);

		assertEquals("()V", constructor.desc);
		assertEquals(Opcodes.ACC_PUBLIC, constructor.access);
		assertEquals("<init>", constructor.name);
		assertEquals(1, constructor.maxLocals);
		assertEquals(1, constructor.maxStack);

		InsnList instructions = constructor.instructions;

		assertEquals(3, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		MethodInsnNode secondInstruction = (MethodInsnNode) instructions.get(1);

		assertEquals(Opcodes.INVOKESPECIAL, secondInstruction.getOpcode());
		assertEquals("java/lang/Object", secondInstruction.owner);
		assertEquals("<init>", secondInstruction.name);
		assertEquals("()V", secondInstruction.desc);

		assertEquals(Opcodes.RETURN, instructions.get(2).getOpcode());
	}

	@Test
	public void testConstructorWithNumberParameter()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass(Num param) {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List methods = classNode.methods;

		assertEquals(1, methods.size());

		MethodNode constructor = (MethodNode) methods.get(0);

		assertEquals("(D)V", constructor.desc);
		assertEquals(Opcodes.ACC_PUBLIC, constructor.access);
		assertEquals("<init>", constructor.name);

		InsnList instructions = constructor.instructions;

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		MethodInsnNode secondInstruction = (MethodInsnNode) instructions.get(1);

		assertEquals(Opcodes.INVOKESPECIAL, secondInstruction.getOpcode());
		assertEquals("java/lang/Object", secondInstruction.owner);
		assertEquals("<init>", secondInstruction.name);
		assertEquals("()V", secondInstruction.desc);
	}

	@Test
	public void testConstructorWithMissingModifier()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"MyClass(Num param) {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:0 Expected public or private access modifier " +
				"at start of constructor declaration.\n",
			result.getErrors());
	}

	@Test
	public void testConstructorWithWrongName()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClasss(Num param) {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:7 Constructor of incorrect type MyClasss: expected MyClass.\n",
			result.getErrors());
	}

	@Test
	public void testPrivateConstructor()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"private MyClass(String param) {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List methods = classNode.methods;

		assertEquals(1, methods.size());

		MethodNode constructor = (MethodNode) methods.get(0);

		assertEquals("(Ljava/lang/String;)V", constructor.desc);
		assertEquals(Opcodes.ACC_PRIVATE, constructor.access);
		assertEquals("<init>", constructor.name);
	}

	@Test
	public void testConstructorCallsInitialiser()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"String thingy = \"sumpt\";\n" +
				"public MyClass() {}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List methods = classNode.methods;

		assertEquals(2, methods.size());

		MethodNode constructor = (MethodNode) methods.get(1);
		InsnList instructions = constructor.instructions;

		assertEquals(5, instructions.size());

		MethodInsnNode fourthInstruction = (MethodInsnNode) instructions.get(3);

		assertEquals(Opcodes.INVOKEVIRTUAL, fourthInstruction.getOpcode());
		assertEquals("MyClass", fourthInstruction.owner);
		assertEquals("0_init_field", fourthInstruction.name);
		assertEquals("()V", fourthInstruction.desc);
	}
}
