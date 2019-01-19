package yirgacheffe.compiler.listener;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.lang.Array;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParallelMethodListenerTest
{
	@Test
	public void testParallelMethod()
	{
		String source =
			"package myPackage;\n" +
			"class MyClass\n" +
			"{\n" +
				"parallel public Comparable<String> method()" +
				"{\n" +
					"return \"thingy\";\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("myPackage/MyClass.yg", source);

		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List methods = classNode.methods;
		MethodNode firstMethod = (MethodNode) methods.get(0);

		InsnList instructions = firstMethod.instructions;

		assertEquals(11, instructions.size());

		TypeInsnNode firstInstruction = (TypeInsnNode) instructions.get(0);

		assertEquals(Opcodes.NEW, firstInstruction.getOpcode());
		assertEquals("myPackage/MyClass$method", firstInstruction.desc);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKESPECIAL, thirdInstruction.getOpcode());
		assertEquals("myPackage/MyClass$method", thirdInstruction.owner);
		assertEquals("<init>", thirdInstruction.name);
		assertEquals("()V", thirdInstruction.desc);

		VarInsnNode fourthInstruction = (VarInsnNode) instructions.get(3);

		assertEquals(Opcodes.ASTORE, fourthInstruction.getOpcode());
		assertEquals(1, fourthInstruction.var);

		TypeInsnNode fifthInstruction = (TypeInsnNode) instructions.get(4);

		assertEquals(Opcodes.NEW, fifthInstruction.getOpcode());
		assertEquals("java/lang/Thread", fifthInstruction.desc);

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.DUP, sixthInstruction.getOpcode());

		VarInsnNode seventhInstructions = (VarInsnNode) instructions.get(6);

		assertEquals(Opcodes.ALOAD, seventhInstructions.getOpcode());
		assertEquals(1, seventhInstructions.var);

		MethodInsnNode eighthInstruction = (MethodInsnNode) instructions.get(7);

		assertEquals(Opcodes.INVOKESPECIAL, eighthInstruction.getOpcode());
		assertEquals("java/lang/Thread", eighthInstruction.owner);
		assertEquals("<init>", eighthInstruction.name);
		assertEquals("(Ljava/lang/Runnable;)V", eighthInstruction.desc);

		MethodInsnNode ninthInstruction = (MethodInsnNode) instructions.get(8);

		assertEquals(Opcodes.INVOKEVIRTUAL, ninthInstruction.getOpcode());
		assertEquals("java/lang/Thread", ninthInstruction.owner);
		assertEquals("start", ninthInstruction.name);
		assertEquals("()V", ninthInstruction.desc);

		VarInsnNode tenthInstruction = (VarInsnNode) instructions.get(9);

		assertEquals(Opcodes.ALOAD, tenthInstruction.getOpcode());
		assertEquals(1, tenthInstruction.var);

		InsnNode eleventhInstruction = (InsnNode) instructions.get(10);

		assertEquals(Opcodes.ARETURN, eleventhInstruction.getOpcode());

		this.testGeneratedClass(result);
	}

	private void testGeneratedClass(CompilationResult result)
	{
		Array<byte[]> generatedClasses = result.getGeneratedClasses();

		assertEquals(1, generatedClasses.length());

		ClassReader reader = new ClassReader(generatedClasses.get(0));
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List fields = classNode.fields;

		assertEquals(1, fields.size());

		FieldNode firstField = (FieldNode) fields.get(0);

		assertEquals("Ljava/lang/Comparable;", firstField.desc);
		assertEquals("0", firstField.name);

		List methods = classNode.methods;
		MethodNode firstMethod = (MethodNode) methods.get(0);

		InsnList instructions = firstMethod.instructions;

		assertEquals(2, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals("thingy", firstInstruction.cst);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.ARETURN, secondInstruction.getOpcode());
	}
}
