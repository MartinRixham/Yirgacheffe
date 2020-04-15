package yirgacheffe.compiler.listener;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
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
	public void testImplementsInterface()
	{
		String source =
			"class MyClass implements Appendable\n" +
			"{\n" +
				"public MyClass() {}\n" +
				"public Appendable append(Char c) { return this; }\n" +
				"public Appendable append(CharSequence csq) { return this; }\n" +
				"public Appendable append(CharSequence csq, Num start, Num end)\n" +
				"{\n" +
					"return this;\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());
	}

	@Test
	public void testImplementsInterfaceWithoutBridgeMethod()
	{
		String source =
			"import java.util.Observer;\n" +
			"import java.util.Observable;\n" +
			"class MyClass implements Observer\n" +
			"{\n" +
				"public MyClass() {}\n" +
				"public Void update(Observable o, Object arg) { return; }\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List methods = classNode.methods;

		assertEquals(3, methods.size());
	}

	@Test
	public void testPrivateMethodDoesntImplementInterface()
	{
		String source =
			"import java.util.Observer;\n" +
			"import java.util.Observable;\n" +
			"class MyClass implements Observer\n" +
			"{\n" +
				"public MyClass() {}\n" +
				"private Void update(Observable o, Object arg) { return; }\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
	}

	@Test
	public void testImplementsMissingType()
	{
		String source =
			"class MyClass implements { public MyClass() {} }";

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
			"class MyClass implements Bool { public MyClass() {} }";

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
			"class MyClass implements Object { public MyClass() {} }";

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
			"class MyClass implements Comparable<String> { public MyClass() {} }";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

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
				"public Num compareTo(String other) { return 0; }\n" +
				"public MyClass() {}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

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

		MethodNode bridgeMethod = classNode.methods.get(0);

		assertEquals("compareTo", bridgeMethod.name);
		assertEquals(Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC, bridgeMethod.access);
		assertEquals("(Ljava/lang/Object;)I", bridgeMethod.desc);

		InsnList instructions = bridgeMethod.instructions;

		assertEquals(6, instructions.size());
		assertEquals(2, bridgeMethod.maxLocals);
		assertEquals(2, bridgeMethod.maxStack);

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.ALOAD, secondInstruction.getOpcode());
		assertEquals(1, secondInstruction.var);

		TypeInsnNode thirdInstruction = (TypeInsnNode) instructions.get(2);

		assertEquals(Opcodes.CHECKCAST, thirdInstruction.getOpcode());
		assertEquals("java/lang/String", thirdInstruction.desc);

		MethodInsnNode fourthInstruction = (MethodInsnNode) instructions.get(3);

		assertEquals("MyClass", fourthInstruction.owner);
		assertEquals("compareTo", fourthInstruction.name);
		assertEquals("(Ljava/lang/String;)D", fourthInstruction.desc);
		assertEquals(false, fourthInstruction.itf);

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.D2I, fifthInstruction.getOpcode());

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.IRETURN, sixthInstruction.getOpcode());

		MethodNode method = classNode.methods.get(1);

		assertEquals("compareTo", method.name);
		assertEquals(Opcodes.ACC_PROTECTED, method.access);
		assertEquals("(Ljava/lang/String;)D", method.desc);
	}

	@Test
	public void testImplementsComparableWithObjectParameter()
	{
		String source =
			"class MyClass implements Comparable<String>\n" +
			"{\n" +
				"public Num compareTo(Object other) { return 0; }\n" +
				"public MyClass() {}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

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

		MethodNode bridgeMethod = classNode.methods.get(0);

		assertEquals("compareTo", bridgeMethod.name);
		assertEquals(Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC, bridgeMethod.access);
		assertEquals("(Ljava/lang/Object;)I", bridgeMethod.desc);

		InsnList instructions = bridgeMethod.instructions;

		//assertEquals(5, instructions.size());
		assertEquals(2, bridgeMethod.maxLocals);
		assertEquals(2, bridgeMethod.maxStack);

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.ALOAD, secondInstruction.getOpcode());
		assertEquals(1, secondInstruction.var);

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals("MyClass", thirdInstruction.owner);
		assertEquals("compareTo", thirdInstruction.name);
		assertEquals("(Ljava/lang/Object;)D", thirdInstruction.desc);
		assertEquals(false, thirdInstruction.itf);

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.D2I, fourthInstruction.getOpcode());

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.IRETURN, fifthInstruction.getOpcode());

		MethodNode method = classNode.methods.get(1);

		assertEquals("compareTo", method.name);
		assertEquals(Opcodes.ACC_PROTECTED, method.access);
		assertEquals("(Ljava/lang/Object;)D", method.desc);
	}

	@Test
	public void testImplementsComparableWithTypeParameter()
	{
		String source =
			"class MyClass<T> implements Comparable<T>\n" +
			"{\n" +
				"public Num compareTo(T other) { return other.hashCode(); }\n" +
				"public MyClass() {}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());
		assertEquals("MyClass.class", result.getClassFileName());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List interfaces = classNode.interfaces;

		assertEquals(1, classNode.interfaces.size());
		assertEquals("java/lang/Comparable", interfaces.get(0));
		assertEquals(
			"<T:Ljava/lang/Object;>Ljava/lang/Object;Ljava/lang/Comparable<TT;>;",
			classNode.signature);

		assertEquals(4, classNode.methods.size());

		MethodNode method = classNode.methods.get(1);

		assertEquals("compareTo", method.name);
		assertEquals(Opcodes.ACC_PROTECTED, method.access);
		assertEquals("(Ljava/lang/Object;)D", method.desc);
		assertEquals("(TT;)D", method.signature);

		InsnList instructions = method.instructions;

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);

		assertTrue(instructions.get(1) instanceof LabelNode);
		assertTrue(instructions.get(2) instanceof LineNumberNode);

		InvokeDynamicInsnNode fourthInstruction =
			(InvokeDynamicInsnNode) instructions.get(3);

		assertEquals(Opcodes.INVOKEDYNAMIC, fourthInstruction.getOpcode());
		assertEquals("(Ljava/lang/Object;)I", fourthInstruction.desc);
	}

	@Test
	public void testImplementsWithSupertypeParameter()
	{
		String interfaceSource =
			"interface Objectifier\n" +
			"{" +
				"Object objectify(String string);\n" +
			"}";

		String source =
			"class MyClass implements Objectifier\n" +
			"{\n" +
				"public Object objectify(Object obj) { return obj; }\n" +
				"public MyClass() {}\n" +
			"}";

		Classes classes = new Classes();
		Compiler interfaceCompiler = new Compiler("", interfaceSource);
		Compiler compiler = new Compiler("", source);

		interfaceCompiler.compileClassDeclaration(classes);
		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		interfaceCompiler.compileInterface(classes);
		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());
		assertEquals("MyClass.class", result.getClassFileName());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List interfaces = classNode.interfaces;

		assertEquals(1, classNode.interfaces.size());
		assertEquals("Objectifier", interfaces.get(0));
		assertEquals(
			"Ljava/lang/Object;LObjectifier;",
			classNode.signature);

		MethodNode bridgeMethod = classNode.methods.get(0);

		assertEquals("objectify", bridgeMethod.name);
		assertEquals(Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC, bridgeMethod.access);
		assertEquals("(Ljava/lang/String;)Ljava/lang/Object;", bridgeMethod.desc);

		InsnList instructions = bridgeMethod.instructions;

		assertEquals(4, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.ALOAD, secondInstruction.getOpcode());
		assertEquals(1, secondInstruction.var);

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals("MyClass", thirdInstruction.owner);
		assertEquals("objectify", thirdInstruction.name);
		assertEquals("(Ljava/lang/Object;)Ljava/lang/Object;", thirdInstruction.desc);
		assertEquals(false, thirdInstruction.itf);

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.ARETURN, fourthInstruction.getOpcode());

		MethodNode method = classNode.methods.get(1);

		assertEquals("objectify", method.name);
		assertEquals(Opcodes.ACC_PUBLIC, method.access);
		assertEquals("(Ljava/lang/Object;)Ljava/lang/Object;", method.desc);
	}

	@Test
	public void testImplementsWithTypeVariance()
	{
		String interfaceSource =
			"interface Objectifier\n" +
			"{" +
				"Object objectify(String string);\n" +
			"}";

		String source =
			"class MyClass implements Objectifier\n" +
			"{\n" +
				"public String objectify(Object obj) { return obj.toString(); }\n" +
				"public MyClass() {}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);
		Compiler interfaceCompiler = new Compiler("", interfaceSource);

		interfaceCompiler.compileClassDeclaration(classes);
		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		interfaceCompiler.compileInterface(classes);
		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());
		assertEquals("MyClass.class", result.getClassFileName());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List interfaces = classNode.interfaces;

		assertEquals(1, classNode.interfaces.size());
		assertEquals("Objectifier", interfaces.get(0));
		assertEquals(
			"Ljava/lang/Object;LObjectifier;",
			classNode.signature);

		MethodNode bridgeMethod = classNode.methods.get(0);

		assertEquals("objectify", bridgeMethod.name);
		assertEquals(Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC, bridgeMethod.access);
		assertEquals("(Ljava/lang/String;)Ljava/lang/Object;", bridgeMethod.desc);

		InsnList instructions = bridgeMethod.instructions;

		assertEquals(4, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.ALOAD, secondInstruction.getOpcode());
		assertEquals(1, secondInstruction.var);

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals("MyClass", thirdInstruction.owner);
		assertEquals("objectify", thirdInstruction.name);
		assertEquals("(Ljava/lang/Object;)Ljava/lang/String;", thirdInstruction.desc);
		assertEquals(false, thirdInstruction.itf);

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.ARETURN, fourthInstruction.getOpcode());

		MethodNode method = classNode.methods.get(1);

		assertEquals("objectify", method.name);
		assertEquals(Opcodes.ACC_PROTECTED, method.access);
		assertEquals("(Ljava/lang/Object;)Ljava/lang/String;", method.desc);
	}

	@Test
	public void testImplementGenericInterface()
	{
		String interfaceSource =
			"interface MyInterface<T>\n" +
			"{" +
				"T getString(T string);\n" +
			"}";

		String source =
			"class MyClass implements MyInterface<String>\n" +
			"{\n" +
				"public String getString(String str) { return str.toString(); }\n" +
				"public MyClass() {}\n" +
			"}";

		Classes classes = new Classes();

		new Compiler("", interfaceSource).compileInterface(classes);

		classes.clearCache();

		Compiler interfaceCompiler = new Compiler("", interfaceSource);
		CompilationResult interfaceResult = interfaceCompiler.compile(classes);

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(classes);

		assertTrue(interfaceResult.isSuccessful());
		assertTrue(result.isSuccessful());
		assertEquals("MyClass.class", result.getClassFileName());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List interfaces = classNode.interfaces;

		assertEquals(1, classNode.interfaces.size());
		assertEquals("MyInterface", interfaces.get(0));
		assertEquals(
			"Ljava/lang/Object;LMyInterface<Ljava/lang/String;>;",
			classNode.signature);
	}

	@Test
	public void testImplementComparableOfNum()
	{
		String source =
			"class MyClass implements Comparable<Num>\n" +
			"{\n" +
				"public MyClass() {}\n" +
				"public Num compareTo(Num other)\n" +
				"{\n" +
					"return 0;\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());
	}

	@Test
	public void testTypeParameterWithTypeBounds()
	{
		String source =
			"class Fold<R implements Combinable<S>, S>\n" +
			"{\n" +
				"Array<R> array;\n" +
				"public Fold(Array<R> array)\n" +
				"{\n" +
					"this.array = array;\n" +
				"}\n" +
				"public S with(S item)\n" +
				"{\n" +
					"for (Num i = 0; i < this.array.length(); i++)\n" +
					"{\n" +
						"item = this.array.get(i).combineWith(item);\n" +
					"}\n" +
					"return item;\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());
	}

	@Test
	public void testTypeParameterWithParameterisedTypeBounds()
	{
		String source =
			"class Fold<R implements Combinable<String>>\n" +
			"{\n" +
				"Array<R> array;\n" +
				"public Fold(Array<R> array)\n" +
				"{\n" +
					"this.array = array;\n" +
				"}\n" +
				"public String with(String item)\n" +
				"{\n" +
					"for (Num i = 0; i < this.array.length(); i++)\n" +
					"{\n" +
						"item = this.array.get(i).combineWith(item);\n" +
					"}\n" +
					"return item;\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());
	}

	@Test
	public void testRecursiveTypeBound()
	{
		String source =
			"class Reduce<S implements Combinable<S>>\n" +
			"{\n" +
				"Array<S> array;\n" +
				"public Reduce(Array<S> array)\n" +
				"{\n" +
					"this.array = array;\n" +
				"}\n" +
				"public S to()\n" +
				"{\n" +
					"S item = this.array.get(0);\n" +
					"for (Num i = 1; i < this.array.length(); i++)\n" +
					"{\n" +
						"item = this.array.get(i)" +
							".combineWith(item).combineWith(item).combineWith(item);\n" +
					"}\n" +
					"return item;\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode method = classNode.methods.get(2);

		assertEquals("to", method.name);

		InsnList instructions = method.instructions;

		assertEquals(54, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);
	}
}
