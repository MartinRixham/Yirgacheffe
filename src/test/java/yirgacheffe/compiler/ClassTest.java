package yirgacheffe.compiler;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

public class ClassTest
{
	@Test
	public void testParseError() throws Exception
	{
		Yirgacheffe yirgacheffe = new Yirgacheffe("interface MyInterface {");

		CompilationResult result = yirgacheffe.compile();

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:23 mismatched input '<EOF>' expecting {'}', Type, Modifier}\n",
			result.getErrors());
	}

	@Test
	public void testNamedEmptyInterface() throws Exception
	{
		Yirgacheffe yirgacheffe = new Yirgacheffe("interface MyInterface {}");

		CompilationResult result = yirgacheffe.compile();

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		int access = Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE;

		assertTrue(result.isSuccessful());
		assertEquals("MyInterface", classNode.name);
		assertEquals(access, classNode.access);
		assertEquals(0, classNode.fields.size());
	}

	@Test
	public void testFailToDeclareClassOrInterface() throws Exception
	{
		Yirgacheffe yirgacheffe = new Yirgacheffe("thingy MyInterface {}");

		CompilationResult result = yirgacheffe.compile();

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:0 Expected declaration of class or interface.\n",
			result.getErrors());
	}

	@Test
	public void testNamedEmptyClass() throws Exception
	{
		Yirgacheffe yirgacheffe = new Yirgacheffe("class MyClass {}");

		CompilationResult result = yirgacheffe.compile();

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		assertTrue(result.isSuccessful());
		assertEquals("MyClass", classNode.name);
		assertEquals(Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, classNode.access);
		assertEquals(0, classNode.fields.size());
	}

	@Test
	public void testInterfaceWithMissingIdentifier() throws Exception
	{
		String source =
			"interface\n" +
				"{\n" +
				"}";

		Yirgacheffe yirgacheffe = new Yirgacheffe(source);

		CompilationResult result = yirgacheffe.compile();

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:0 Interface identifier expected.\n",
			result.getErrors());
	}

	@Test
	public void testClassWithMissingIdentifier() throws Exception
	{
		String source =
			"class\n" +
				"{\n" +
				"}";

		Yirgacheffe yirgacheffe = new Yirgacheffe(source);

		CompilationResult result = yirgacheffe.compile();

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:0 Class identifier expected.\n",
			result.getErrors());
	}
}
