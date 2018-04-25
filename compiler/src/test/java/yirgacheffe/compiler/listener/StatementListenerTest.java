package yirgacheffe.compiler.listener;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StatementListenerTest
{
	@Test
	public void testLocalVariableDeclaration() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass()" +
				"{\n" +
					"num myVariable;\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());
	}

	@Test
	public void testMissingSemicolon() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass()" +
				"{\n" +
					"num myVariable\n" +
					"num anotherVariable\n" +
					"new String()\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 5:0 Missing ';'.\n" +
			"line 6:0 Missing ';'.\n" +
			"line 7:0 Missing ';'.\n",
			result.getErrors());
	}

	@Test
	public void testLocalVariableInitialisation() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public void method()" +
				"{\n" +
					"num myVariable = 50;\n" +
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
		assertEquals(3, firstMethod.maxLocals);

		InsnList instructions = firstMethod.instructions;

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(50.0, firstInstruction.cst);

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.DSTORE, secondInstruction.getOpcode());
		assertEquals(1, secondInstruction.var);
	}

	@Test
	public void testAssignParameterToLocalVariable() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public void method(num param)" +
				"{\n" +
					"num myVariable = param;\n" +
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
		assertEquals(5, firstMethod.maxLocals);

		InsnList instructions = firstMethod.instructions;

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.DLOAD, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.DSTORE, secondInstruction.getOpcode());
		assertEquals(3, secondInstruction.var);

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.RETURN, thirdInstruction.getOpcode());
	}

	@Test
	public void testTwoVariableInitialisations() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public void method()" +
				"{\n" +
					"num myVariable = 1;\n" +
					"bool anotherVariable = false;\n" +
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
		assertEquals(4, firstMethod.maxLocals);

		InsnList instructions = firstMethod.instructions;

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(1.0, firstInstruction.cst);

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.DSTORE, secondInstruction.getOpcode());
		assertEquals(1, secondInstruction.var);

		LdcInsnNode thirdInstruction = (LdcInsnNode) instructions.get(2);

		assertEquals(Opcodes.LDC, thirdInstruction.getOpcode());
		assertEquals(0, thirdInstruction.cst);

		VarInsnNode fourthInstruction = (VarInsnNode) instructions.get(3);

		assertEquals(Opcodes.ISTORE, fourthInstruction.getOpcode());
		assertEquals(3, fourthInstruction.var);
	}

	@Test
	public void testInitialiseNumAfterObject() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public void method()" +
				"{\n" +
					"Object myObject = new Object();\n" +
					"num myVariable = 1;\n" +
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
		assertEquals(4, firstMethod.maxLocals);
	}

	@Test
	public void testUninitialisedVariable() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass()" +
				"{\n" +
					"myVariable = 1;\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 4:0 Assignment to uninitialised variable 'myVariable'.\n",
			result.getErrors());
	}

	@Test
	public void testLocalVariableAssignment() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public void method()" +
				"{\n" +
					"num myVariable;" +
					"myVariable = 1;\n" +
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
		assertEquals(3, firstMethod.maxLocals);

		InsnList instructions = firstMethod.instructions;

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(1.0, firstInstruction.cst);

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.DSTORE, secondInstruction.getOpcode());
		assertEquals(1, secondInstruction.var);
	}

	@Test
	public void testMissingVariableTypeParameter() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass()" +
				"{\n" +
					"MutableReference ref =" +
						"new MutableReference<String>(\"thingy\");\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 4:0 Missing type parameter for type" +
				" yirgacheffe.lang.MutableReference.\n",
			result.getErrors());
	}

	@Test
	public void testMissingConstructorTypeParameter() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass()" +
				"{\n" +
					"MutableReference<String> ref =" +
					"new MutableReference(\"thingy\");\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 4:34 Missing type parameter for type" +
				" yirgacheffe.lang.MutableReference.\n",
			result.getErrors());
	}

	@Test
	public void testMismatchedTypeAssignment() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass()" +
				"{\n" +
					"num number = \"thingy\";" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 4:0 Cannot assign expression of type " +
				"java.lang.String to variable of type num.\n",
			result.getErrors());
	}
}
