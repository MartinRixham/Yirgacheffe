package yirgacheffe.compiler.listener;

import org.junit.Ignore;
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

public class ClassListenerTest
{
	@Test
	public void testParseError() throws Exception
	{
		String source = "interface MyInterface {{";
		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(1, result.getErrors().split("\n").length);
		assertEquals("line 1:23 mismatched input", result.getErrors().substring(0, 26));
	}

	@Test
	public void testNamedEmptyInterface() throws Exception
	{
		String source = "interface MyInterface {}";
		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

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
	public void testMultipleClassDeclarations() throws Exception
	{
		String source = "interface MyInterface {} class MyClass {}";
		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:0 File contains multiple class declarations.\n",
			result.getErrors());
	}

	@Test
	public void testFailToDeclareClassOrInterface() throws Exception
	{
		String source = "thingy MyInterface {}";
		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

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
		CompilationResult result = compiler.compile(new Classes());

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
		CompilationResult result = compiler.compile(new Classes());

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
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:0 Class identifier expected.\n",
			result.getErrors());
	}

	@Ignore
	@Test
	public void testClassWithMissingCloseBlock() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 2:1 Missing '}'.\n",
			result.getErrors());
	}

	@Test
	public void testClassInPackage() throws Exception
	{
		String source = "package myPackage; class MyClass {}";
		Compiler compiler = new Compiler("myPackage/gile.gg", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());
		assertEquals("myPackage/MyClass.class", result.getClassFileName());
	}

	@Test
	public void testClassInNestedPackage() throws Exception
	{
		String source = "package myPackage.thingy; class MyClass {}";
		Compiler compiler = new Compiler("myPackage/thingy/file.yg", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());
		assertEquals("myPackage/thingy/MyClass.class", result.getClassFileName());
	}

	@Test
	public void testClassWithMissingPackage() throws Exception
	{
		String source = "class MyClass {}";
		Compiler compiler = new Compiler("anotherPackage/wibble/file.yg", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:0 Missing package declaration " +
				"for file path anotherPackage/wibble/.\n",
			result.getErrors());
	}

	@Test
	public void testClassInPackageWrongPackage() throws Exception
	{
		String source = "package myPackage.wibble; class MyClass {}";
		Compiler compiler = new Compiler("anotherPackage/wibble/file.yg", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:8 Package name myPackage.wibble " +
				"does not correspond to the file path anotherPackage/wibble/.\n",
			result.getErrors());
	}

	@Test
	public void testPackagedClass() throws Exception
	{
		Classes classes = new Classes();
		String source = "package tis.that; interface MyInterface {}";
		Compiler compiler = new Compiler("tis/that/MyInterface.yg", source);
		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		source =
			"package tis.that;\n" +
			"interface AnotherInterface\n" +
				"{\n" +
				"MyInterface myMethod();\n" +
				"}";

		compiler = new Compiler("tis/that/AnotherInterface.yg", source);
		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;
		MethodNode method = methods.get(0);

		assertEquals("()Ltis/that/MyInterface;", method.desc);
	}

	@Test
	public void testClassWithInterfaceMethod() throws Exception
	{
		String source =
			"class MyClass\n" +
				"{\n" +
					"String myMethod();\n" +
				"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals("line 3:0 Method requires method body.\n", result.getErrors());
	}

	@Test
	public void testInterfaceWithClassMethod() throws Exception
	{
		String source =
			"interface MyInterface\n" +
				"{\n" +
				"public String myMethod() {}\n" +
				"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:0 Method body not permitted for interface method.\n",
			result.getErrors());
	}

	@Test
	public void testImportUnknownType() throws Exception
	{
		String source =
			"import java.util.Liszt;\n" +
			"class MyClass {}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:7 Unrecognised type: java.util.Liszt is not a type.\n",
			result.getErrors());
	}
}
