package yirgacheffe.compiler;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashMap;
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
		Compiler compiler = new Compiler("", source);
		CompilationResult result =
			compiler.compile(new HashMap<>(), new BytecodeClassLoader());

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
		Compiler compiler = new Compiler("", source);
		CompilationResult result =
			compiler.compile(new HashMap<>(), new BytecodeClassLoader());

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
		Compiler compiler = new Compiler("", source);
		CompilationResult result =
			compiler.compile(new HashMap<>(), new BytecodeClassLoader());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:0 Expected declaration of class or interface.\n",
			result.getErrors());
	}

	@Test
	public void testNamedEmptyClass() throws Exception
	{
		String source = "class MyClass {}";
		Compiler compiler = new Compiler("", source);
		CompilationResult result =
			compiler.compile(new HashMap<>(), new BytecodeClassLoader());

		assertTrue(result.isSuccessful());
		assertEquals("MyClass.class", result.getClassFileName());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

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

		Compiler compiler = new Compiler("", source);
		CompilationResult result =
			compiler.compile(new HashMap<>(), new BytecodeClassLoader());

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

		Compiler compiler = new Compiler("", source);
		CompilationResult result =
			compiler.compile(new HashMap<>(), new BytecodeClassLoader());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:0 Class identifier expected.\n",
			result.getErrors());
	}

	@Test
	public void testClassInPackage() throws Exception
	{
		String source = "package myPackage; class MyClass {}";
		Compiler compiler = new Compiler("myPackage/", source);
		CompilationResult result =
			compiler.compile(new HashMap<>(), new BytecodeClassLoader());

		assertTrue(result.isSuccessful());
		assertEquals("myPackage/MyClass.class", result.getClassFileName());
	}

	@Test
	public void testClassInNestedPackage() throws Exception
	{
		String source = "package myPackage.thingy; class MyClass {}";
		Compiler compiler = new Compiler("myPackage/thingy/", source);
		CompilationResult result =
			compiler.compile(new HashMap<>(), new BytecodeClassLoader());

		assertTrue(result.isSuccessful());
		assertEquals("myPackage/thingy/MyClass.class", result.getClassFileName());
	}

	@Test
	public void testClassInPackageWrongPackage() throws Exception
	{
		String source = "package myPackage.wibble; class MyClass {}";
		Compiler compiler = new Compiler("anotherPackage/wibble/", source);
		CompilationResult result =
			compiler.compile(new HashMap<>(), new BytecodeClassLoader());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:8 Package name myPackage.wibble " +
				"does not correspond to the file path anotherPackage/wibble/.\n",
			result.getErrors());
	}

	@Test
	public void testPackagedClass() throws Exception
	{
		HashMap<String, DeclaredType> declaredTypes = new HashMap<>();
		BytecodeClassLoader classLoader = new BytecodeClassLoader();
		String source = "package this.that; interface MyInterface {}";
		Compiler compiler = new Compiler("this/that/", source);
		CompilationResult result =
			compiler.compileClassDeclaration(declaredTypes, classLoader);

		assertTrue(result.isSuccessful());

		source =
			"package this.that;\n" +
			"interface AnotherInterface\n" +
				"{\n" +
				"MyInterface myMethod();\n" +
				"}";

		compiler = new Compiler("this/that/", source);
		result = compiler.compile(declaredTypes, classLoader);

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;
		MethodNode method = methods.get(0);

		assertEquals("()Lthis/that/MyInterface;", method.desc);
	}
}
