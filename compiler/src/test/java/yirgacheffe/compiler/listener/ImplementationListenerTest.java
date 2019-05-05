package yirgacheffe.compiler.listener;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
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

public class ImplementationListenerTest
{
	@Test
	public void testImplementsMissingType()
	{
		String source =
			"class MyClass implements { public MyClass() {} }";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:14 Missing implemented type.\n",
			result.getErrors());
	}

	@Test
	public void testFailToImplementPrimitiveType()
	{
		String source =
			"class MyClass implements Bool { public MyClass() {} }";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:25 Cannot implement primitive type Bool.\n",
			result.getErrors());
	}

	@Test
	public void testFailToImplementClass()
	{
		String source =
			"class MyClass implements Object { public MyClass() {} }";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:25 Cannot implement concrete type java.lang.Object.\n",
			result.getErrors());
	}

	@Test
	public void testFailToImplementInterface()
	{
		String source =
			"class MyClass implements Comparable<String> { public MyClass() {} }";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:0 Missing implementation of interface method " +
				"Num compareTo(java.lang.String).\n",
			result.getErrors());
	}

	@Test
	public void testImplementsComparable()
	{
		String source =
			"class MyClass implements Comparable<String>\n" +
			"{\n" +
				"public Num compareTo(String other) { return 0; }\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());
		assertEquals("MyClass.class", result.getClassFileName());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List interfaces = classNode.interfaces;

		assertEquals(1, classNode.interfaces.size());
		assertEquals("java/lang/Comparable", interfaces.get(0));
		assertEquals(
			"Ljava/lang/Object;Ljava/lang/Comparable<Ljava/lang/String;>;",
			classNode.signature);
	}

	@Test
	public void testImplementsWithTypeVariance()
	{
		String interfaceSource =
			"interface Objectifier\n" +
			"{" +
				"Object objectify(String string);\n" +
			"}";

		String source =
			"class MyClass implements Objectifier\n" +
			"{\n" +
				"public String objectify(Object obj) { return obj.toString(); }\n" +
				"public MyClass() {}\n" +
			"}";

		Classes classes = new Classes();

		new Compiler("", interfaceSource).compileInterface(classes);

		classes.clearCache();

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());
		assertEquals("MyClass.class", result.getClassFileName());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List interfaces = classNode.interfaces;

		assertEquals(1, classNode.interfaces.size());
		assertEquals("Objectifier", interfaces.get(0));
		assertEquals(
			"Ljava/lang/Object;LObjectifier;",
			classNode.signature);

		MethodNode bridgeMethod = (MethodNode) classNode.methods.get(1);

		assertEquals("objectify", bridgeMethod.name);
		assertEquals(Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC, bridgeMethod.access);
		assertEquals("(Ljava/lang/String;)Ljava/lang/Object;", bridgeMethod.desc);

		InsnList instructions = bridgeMethod.instructions;

		assertEquals(4, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.ALOAD, secondInstruction.getOpcode());
		assertEquals(1, secondInstruction.var);

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals("MyClass", thirdInstruction.owner);
		assertEquals("objectify", thirdInstruction.name);
		assertEquals("(Ljava/lang/Object;)Ljava/lang/String;", thirdInstruction.desc);
		assertEquals(false, thirdInstruction.itf);

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.ARETURN, fourthInstruction.getOpcode());
	}
}
