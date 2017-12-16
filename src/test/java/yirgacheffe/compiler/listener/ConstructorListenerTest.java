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
	public void testEmptyClassHasDefaultConstructor() throws Exception
	{
		String source = "class MyClass {}";
		Compiler compiler = new Compiler("", source);
		CompilationResult result =
			compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;

		assertEquals(1, methods.size());

		MethodNode constructor = methods.get(0);

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
	public void testConstructorWithNumberParameter() throws Exception
	{
		String source =
			"class MyClass\n" +
				"{\n" +
				"public MyClass(num param) {}\n" +
				"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result =
			compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;

		assertEquals(1, methods.size());

		MethodNode constructor = methods.get(0);

		assertEquals("(D)V", constructor.desc);
		assertEquals(Opcodes.ACC_PUBLIC, constructor.access);
		assertEquals("<init>", constructor.name);
	}

	@Test
	public void testConstructorWithMissingModifier() throws Exception
	{
		String source =
			"class MyClass\n" +
				"{\n" +
					"MyClass(num param) {}\n" +
				"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result =
			compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:0 Expected public or private access modifier " +
				"at start of constructor declaration.\n",
			result.getErrors());
	}

	@Test
	public void testConstructorWithWrongName() throws Exception
	{
		String source =
			"class MyClass\n" +
				"{\n" +
				"public MyClasss(num param) {}\n" +
				"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result =
			compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:7 Constructor of incorrect type MyClasss: expected MyClass.\n",
			result.getErrors());
	}

	@Test
	public void testPrivateConstructor() throws Exception
	{
		String source =
			"class MyClass\n" +
				"{\n" +
				"private MyClass(String param) {}\n" +
				"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result =
			compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;

		assertEquals(1, methods.size());

		MethodNode constructor = methods.get(0);

		assertEquals("(Ljava/lang/String;)V", constructor.desc);
		assertEquals(Opcodes.ACC_PRIVATE, constructor.access);
		assertEquals("<init>", constructor.name);
	}
}
