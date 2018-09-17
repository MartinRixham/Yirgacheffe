package yirgacheffe.compiler.listener;

import org.junit.Ignore;
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
	public void testLocalVariableDeclaration()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass()" +
				"{\n" +
					"Num myVariable;\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());
	}

	@Test
	public void testMissingSemicolon()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass()" +
				"{\n" +
					"Num myVariable\n" +
					"Num anotherVariable\n" +
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
	public void testAssignParameterToLocalVariable()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method(Num param)" +
				"{\n" +
					"Num myVariable = param;\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List methods = classNode.methods;
		MethodNode firstMethod = (MethodNode) methods.get(0);

		InsnList instructions = firstMethod.instructions;

		assertEquals(3, instructions.size());

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
	public void testUninitialisedVariable()
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
	public void testWriteVariableInsideBlock()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{\n" +
					"{" +
						"String thingy = \"thingy\";\n" +
					"}\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List methods = classNode.methods;
		MethodNode firstMethod = (MethodNode) methods.get(0);

		InsnList instructions = firstMethod.instructions;

		assertEquals(3, instructions.size());
	}

	@Test
	public void testAssignToVariableInParentBlock()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{\n" +
					"Num myVariable;\n" +
					"{" +
						"myVariable = 50;" +
					"}\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());
	}

	@Ignore
	@Test
	public void testIfStatement()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{\n" +
					"if (true)" +
					"{" +
						"Num one = 1;\n" +
					"}\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List methods = classNode.methods;
		MethodNode firstMethod = (MethodNode) methods.get(0);

		InsnList instructions = firstMethod.instructions;

		// assertEquals(4, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_1, firstInstruction.getOpcode());
	}

	@Test
	public void testMismatchedTypeAssignment()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass()" +
				"{\n" +
					"Num number = \"thingy\";" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 4:0 Cannot assign expression of type " +
				"java.lang.String to variable of type Num.\n",
			result.getErrors());
	}

	@Test
	public void testReturnStatement()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Num method()" +
				"{\n" +
					"return 1000000;\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List methods = classNode.methods;
		MethodNode firstMethod = (MethodNode) methods.get(0);

		assertEquals(2, firstMethod.maxStack);
		assertEquals(1, firstMethod.maxLocals);

		InsnList instructions = firstMethod.instructions;

		assertEquals(2, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(1000000.0, firstInstruction.cst);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DRETURN, secondInstruction.getOpcode());
	}

	@Test
	public void testMismatchedReturnType()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Num method()" +
				"{\n" +
					"return \"thingy\";\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals("line 4:0 Mismatched return type: " +
			"Cannot return expression of type " +
			"java.lang.String from method of return type Num.\n",
			result.getErrors());
	}

	@Test
	public void testMissingReturnStatement()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public String method()" +
				"{\n" +
					"new String(\"thingy\");\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals("line 5:0 No return statement in method.\n",
			result.getErrors());
	}
}
