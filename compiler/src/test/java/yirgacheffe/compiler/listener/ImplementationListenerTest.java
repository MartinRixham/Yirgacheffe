package yirgacheffe.compiler.listener;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.type.Classes;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ImplementationListenerTest
{
	@Test
	public void testImplementsMissingType()
	{
		String source =
			"class MyClass implements {}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:14 Missing implemented type.\n",
			result.getErrors());
	}

	@Test
	public void testFailToImplementPrimitiveType()
	{
		String source =
			"class MyClass implements Bool {}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:25 Cannot implement primitive type Bool.\n",
			result.getErrors());
	}

	@Test
	public void testFailToImplementClass()
	{
		String source =
			"class MyClass implements Object {}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:25 Cannot implement concrete type java.lang.Object.\n",
			result.getErrors());
	}

	@Test
	public void testFailToImplementInterface()
	{
		String source =
			"class MyClass implements Comparable<String> {}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:0 Missing implementation of interface method " +
				"Num compareTo(java.lang.String).\n",
			result.getErrors());
	}

	@Test
	public void testImplementsComparable()
	{
		String source =
			"class MyClass implements Comparable<String>\n" +
			"{\n" +
				"public Num compareTo(String other) { return 0; }" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());
		assertEquals("MyClass.class", result.getClassFileName());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List interfaces = classNode.interfaces;

		assertEquals(1, classNode.interfaces.size());
		assertEquals("java/lang/Comparable", interfaces.get(0));
		assertEquals(
			"Ljava/lang/Object;Ljava/lang/Comparable<Ljava/lang/String;>;",
			classNode.signature);
	}
}
