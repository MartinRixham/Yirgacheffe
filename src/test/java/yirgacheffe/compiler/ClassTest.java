package yirgacheffe.compiler;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

public class ClassTest
{
	@Test
	public void testParseError() throws Exception
	{
		String source = "interface MyInterface {";
		InputStream inputStream = new ByteArrayInputStream(source.getBytes());
		Compiler compiler = new Compiler(inputStream);
		CompilationResult result = compiler.compile();

		assertFalse(result.isSuccessful());
		assertEquals(1, result.getErrors().split("\n").length);
		assertEquals(
			"line 1:23 mismatched input",
			result.getErrors().substring(0, 26));
	}

	@Test
	public void testNamedEmptyInterface() throws Exception
	{
		String source = "interface MyInterface {}";
		InputStream inputStream = new ByteArrayInputStream(source.getBytes());
		Compiler compiler = new Compiler(inputStream);
		CompilationResult result = compiler.compile();

		assertTrue(result.isSuccessful());
		assertEquals("MyInterface.class", result.getClassFileName());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		int access = Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE;

		assertEquals("MyInterface", classNode.name);
		assertEquals(access, classNode.access);
		assertEquals(0, classNode.fields.size());
	}

	@Test
	public void testFailToDeclareClassOrInterface() throws Exception
	{
		String source = "thingy MyInterface {}";
		InputStream inputStream = new ByteArrayInputStream(source.getBytes());
		Compiler compiler = new Compiler(inputStream);
		CompilationResult result = compiler.compile();

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:0 Expected declaration of class or interface.\n",
			result.getErrors());
	}

	@Test
	public void testNamedEmptyClass() throws Exception
	{
		String source = "class MyClass {}";
		InputStream inputStream = new ByteArrayInputStream(source.getBytes());
		Compiler compiler = new Compiler(inputStream);
		CompilationResult result = compiler.compile();

		assertTrue(result.isSuccessful());
		assertEquals("MyClass.class", result.getClassFileName());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		assertEquals("MyClass", classNode.name);
		assertEquals(Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, classNode.access);
		assertEquals(0, classNode.fields.size());

		List<MethodNode> methods = classNode.methods;

		assertEquals(1, methods.size());

		MethodNode constructor = methods.get(0);

		assertEquals("()V", constructor.desc);
		assertEquals(Opcodes.ACC_PUBLIC, constructor.access);
		assertEquals("<init>", constructor.name);
	}

	@Test
	public void testInterfaceWithMissingIdentifier() throws Exception
	{
		String source =
			"interface\n" +
				"{\n" +
				"}";

		InputStream inputStream = new ByteArrayInputStream(source.getBytes());
		Compiler compiler = new Compiler(inputStream);
		CompilationResult result = compiler.compile();

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

		InputStream inputStream = new ByteArrayInputStream(source.getBytes());
		Compiler compiler = new Compiler(inputStream);
		CompilationResult result = compiler.compile();

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:0 Class identifier expected.\n",
			result.getErrors());
	}
}
