package yirgacheffe.compiler.listener;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MethodListenerTest
{
	@Test
	public void testInterfaceWithMethod()
	{
		String source =
			"interface MyInterface\n" +
			"{\n" +
				"Num myMethod();\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;

		assertEquals(1, methods.size());

		MethodNode firstMethod = methods.get(0);

		assertEquals(Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT, firstMethod.access);
		assertEquals("()D", firstMethod.desc);
		assertEquals("myMethod", firstMethod.name);
	}

	@Test
	public void testInterfaceMethodWithModifier()
	{
		String source =
			"interface MyInterface\n" +
			"{\n" +
				"public Num myInterfaceMethod();\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:0 Access modifier is not required for " +
				"interface method declaration.\n",
			result.getErrors());
	}

	@Test
	public void testInterfaceMethodWithKeywordIdentifier()
	{
		String source =
			"interface MyInterface\n" +
			"{\n" +
				"Num return();\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:4 Invalid use of keyword 'return'.\n",
			result.getErrors());
	}

	@Test
	public void testInterfaceMethodWithParameters()
	{
		String source =
			"interface MyInterface\n" +
			"{\n" +
				"Num myParameterisedMethod(String param1, Num param2);\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;
		MethodNode firstMethod = methods.get(0);

		assertEquals("(Ljava/lang/String;D)D", firstMethod.desc);
		assertEquals("myParameterisedMethod", firstMethod.name);
	}

	@Test
	public void testParameterWithMissingType()
	{
		String source =
			"interface MyInterface\n" +
			"{\n" +
				"Num myMethod(param1, Num param2);\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:13 Expected type before parameter identifier.\n",
			result.getErrors());
	}

	@Test
	public void testClassWithPublicMethod()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Num myMethod(String param1, Num param2) {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;

		assertEquals(2, methods.size());

		MethodNode method = methods.get(0);

		assertEquals("(Ljava/lang/String;D)D", method.desc);
		assertEquals(Opcodes.ACC_PUBLIC, method.access);
		assertEquals("myMethod", method.name);
	}

	@Test
	public void testClassWithPrivateMethod()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"private Num myMethod(String param1, Num param2) {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;

		assertEquals(2, methods.size());

		MethodNode method = methods.get(0);

		assertEquals("(Ljava/lang/String;D)D", method.desc);
		assertEquals(Opcodes.ACC_PRIVATE, method.access);
		assertEquals("myMethod", method.name);
	}

	@Test
	public void testClassMethodWithMissingModifier()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"Num myMethod(String param1, Num param2) {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:0 Expected public or private access modifier " +
				"at start of method declaration.\n",
			result.getErrors());
	}

	@Test
	public void testClassWithTwoMethods()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Num myMethod(String param1, Num param2) {}\n" +
				"public String buildString(StringBuilder builder) {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;

		assertEquals(3, methods.size());

		MethodNode secondMethod = methods.get(0);

		assertEquals("(Ljava/lang/String;D)D", secondMethod.desc);
		assertEquals("myMethod", secondMethod.name);

		MethodNode thirdMethod = methods.get(1);

		assertEquals(
			"(Ljava/lang/StringBuilder;)Ljava/lang/String;",
			thirdMethod.desc);

		assertEquals("buildString", thirdMethod.name);
	}

	@Test
	public void testMethodWithImportedTypes()
	{
		String source =
			"import java.util.List;\n" +
			"import java.util.Set;\n" +
			"class MyClass\n" +
			"{\n" +
				"public Set<String> myMethod(List<String> param1) {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;
		MethodNode method = methods.get(0);

		assertEquals("(Ljava/util/List;)Ljava/util/Set;", method.desc);
	}

	@Test
	public void testInterfaceWithVoidMethod()
	{
		String source =
			"interface MyInterface\n" +
			"{\n" +
				"Void myMethod();\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;
		MethodNode firstMethod = methods.get(0);

		assertEquals("()V", firstMethod.desc);
	}

	@Test
	public void testInterfaceWithBooleanMethod()
	{
		String source =
			"interface MyInterface\n" +
			"{\n" +
				"Bool myMethod();\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;
		MethodNode firstMethod = methods.get(0);

		assertEquals("()Z", firstMethod.desc);
	}

	@Test
	public void testInterfaceWithCharacterMethod()
	{
		String source =
			"interface MyInterface\n" +
			"{\n" +
				"Char myMethod();\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;
		MethodNode firstMethod = methods.get(0);

		assertEquals("()C", firstMethod.desc);
	}
}
