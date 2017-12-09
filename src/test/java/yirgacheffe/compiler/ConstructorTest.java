package yirgacheffe.compiler;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConstructorTest
{
	@Test
	public void testEmptyClassHasDefaultConstructor() throws Exception
	{
		String source = "class MyClass {}";
		Compiler compiler = new Compiler("", source);
		CompilationResult result =
			compiler.compile(new HashMap<>(), new BytecodeClassLoader());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;

		assertEquals(1, methods.size());

		MethodNode constructor = methods.get(0);

		assertEquals("()V", constructor.desc);
		assertEquals(Opcodes.ACC_PUBLIC, constructor.access);
		assertEquals("<init>", constructor.name);
	}

	@Test
	public void testConstructorWithNumberParameter() throws Exception
	{
		String source =
			"class MyClass\n" +
				"{\n" +
				"public MyClass(num param) {}\n" +
				"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result =
			compiler.compile(new HashMap<>(), new BytecodeClassLoader());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;

		assertEquals(1, methods.size());

		MethodNode constructor = methods.get(0);

		assertEquals("(D)V", constructor.desc);
		assertEquals(Opcodes.ACC_PUBLIC, constructor.access);
		assertEquals("<init>", constructor.name);
	}

	@Test
	public void testConstructorWithMissingModifier() throws Exception
	{
		String source =
			"class MyClass\n" +
				"{\n" +
				"MyClass(num param) {}\n" +
				"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result =
			compiler.compile(new HashMap<>(), new BytecodeClassLoader());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:0 Expected public or private access modifier " +
				"at start of constructor declaration.\n",
			result.getErrors());
	}
}
