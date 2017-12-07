package yirgacheffe.compiler;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

public class MethodTest
{
	@Test
	public void testInterfaceWithMethod() throws Exception
	{
		String source =
			"interface MyInterface\n" +
				"{\n" +
				"num myMethod();\n" +
				"}";

		Yirgacheffe yirgacheffe = new Yirgacheffe(source);

		CompilationResult result = yirgacheffe.compile();

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

		Yirgacheffe yirgacheffe = new Yirgacheffe(source);

		CompilationResult result = yirgacheffe.compile();

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

		Yirgacheffe yirgacheffe = new Yirgacheffe(source);

		CompilationResult result = yirgacheffe.compile();

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

		Yirgacheffe yirgacheffe = new Yirgacheffe(source);

		CompilationResult result = yirgacheffe.compile();

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:13 Expected type before argument identifier\n",
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

		Yirgacheffe yirgacheffe = new Yirgacheffe(source);

		CompilationResult result = yirgacheffe.compile();

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;

		assertEquals(2, methods.size());

		MethodNode firstMethod = methods.get(0);

		assertEquals("<init>", firstMethod.name);

		MethodNode secondMethod = methods.get(1);

		assertEquals("(Ljava/lang/String;D)D", secondMethod.desc);
		assertEquals(Opcodes.ACC_PUBLIC, secondMethod.access);
		assertEquals("myMethod", secondMethod.name);
	}

	@Test
	public void testClassWithPrivateMethod() throws Exception
	{
		String source =
			"class MyClass\n" +
				"{\n" +
				"private num myMethod(String param1, num param2) {}\n" +
				"}";

		Yirgacheffe yirgacheffe = new Yirgacheffe(source);

		CompilationResult result = yirgacheffe.compile();

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;

		assertEquals(2, methods.size());

		MethodNode firstMethod = methods.get(0);

		assertEquals("<init>", firstMethod.name);

		MethodNode secondMethod = methods.get(1);

		assertEquals("(Ljava/lang/String;D)D", secondMethod.desc);
		assertEquals(Opcodes.ACC_PRIVATE, secondMethod.access);
		assertEquals("myMethod", secondMethod.name);
	}
}
