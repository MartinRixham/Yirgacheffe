package yirgacheffe.compiler.listener;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.type.Classes;

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
					"anotherVariable = myVariable;\n" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode method = classNode.methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(7, instructions.size());

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
				"public MyClass() {}\n" +
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
					"return 1.0;\n" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode firstMethod = classNode.methods.get(0);

		assertEquals("method", firstMethod.name);
		assertEquals(2, firstMethod.maxStack);
		assertEquals(3, firstMethod.maxLocals);

		InsnList instructions = firstMethod.instructions;

		assertEquals(6, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		LabelNode secondInstruction = (LabelNode) instructions.get(1);
		Label label = secondInstruction.getLabel();

		LineNumberNode thirdInstruction = (LineNumberNode) instructions.get(2);

		assertEquals(label, thirdInstruction.start.getLabel());
		assertEquals(5, thirdInstruction.line);

		InvokeDynamicInsnNode fourthInstruction =
			(InvokeDynamicInsnNode) instructions.get(3);

		assertEquals(Opcodes.INVOKEDYNAMIC, fourthInstruction.getOpcode());
		assertEquals("getOne", fourthInstruction.name);

		VarInsnNode fifthInstruction = (VarInsnNode) instructions.get(4);

		assertEquals(Opcodes.DSTORE, fifthInstruction.getOpcode());
		assertEquals(1, fifthInstruction.var);

		MethodNode secondMethod = classNode.methods.get(1);

		assertEquals("getOne", secondMethod.name);

		instructions = secondMethod.instructions;

		assertEquals(2, instructions.size());

		InsnNode first = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCONST_1, first.getOpcode());

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
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode firstMethod = classNode.methods.get(0);

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
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode firstMethod = classNode.methods.get(0);

		assertEquals(5, firstMethod.maxStack);
		assertEquals(1, firstMethod.maxLocals);
	}

	@Test
	public void testVariableOfWrongType()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass() {}\n" +
				"public Void method(Array<String> args)\n" +
				"{\n" +
					"Object string = \"thingy\";\n" +
					"this.print(string);\n" +
				"}\n" +
				"public Void print(String string)\n" +
				"{\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 7:4 Method MyClass.print(java.lang.Object) not found.\n",
			result.getErrors());
	}

	@Test
	public void testAssignVariableOfWrongType()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass() {}\n" +
				"public Void method(Array<String> args)\n" +
				"{\n" +
					"System string = \"thingy\";\n" +
					"this.print(string);\n" +
				"}\n" +
				"public Void print(String string)\n" +
				"{\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals("line 6:0 Cannot assign expression of type " +
			"java.lang.String to variable of " +
			"type yirgacheffe.lang.System.\n" +
			"line 7:4 Method MyClass.print(yirgacheffe.lang.System) not found.\n",
			result.getErrors());
	}
}
