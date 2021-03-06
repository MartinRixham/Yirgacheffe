package yirgacheffe.compiler.listener;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;

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

		assertEquals(1, classNode.methods.size());

		MethodNode firstMethod = classNode.methods.get(0);

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
			"line 3:4 Invalid use of symbol 'return'.\n",
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

		MethodNode firstMethod = classNode.methods.get(0);

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
				"public Num myMethod(String param1, Num param2)" +
				"{" +
					"return 1;" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		assertEquals(3, classNode.methods.size());

		MethodNode method = classNode.methods.get(0);

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
				"private Num myMethod(String param1, Num param2)" +
				"{" +
					"return 1;" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		assertEquals(3, classNode.methods.size());

		MethodNode method = classNode.methods.get(0);

		assertEquals("(Ljava/lang/String;D)D", method.desc);
		assertEquals(Opcodes.ACC_PROTECTED, method.access);
		assertEquals("myMethod", method.name);
	}

	@Test
	public void testMethodWithTypeParameter()
	{
		String source =
			"class MyClass<W> implements Comparable<W>\n" +
			"{\n" +
				"public Num compareTo(W other)" +
				"{" +
					"return other.hashCode();" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		assertEquals(4, classNode.methods.size());

		MethodNode bridgeMethod = classNode.methods.get(0);

		assertEquals("(Ljava/lang/Object;)I", bridgeMethod.desc);
		assertEquals("(TW;)I", bridgeMethod.signature);
		assertEquals(Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC, bridgeMethod.access);
		assertEquals("compareTo", bridgeMethod.name);

		MethodNode method = classNode.methods.get(1);

		assertEquals("(Ljava/lang/Object;)D", method.desc);
		assertEquals("(TW;)D", method.signature);
		assertEquals(Opcodes.ACC_PROTECTED, method.access);
		assertEquals("compareTo", method.name);
	}

	@Test
	public void testClassMethodWithMissingModifier()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"Num myMethod(String param1, Num param2)" +
				"{" +
					"return 1;" +
				"}\n" +
				"public MyClass() {}\n" +
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
				"public Num myMethod(String param1, Num param2)" +
				"{" +
					"return 1;" +
				"}\n" +

				"public String buildString(StringBuilder builder)" +
				"{" +
					"return \"\";" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		assertEquals(4, classNode.methods.size());

		MethodNode secondMethod = classNode.methods.get(0);

		assertEquals("(Ljava/lang/String;D)D", secondMethod.desc);
		assertEquals("myMethod", secondMethod.name);

		MethodNode thirdMethod = classNode.methods.get(1);

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
			"import java.util.HashSet;\n" +
			"class MyClass\n" +
			"{\n" +
				"public Set<String> myMethod(List<String> param1)" +
				"{" +
					"return new HashSet<String>();" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode method = classNode.methods.get(0);

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

		MethodNode firstMethod = classNode.methods.get(0);

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

		MethodNode firstMethod = classNode.methods.get(0);

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

		MethodNode firstMethod = classNode.methods.get(0);

		assertEquals("()C", firstMethod.desc);
	}

	@Test
	public void testDuplicateMethod()
	{
		String source =
			"package thingy;" +
			"interface MyInterface\n" +
			"{\n" +
				"Char myMethod(String string);\n" +
				"Char myMethod(String string);\n" +
			"}";

		Compiler compiler = new Compiler("thingy/MyInterface.yg", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 4:5 Duplicate declaration of method myMethod(java.lang.String).\n",
			result.getErrors());
	}

	@Test
	public void testMethodsWithSameErasure()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"private MyClass() {}\n" +
				"private Void method(Array<String> objs) {}\n" +
				"private Void method(Array<Object> objs) {}\n" +
			"}\n";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 5:13 Methods method(yirgacheffe.lang.Array<java.lang.String>) and " +
			"method(yirgacheffe.lang.Array<java.lang.Object>) have the same erasure.\n",
			result.getErrors());
	}

	@Test
	public void testCallingBooleanMethod()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"main method(Array<String> args)\n" +
				"{\n" +
					"this.isTrue();\n" +
				"}\n\n" +
				"public Bool isTrue()" +
				"{\n" +
					"return true;\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());
	}

	@Test
	public void testMissingReturnStatement()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Num myMethod()" +
				"{" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals("line 3:0 Missing return statement.\n", result.getErrors());
	}

	@Test
	public void testNonConstantReturnType() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"main method(Array<String> args)\n" +
				"{\n" +
					"this.method(\"\");\n" +
				"}\n" +
				"private String method(String string)\n" +
				"{\n" +
					"return string;\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());

		assertEquals(
			"line 7:15 Overloaded method method does not have constant return type.\n",
			result.getErrors());
	}
}
