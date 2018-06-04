package yirgacheffe.compiler.listener;

import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
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

public class MainMethodListenerTest
{
	@Test
	public void testMainMethod()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"main myMainMethod(Array<String> args) {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;

		assertEquals(3, methods.size());

		MethodNode method = methods.get(0);

		assertEquals("(Lyirgacheffe/lang/Array;)V", method.desc);
		assertEquals(Opcodes.ACC_PUBLIC, method.access);
		assertEquals("myMainMethod", method.name);

		MethodNode mainMethod = methods.get(1);

		assertEquals("([Ljava/lang/String;)V", mainMethod.desc);
		assertEquals(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, mainMethod.access);
		assertEquals("main", mainMethod.name);
		assertEquals(4, mainMethod.maxStack);
		assertEquals(1, mainMethod.maxLocals);

		InsnList instructions = mainMethod.instructions;

		TypeInsnNode firstInstruction = (TypeInsnNode) instructions.get(0);

		assertEquals(Opcodes.NEW, firstInstruction.getOpcode());
		assertEquals("MyClass", firstInstruction.desc);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKESPECIAL, thirdInstruction.getOpcode());
		assertEquals("MyClass", thirdInstruction.owner);
		assertEquals("<init>", thirdInstruction.name);
		assertEquals("()V", thirdInstruction.desc);
		assertFalse(thirdInstruction.itf);

		TypeInsnNode fourthInstruction = (TypeInsnNode) instructions.get(3);

		assertEquals(Opcodes.NEW, fourthInstruction.getOpcode());
		assertEquals("yirgacheffe/lang/Array", fourthInstruction.desc);

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.DUP, fifthInstruction.getOpcode());

		VarInsnNode sixthInstruction = (VarInsnNode) instructions.get(5);

		assertEquals(Opcodes.ALOAD, sixthInstruction.getOpcode());
		assertEquals(0, sixthInstruction.var);

		MethodInsnNode seventhInstruction = (MethodInsnNode) instructions.get(6);

		assertEquals(Opcodes.INVOKESPECIAL, seventhInstruction.getOpcode());
		assertEquals("yirgacheffe/lang/Array", seventhInstruction.owner);
		assertEquals("<init>", seventhInstruction.name);
		assertEquals("([Ljava/lang/Object;)V", seventhInstruction.desc);
		assertFalse(seventhInstruction.itf);

		MethodInsnNode eighthInstruction = (MethodInsnNode) instructions.get(7);

		assertEquals(Opcodes.INVOKEVIRTUAL, eighthInstruction.getOpcode());
		assertEquals("MyClass", eighthInstruction.owner);
		assertEquals("myMainMethod", eighthInstruction.name);
		assertEquals("(Lyirgacheffe/lang/Array;)V", eighthInstruction.desc);
		assertFalse(eighthInstruction.itf);

		InsnNode ninthInstruction = (InsnNode) instructions.get(8);

		assertEquals(Opcodes.RETURN, ninthInstruction.getOpcode());
	}

	@Ignore
	@Test
	public void testMainMethodInstantiatesItself()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"main myMainMethod(Array<String> args)" +
				"{" +
					"MyClass myClass = new MyClass();" +
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
}
