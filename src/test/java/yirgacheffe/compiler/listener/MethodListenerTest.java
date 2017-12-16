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
	public void testInterfaceWithMethod() throws Exception
	{
		String source =
			"interface MyInterface\n" +
				"{\n" +
				"num myMethod();\n" +
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

		MethodNode firstMethod = methods.get(0);

		assertEquals(Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT, firstMethod.access);
		assertEquals("()D", firstMethod.desc);
		assertEquals("myMethod", firstMethod.name);
	}

	@Test
	public void testInterfaceMethodWithModifier() throws Exception
	{
		String source =
			"interface MyInterface\n" +
				"{\n" +
				"public num myInterfaceMethod();\n" +
				"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result =
			compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:0 Access modifier is not required for " +
				"interface method declaration.\n",
			result.getErrors());
	}

	@Test
	public void testInterfaceMethodWithParameters() throws Exception
	{
		String source =
			"interface MyInterface\n" +
				"{\n" +
				"num myParameterisedMethod(String param1, num param2);\n" +
				"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result =
			compiler.compile(new Classes());

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
	public void testParameterWithMissingType() throws Exception
	{
		String source =
			"interface MyInterface\n" +
				"{\n" +
				"num myMethod(param1, num param2);\n" +
				"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result =
			compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:13 Expected type before parameter identifier.\n",
			result.getErrors());
	}

	@Test
	public void testClassWithPublicMethod() throws Exception
	{
		String source =
			"class MyClass\n" +
				"{\n" +
				"public num myMethod(String param1, num param2) {}\n" +
				"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result =
			compiler.compile(new Classes());

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
	public void testClassWithPrivateMethod() throws Exception
	{
		String source =
			"class MyClass\n" +
				"{\n" +
				"private num myMethod(String param1, num param2) {}\n" +
				"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result =
			compiler.compile(new Classes());

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
	public void testClassMethodWithMissingModifier() throws Exception
	{
		String source =
			"class MyClass\n" +
				"{\n" +
				"num myMethod(String param1, num param2) {}\n" +
				"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result =
			compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:0 Expected public or private access modifier " +
				"at start of method declaration.\n",
			result.getErrors());
	}

	@Test
	public void testClassWithTwoMethods() throws Exception
	{
		String source =
			"class MyClass\n" +
				"{\n" +
				"public num myMethod(String param1, num param2) {}\n" +
				"public String buildString(StringBuilder builder) {}\n" +
				"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result =
			compiler.compile(new Classes());

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
	public void testMethodWithImportedTypes() throws Exception
	{
		String source =
			"import java.util.List;\n" +
			"import java.util.Set;\n" +
			"class MyClass\n" +
				"{\n" +
				"public Set myMethod(List param1) {}\n" +
				"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result =
			compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;
		MethodNode method = methods.get(0);

		assertEquals("(Ljava/util/List;)Ljava/util/Set;", method.desc);
	}

	@Test
	public void testInterfaceWithVoidMethod() throws Exception
	{
		String source =
			"interface MyInterface\n" +
				"{\n" +
				"void myMethod();\n" +
				"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result =
			compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;
		MethodNode firstMethod = methods.get(0);

		assertEquals("()V", firstMethod.desc);
	}

	@Test
	public void testInterfaceWithBooleanMethod() throws Exception
	{
		String source =
			"interface MyInterface\n" +
				"{\n" +
				"bool myMethod();\n" +
				"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result =
			compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;
		MethodNode firstMethod = methods.get(0);

		assertEquals("()B", firstMethod.desc);
	}

	@Test
	public void testInterfaceWithCharacterMethod() throws Exception
	{
		String source =
			"interface MyInterface\n" +
				"{\n" +
				"char myMethod();\n" +
				"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result =
			compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;
		MethodNode firstMethod = methods.get(0);

		assertEquals("()C", firstMethod.desc);
	}
}
