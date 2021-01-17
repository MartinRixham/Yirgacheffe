package yirgacheffe.compiler.listener;

import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.type.Classes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExceptionTest
{
	@Test
	public void testHandleException()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{" +
					"Num number = try this.getNumber();\n" +
					"this.handle(number);" +
				"}\n" +
				"public Num getNumber()" +
				"{\n" +
					"return new Exception();\n" +
				"}\n" +
				"public Void handle(Object number) {}\n" +
				"public Void handle(Exception e) {}\n" +
				"public MyClass() {}\n" +
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

		MethodNode firstMethod = classNode.methods.get(0);

		assertEquals("method", firstMethod.name);

		assertEquals(1, firstMethod.tryCatchBlocks.size());

		TryCatchBlockNode tryCatch = firstMethod.tryCatchBlocks.get(0);
		Label startLabel = tryCatch.start.getLabel();
		Label endLabel = tryCatch.end.getLabel();
		Label handlerLabel = tryCatch.handler.getLabel();

		InsnList instructions = firstMethod.instructions;

		assertEquals(16, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.ASTORE, secondInstruction.getOpcode());
		assertEquals(1, secondInstruction.var);

		LabelNode thirdInstruction = (LabelNode) instructions.get(2);

		assertEquals(startLabel, thirdInstruction.getLabel());

		VarInsnNode fourthInstruction = (VarInsnNode) instructions.get(3);

		assertEquals(Opcodes.ALOAD, fourthInstruction.getOpcode());
		assertEquals(0, fourthInstruction.var);

		assertTrue(instructions.get(4) instanceof LabelNode);
		assertTrue(instructions.get(5) instanceof LineNumberNode);

		InvokeDynamicInsnNode seventhInstruction =
			(InvokeDynamicInsnNode) instructions.get(6);

		assertEquals(Opcodes.INVOKEDYNAMIC, seventhInstruction.getOpcode());
		assertEquals("getNumber", seventhInstruction.name);

		MethodInsnNode eighthInstruction = (MethodInsnNode) instructions.get(7);

		assertEquals(Opcodes.INVOKESTATIC, eighthInstruction.getOpcode());
		assertEquals("valueOf", eighthInstruction.name);

		LabelNode ninthInstruction = (LabelNode) instructions.get(8);

		assertEquals(endLabel, ninthInstruction.getLabel());
		assertEquals(handlerLabel, ninthInstruction.getLabel());

		assertTrue(instructions.get(9) instanceof FrameNode);

		VarInsnNode eleventhInstruction = (VarInsnNode) instructions.get(10);

		assertEquals(Opcodes.ALOAD, eleventhInstruction.getOpcode());
		assertEquals(1, eleventhInstruction.var);

		InsnNode twelfthInstruction = (InsnNode) instructions.get(11);

		assertEquals(Opcodes.SWAP, twelfthInstruction.getOpcode());

		assertTrue(instructions.get(12) instanceof LabelNode);
		assertTrue(instructions.get(13) instanceof LineNumberNode);

		InvokeDynamicInsnNode fifteenthInstruction =
			(InvokeDynamicInsnNode) instructions.get(14);

		assertEquals(Opcodes.INVOKEDYNAMIC, fifteenthInstruction.getOpcode());
		assertEquals("handle", fifteenthInstruction.name);
		assertEquals("(LMyClass;Ljava/lang/Object;)V", fifteenthInstruction.desc);
	}

	@Test
	public void testPrimitiveExceptionHandler()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{" +
					"Num number = try this.getNumber();\n" +
					"this.handle(number);" +
					"this.handle(number);" +
				"}\n" +
				"public Num getNumber()" +
				"{\n" +
					"return new Exception();\n" +
				"}\n" +
				"public Void handle(Num number) {}\n" +
				"public Void handle(Exception e) {}\n" +
				"public MyClass() {}\n" +
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

		MethodNode firstMethod = classNode.methods.get(0);

		assertEquals("method", firstMethod.name);

		assertEquals(1, firstMethod.tryCatchBlocks.size());

		TryCatchBlockNode tryCatch = firstMethod.tryCatchBlocks.get(0);
		Label startLabel = tryCatch.start.getLabel();
		Label endLabel = tryCatch.end.getLabel();
		Label handlerLabel = tryCatch.handler.getLabel();

		InsnList instructions = firstMethod.instructions;

		assertEquals(20, instructions.size());

		LabelNode firstInstruction = (LabelNode) instructions.get(0);

		assertEquals(startLabel, firstInstruction.getLabel());

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.ALOAD, secondInstruction.getOpcode());
		assertEquals(0, secondInstruction.var);

		assertTrue(instructions.get(2) instanceof LabelNode);
		assertTrue(instructions.get(3) instanceof LineNumberNode);

		InvokeDynamicInsnNode fifthInstruction =
			(InvokeDynamicInsnNode) instructions.get(4);

		assertEquals(Opcodes.INVOKEDYNAMIC, fifthInstruction.getOpcode());
		assertEquals("getNumber", fifthInstruction.name);

		MethodInsnNode sixthInstruction = (MethodInsnNode) instructions.get(5);

		assertEquals(Opcodes.INVOKESTATIC, sixthInstruction.getOpcode());
		assertEquals("valueOf", sixthInstruction.name);

		LabelNode seventhInstruction = (LabelNode) instructions.get(6);

		assertEquals(endLabel, seventhInstruction.getLabel());
		assertEquals(handlerLabel, seventhInstruction.getLabel());

		assertTrue(instructions.get(7) instanceof FrameNode);

		VarInsnNode ninthInstruction = (VarInsnNode) instructions.get(8);

		assertEquals(Opcodes.ASTORE, ninthInstruction.getOpcode());
		assertEquals(1, ninthInstruction.var);

		VarInsnNode tenthInstruction = (VarInsnNode) instructions.get(9);

		assertEquals(Opcodes.ALOAD, tenthInstruction.getOpcode());
		assertEquals(0, tenthInstruction.var);

		VarInsnNode eleventhInstruction = (VarInsnNode) instructions.get(10);

		assertEquals(Opcodes.ALOAD, eleventhInstruction.getOpcode());
		assertEquals(1, eleventhInstruction.var);

		assertTrue(instructions.get(11) instanceof LabelNode);
		assertTrue(instructions.get(12) instanceof LineNumberNode);

		InvokeDynamicInsnNode fourteenthInstruction =
			(InvokeDynamicInsnNode) instructions.get(13);

		assertEquals(Opcodes.INVOKEDYNAMIC, fourteenthInstruction.getOpcode());
		assertEquals("handle", fourteenthInstruction.name);
		assertEquals("(LMyClass;Ljava/lang/Object;)V", fourteenthInstruction.desc);

		VarInsnNode fifteenthInstruction = (VarInsnNode) instructions.get(14);

		assertEquals(Opcodes.ALOAD, fifteenthInstruction.getOpcode());
		assertEquals(0, fifteenthInstruction.var);

		VarInsnNode sixteenthInstruction = (VarInsnNode) instructions.get(15);

		assertEquals(Opcodes.ALOAD, sixteenthInstruction.getOpcode());
		assertEquals(1, sixteenthInstruction.var);

		assertTrue(instructions.get(16) instanceof LabelNode);
		assertTrue(instructions.get(17) instanceof LineNumberNode);

		InvokeDynamicInsnNode nineteenthInstruction =
			(InvokeDynamicInsnNode) instructions.get(18);

		assertEquals(Opcodes.INVOKEDYNAMIC, nineteenthInstruction.getOpcode());
		assertEquals("handle", nineteenthInstruction.name);
		assertEquals("(LMyClass;Ljava/lang/Object;)V", nineteenthInstruction.desc);
	}

	@Test
	public void testTryExpressionAsArgument()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{" +
					"this.handle(try this.getNumber());" +
				"}\n" +
				"public Num getNumber()" +
				"{\n" +
					"return new Exception();\n" +
				"}\n" +
				"public Void handle(Object number) {}\n" +
				"public Void handle(Exception e) {}\n" +
				"public MyClass() {}\n" +
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

		MethodNode firstMethod = classNode.methods.get(0);

		assertEquals("method", firstMethod.name);

		assertEquals(1, firstMethod.tryCatchBlocks.size());

		TryCatchBlockNode tryCatch = firstMethod.tryCatchBlocks.get(0);
		Label startLabel = tryCatch.start.getLabel();
		Label endLabel = tryCatch.end.getLabel();
		Label handlerLabel = tryCatch.handler.getLabel();

		InsnList instructions = firstMethod.instructions;

		assertEquals(16, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.ASTORE, secondInstruction.getOpcode());
		assertEquals(1, secondInstruction.var);

		LabelNode thirdInstruction = (LabelNode) instructions.get(2);

		assertEquals(startLabel, thirdInstruction.getLabel());

		VarInsnNode fourthInstruction = (VarInsnNode) instructions.get(3);

		assertEquals(Opcodes.ALOAD, fourthInstruction.getOpcode());
		assertEquals(0, fourthInstruction.var);

		assertTrue(instructions.get(4) instanceof LabelNode);
		assertTrue(instructions.get(5) instanceof LineNumberNode);

		InvokeDynamicInsnNode seventhInstruction =
			(InvokeDynamicInsnNode) instructions.get(6);

		assertEquals(Opcodes.INVOKEDYNAMIC, seventhInstruction.getOpcode());
		assertEquals("getNumber", seventhInstruction.name);

		MethodInsnNode eighthInstruction = (MethodInsnNode) instructions.get(7);

		assertEquals(Opcodes.INVOKESTATIC, eighthInstruction.getOpcode());
		assertEquals("valueOf", eighthInstruction.name);

		LabelNode ninthInstruction = (LabelNode) instructions.get(8);

		assertEquals(endLabel, ninthInstruction.getLabel());
		assertEquals(handlerLabel, ninthInstruction.getLabel());

		assertTrue(instructions.get(9) instanceof FrameNode);

		VarInsnNode eleventhInstruction = (VarInsnNode) instructions.get(10);

		assertEquals(Opcodes.ALOAD, eleventhInstruction.getOpcode());
		assertEquals(1, eleventhInstruction.var);

		InsnNode twelfthInstruction = (InsnNode) instructions.get(11);

		assertEquals(Opcodes.SWAP, twelfthInstruction.getOpcode());

		assertTrue(instructions.get(12) instanceof LabelNode);
		assertTrue(instructions.get(13) instanceof LineNumberNode);

		InvokeDynamicInsnNode fifteenthInstruction =
			(InvokeDynamicInsnNode) instructions.get(14);

		assertEquals(Opcodes.INVOKEDYNAMIC, fifteenthInstruction.getOpcode());
		assertEquals("handle", fifteenthInstruction.name);
		assertEquals("(LMyClass;Ljava/lang/Object;)V", fifteenthInstruction.desc);
	}

	@Test
	public void operateOnException()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{" +
					"Num number = try this.getNumber() + 1;\n" +
				"}\n" +
				"public Num getNumber()" +
				"{\n" +
					"return new Exception();\n" +
				"}\n" +
				"public Void handle(Num number) {}\n" +
				"public Void handle(Exception e) {}\n" +
				"public MyClass() {}\n" +
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

		MethodNode firstMethod = classNode.methods.get(0);

		assertEquals("method", firstMethod.name);

		assertEquals(1, firstMethod.tryCatchBlocks.size());

		TryCatchBlockNode tryCatch = firstMethod.tryCatchBlocks.get(0);
		Label startLabel = tryCatch.start.getLabel();
		Label endLabel = tryCatch.end.getLabel();
		Label handlerLabel = tryCatch.handler.getLabel();

		InsnList instructions = firstMethod.instructions;

		assertEquals(14, instructions.size());

		LabelNode firstInstruction = (LabelNode) instructions.get(0);

		assertEquals(startLabel, firstInstruction.getLabel());

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.ALOAD, secondInstruction.getOpcode());
		assertEquals(0, secondInstruction.var);

		assertTrue(instructions.get(2) instanceof LabelNode);
		assertTrue(instructions.get(3) instanceof LineNumberNode);

		InvokeDynamicInsnNode fifthInstruction =
			(InvokeDynamicInsnNode) instructions.get(4);

		assertEquals(Opcodes.INVOKEDYNAMIC, fifthInstruction.getOpcode());
		assertEquals("getNumber", fifthInstruction.name);

		MethodInsnNode sixthInstruction = (MethodInsnNode) instructions.get(5);

		assertEquals(Opcodes.INVOKESTATIC, sixthInstruction.getOpcode());
		assertEquals("valueOf", sixthInstruction.name);

		LabelNode seventhInstruction = (LabelNode) instructions.get(6);

		assertEquals(endLabel, seventhInstruction.getLabel());
		assertEquals(handlerLabel, seventhInstruction.getLabel());

		assertTrue(instructions.get(7) instanceof FrameNode);

		MethodInsnNode ninthInstruction = (MethodInsnNode) instructions.get(8);

		assertEquals(Opcodes.INVOKESTATIC, ninthInstruction.getOpcode());
		assertEquals("toDouble", ninthInstruction.name);

		InsnNode tenthInstruction = (InsnNode) instructions.get(9);

		assertEquals(Opcodes.ICONST_1, tenthInstruction.getOpcode());

		InsnNode eleventhInstruction = (InsnNode) instructions.get(10);

		assertEquals(Opcodes.I2D, eleventhInstruction.getOpcode());

		InsnNode twelfthInstruction = (InsnNode) instructions.get(11);

		assertEquals(Opcodes.DADD, twelfthInstruction.getOpcode());

		VarInsnNode thirteenthInstruction = (VarInsnNode) instructions.get(12);

		assertEquals(Opcodes.DSTORE, thirteenthInstruction.getOpcode());
		assertEquals(1, thirteenthInstruction.var);
	}

	@Test
	public void testIgnoreException()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{" +
					"try this.method();" +
				"}\n" +
				"public MyClass() {}\n" +
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

		MethodNode firstMethod = classNode.methods.get(0);

		assertEquals("method", firstMethod.name);

		assertEquals(1, firstMethod.tryCatchBlocks.size());

		TryCatchBlockNode tryCatch = firstMethod.tryCatchBlocks.get(0);
		Label startLabel = tryCatch.start.getLabel();
		Label endLabel = tryCatch.end.getLabel();
		Label handlerLabel = tryCatch.handler.getLabel();

		InsnList instructions = firstMethod.instructions;

		assertEquals(12, instructions.size());

		LabelNode firstInstruction = (LabelNode) instructions.get(0);

		assertEquals(startLabel, firstInstruction.getLabel());

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.ALOAD, secondInstruction.getOpcode());
		assertEquals(0, secondInstruction.var);

		assertTrue(instructions.get(2) instanceof LabelNode);
		assertTrue(instructions.get(3) instanceof LineNumberNode);

		InvokeDynamicInsnNode fifthInstruction =
			(InvokeDynamicInsnNode) instructions.get(4);

		assertEquals(Opcodes.INVOKEDYNAMIC, fifthInstruction.getOpcode());

		JumpInsnNode sixthInstruction = (JumpInsnNode) instructions.get(5);
		Label success = sixthInstruction.label.getLabel();

		assertEquals(Opcodes.GOTO, sixthInstruction.getOpcode());

		LabelNode seventhInstruction = (LabelNode) instructions.get(6);

		assertEquals(endLabel, seventhInstruction.getLabel());
		assertEquals(handlerLabel, seventhInstruction.getLabel());

		assertTrue(instructions.get(7) instanceof FrameNode);

		InsnNode ninthInstruction = (InsnNode) instructions.get(8);

		assertEquals(Opcodes.POP, ninthInstruction.getOpcode());

		LabelNode tenthInstruction = (LabelNode) instructions.get(9);

		assertEquals(success, tenthInstruction.getLabel());
		assertTrue(instructions.get(10) instanceof FrameNode);
	}

	@Test
	public void testTryInExpression()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"main method(Array<String> args)\n" +
				"{\n" +
					"String str = try(\"\") || \"\";\n" +
					"this.handle(str);\n" +
				"}\n" +
				"private Void handle(String str)\n" +
				"{\n" +
				"}\n" +
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

		MethodNode firstMethod = classNode.methods.get(0);

		assertEquals("method", firstMethod.name);

		assertEquals(1, firstMethod.tryCatchBlocks.size());

		TryCatchBlockNode tryCatch = firstMethod.tryCatchBlocks.get(0);
		Label startLabel = tryCatch.start.getLabel();
		Label endLabel = tryCatch.end.getLabel();
		Label handlerLabel = tryCatch.handler.getLabel();

		InsnList instructions = firstMethod.instructions;

		assertEquals(52, instructions.size());
	}

	@Test
	@Ignore
	public void testTryInAnotherExpression()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"main method(Array<String> args)\n" +
				"{\n" +
					"String str = \"\" || try(\"\");\n" +
					"this.handle(str);\n" +
				"}\n" +
				"private Void handle(String str)\n" +
				"{\n" +
				"}\n" +
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

		MethodNode firstMethod = classNode.methods.get(0);

		assertEquals("method", firstMethod.name);

		assertEquals(1, firstMethod.tryCatchBlocks.size());

		TryCatchBlockNode tryCatch = firstMethod.tryCatchBlocks.get(0);
		Label startLabel = tryCatch.start.getLabel();
		Label endLabel = tryCatch.end.getLabel();
		Label handlerLabel = tryCatch.handler.getLabel();

		InsnList instructions = firstMethod.instructions;

		assertEquals(20, instructions.size());
	}
}
