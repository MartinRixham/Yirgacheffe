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
				"int myMethod();\n" +
				"}";

		Yirgacheffe yirgacheffe = new Yirgacheffe(source);

		CompilationResult result = yirgacheffe.compile();

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;

		assertTrue(result.isSuccessful());
		assertEquals(1, methods.size());

		MethodNode firstMethod = methods.get(0);

		assertEquals(Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT, firstMethod.access);
		assertEquals("()I", firstMethod.desc);
		assertEquals("myMethod", firstMethod.name);
	}

	@Test
	public void testInterfaceMethodWithModifier() throws Exception
	{
		String source =
			"interface MyInterface\n" +
				"{\n" +
				"public int myInterfaceMethod();\n" +
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
				"int myParameterisedMethod(String param1, int param2);\n" +
				"}";

		Yirgacheffe yirgacheffe = new Yirgacheffe(source);

		CompilationResult result = yirgacheffe.compile();

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;
		MethodNode firstMethod = methods.get(0);

		assertTrue(result.isSuccessful());
		assertEquals("(Ljava/lang/String;I)I", firstMethod.desc);
		assertEquals("myParameterisedMethod", firstMethod.name);
	}

	@Test
	public void testParameterWithMissingType() throws Exception
	{
		String source =
			"interface MyInterface\n" +
				"{\n" +
				"int myMethod(param1, int param2);\n" +
				"}";

		Yirgacheffe yirgacheffe = new Yirgacheffe(source);

		CompilationResult result = yirgacheffe.compile();

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:13 Expected type before argument identifier\n",
			result.getErrors());
	}

	@Test
	public void testClassWithMethod() throws Exception
	{
		String source =
			"class MyClass\n" +
				"{\n" +
				"int myMethod(String param1, int param2) {}\n" +
				"}";

		Yirgacheffe yirgacheffe = new Yirgacheffe(source);

		CompilationResult result = yirgacheffe.compile();

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;

		assertTrue(result.isSuccessful());
		assertEquals(1, methods.size());

		MethodNode firstMethod = methods.get(0);

		assertEquals("(Ljava/lang/String;I)I", firstMethod.desc);
		assertEquals("myMethod", firstMethod.name);
		assertEquals("myMethod", firstMethod.name);
	}
}