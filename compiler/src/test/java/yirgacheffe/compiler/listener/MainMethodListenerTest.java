package yirgacheffe.compiler.listener;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.type.Classes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MainMethodListenerTest
{
	@Test
	public void testMainMethod()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"main myMainMethod(Array<String> args) {}\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		assertEquals(4, classNode.methods.size());

		MethodNode method = classNode.methods.get(0);

		assertEquals("(Lyirgacheffe/lang/Array;)V", method.desc);
		assertEquals(Opcodes.ACC_PUBLIC, method.access);
		assertEquals("myMainMethod", method.name);
		assertEquals(2, method.maxLocals);
		assertEquals(2, method.maxStack);

		MethodNode mainMethod = classNode.methods.get(3);

		assertEquals("([Ljava/lang/String;)V", mainMethod.desc);
		assertEquals(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, mainMethod.access);
		assertEquals("main", mainMethod.name);
		assertEquals(4, mainMethod.maxStack);
		assertEquals(1, mainMethod.maxLocals);

		InsnList instructions = mainMethod.instructions;

		TypeInsnNode firstInstruction = (TypeInsnNode) instructions.get(0);

		assertEquals(Opcodes.NEW, firstInstruction.getOpcode());
		assertEquals("MyClass", firstInstruction.desc);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKESPECIAL, thirdInstruction.getOpcode());
		assertEquals("MyClass", thirdInstruction.owner);
		assertEquals("<init>", thirdInstruction.name);
		assertEquals("()V", thirdInstruction.desc);
		assertFalse(thirdInstruction.itf);

		TypeInsnNode fourthInstruction = (TypeInsnNode) instructions.get(3);

		assertEquals(Opcodes.NEW, fourthInstruction.getOpcode());
		assertEquals("yirgacheffe/lang/Array", fourthInstruction.desc);

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.DUP, fifthInstruction.getOpcode());

		VarInsnNode sixthInstruction = (VarInsnNode) instructions.get(5);

		assertEquals(Opcodes.ALOAD, sixthInstruction.getOpcode());
		assertEquals(0, sixthInstruction.var);

		MethodInsnNode seventhInstruction = (MethodInsnNode) instructions.get(6);

		assertEquals(Opcodes.INVOKESPECIAL, seventhInstruction.getOpcode());
		assertEquals("yirgacheffe/lang/Array", seventhInstruction.owner);
		assertEquals("<init>", seventhInstruction.name);
		assertEquals("([Ljava/lang/Object;)V", seventhInstruction.desc);
		assertFalse(seventhInstruction.itf);

		MethodInsnNode eighthInstruction = (MethodInsnNode) instructions.get(7);

		assertEquals(Opcodes.INVOKEVIRTUAL, eighthInstruction.getOpcode());
		assertEquals("MyClass", eighthInstruction.owner);
		assertEquals("myMainMethod", eighthInstruction.name);
		assertEquals("(Lyirgacheffe/lang/Array;)V", eighthInstruction.desc);
		assertFalse(eighthInstruction.itf);

		InsnNode ninthInstruction = (InsnNode) instructions.get(8);

		assertEquals(Opcodes.RETURN, ninthInstruction.getOpcode());
	}

	@Test
	public void testMainMethodInstantiatesItself()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"main myMainMethod(Array<String> args)" +
				"{" +
					"MyClass myClass = new MyClass();" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());
	}

	@Test
	public void testMainMethodMissingArguments()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"main myMainMethod()" +
				"{" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:0 Main method must have exactly one parameter of type " +
			"yirgacheffe.lang.Array<java.lang.String>.\n",
			result.getErrors());
	}

	@Test
	public void testMainMethodWrongArgument()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"main myMainMethod(String args)" +
				"{" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:0 Main method must have exactly one parameter of type " +
			"yirgacheffe.lang.Array<java.lang.String>.\n",
			result.getErrors());
	}

	@Test
	public void testPrivateMainMethodNotAllowed()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"private main method(Array<String> args)" +
				"{" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:0 Main method cannot be private.\n",
			result.getErrors());
	}

	@Test
	public void testMainMethodHasDefaultConstructor()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass(String string)" +
				"{" +
				"}\n" +
				"main method(Array<String> args)" +
				"{" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());
	}

	@Test
	public void testMultipleMainMethods()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"main method(Array<String> args)" +
				"{" +
				"}\n" +
				"main schmethod(Array<String> args)" +
				"{" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 4:0 Cannot have multiple main methods.\n",
			result.getErrors());
	}

	@Test
	public void testMainMethodWithInitialiser()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"String string = \"thingy\";\n" +
				"main myMainMethod(Array<String> args) {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		assertEquals(4, classNode.methods.size());

		MethodNode method = classNode.methods.get(3);

		assertEquals("()V", method.desc);
		assertEquals(Opcodes.ACC_PUBLIC, method.access);
		assertEquals("<init>", method.name);

		InsnList instructions = method.instructions;

		assertEquals(5, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		MethodInsnNode secondInstruction = (MethodInsnNode) instructions.get(1);

		assertEquals(Opcodes.INVOKESPECIAL, secondInstruction.getOpcode());
		assertEquals("<init>", secondInstruction.name);
		assertEquals("()V", secondInstruction.desc);

		VarInsnNode thirdInstruction = (VarInsnNode) instructions.get(2);

		assertEquals(Opcodes.ALOAD, thirdInstruction.getOpcode());
		assertEquals(0, thirdInstruction.var);

		MethodInsnNode fourthInstruction = (MethodInsnNode) instructions.get(3);

		assertEquals(Opcodes.INVOKEVIRTUAL, fourthInstruction.getOpcode());
		assertEquals("0init_field_string", fourthInstruction.name);
		assertEquals("()V", fourthInstruction.desc);
	}
}
