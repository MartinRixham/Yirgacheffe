package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.function.Callable;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.type.ParameterisedType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InvokeMethodTest
{
	@Test
	public void testCompilingToStringInvocation() throws Exception
	{
		MethodNode methodVisitor = new MethodNode();
		Type stringType = new ReferenceType(String.class);
		Callable function =
			new Function(stringType, String.class.getMethod("toString"));
		Expression expression = new Literal(stringType, "\"thingy\"");

		InvokeMethod invokeMethod =
			new InvokeMethod(function, expression, new Expression[0]);

		invokeMethod.compile(methodVisitor);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(2, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals("thingy", firstInstruction.cst);

		MethodInsnNode secondInstruction = (MethodInsnNode) instructions.get(1);

		assertEquals(Opcodes.INVOKEVIRTUAL, secondInstruction.getOpcode());
		assertEquals("java/lang/String", secondInstruction.owner);
		assertEquals("toString", secondInstruction.name);
		assertEquals("()Ljava/lang/String;", secondInstruction.desc);
		assertFalse(secondInstruction.itf);

		assertEquals("java.lang.String", invokeMethod.getType().toFullyQualifiedType());
	}

	@Test
	public void testInvocationCallWithArgument() throws Exception
	{
		MethodNode methodVisitor = new MethodNode();

		Type stringType = new ReferenceType(String.class);
		Function function =
			new Function(stringType, String.class.getMethod("concat", String.class));
		Expression expression = new Literal(stringType, "\"thingy\"");
		Expression[] arguments = new Expression[] {new Literal(stringType, "\"sumpt\"")};

		InvokeMethod invokeMethod = new InvokeMethod(function, expression, arguments);

		invokeMethod.compile(methodVisitor);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(3, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals("thingy", firstInstruction.cst);

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(Opcodes.LDC, secondInstruction.getOpcode());
		assertEquals("sumpt", secondInstruction.cst);

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKEVIRTUAL, thirdInstruction.getOpcode());
		assertEquals("java/lang/String", thirdInstruction.owner);
		assertEquals("(Ljava/lang/String;)Ljava/lang/String;", thirdInstruction.desc);
		assertEquals("concat", thirdInstruction.name);
		assertFalse(thirdInstruction.itf);

		assertEquals("java.lang.String", invokeMethod.getType().toFullyQualifiedType());
	}

	@Test
	public void testCompilingInvocationWithGenericReturnType() throws Exception
	{
		MethodNode methodVisitor = new MethodNode();
		Array<Type> typeParameters =
			new Array<>(PrimitiveType.DOUBLE, PrimitiveType.DOUBLE);
		Type owner =
			new ParameterisedType(new ReferenceType(HashMap.class), typeParameters);
		Callable function =
			new Function(owner, Map.class.getMethod("get", Object.class));
		Callable constructor = new Function(owner, HashMap.class.getConstructor());
		Expression expression = new InvokeConstructor(constructor, new Expression[0]);
		Expression[] arguments =
			new Expression[] {new Literal(PrimitiveType.DOUBLE, "1")};

		InvokeMethod invokeMethod = new InvokeMethod(function, expression, arguments);

		invokeMethod.compile(methodVisitor);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(8, instructions.size());
		assertEquals("java.lang.Double", invokeMethod.getType().toFullyQualifiedType());

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKESPECIAL, thirdInstruction.getOpcode());
		assertEquals("java/util/HashMap", thirdInstruction.owner);
		assertEquals("()V", thirdInstruction.desc);
		assertEquals("<init>", thirdInstruction.name);
		assertFalse(thirdInstruction.itf);

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.DCONST_1, fourthInstruction.getOpcode());

		MethodInsnNode fifthInstruction = (MethodInsnNode) instructions.get(4);

		assertEquals(Opcodes.INVOKESTATIC, fifthInstruction.getOpcode());
		assertEquals("java/lang/Double", fifthInstruction.owner);
		assertEquals("(D)Ljava/lang/Double;", fifthInstruction.desc);
		assertEquals("valueOf", fifthInstruction.name);
		assertEquals(false, fifthInstruction.itf);

		MethodInsnNode sixthInstruction = (MethodInsnNode) instructions.get(5);

		assertEquals(Opcodes.INVOKEVIRTUAL, sixthInstruction.getOpcode());
		assertEquals("java/util/HashMap", sixthInstruction.owner);
		assertEquals("get", sixthInstruction.name);
		assertEquals("(Ljava/lang/Object;)Ljava/lang/Object;", sixthInstruction.desc);
		assertFalse(sixthInstruction.itf);

		TypeInsnNode seventhInstruction = (TypeInsnNode) instructions.get(6);

		assertEquals(Opcodes.CHECKCAST, seventhInstruction.getOpcode());
		assertEquals("java/lang/Double", seventhInstruction.desc);

		MethodInsnNode eightInstruction = (MethodInsnNode) instructions.get(7);

		assertEquals(Opcodes.INVOKESTATIC, eightInstruction.getOpcode());
		assertEquals("yirgacheffe/lang/Boxer", eightInstruction.owner);
		assertEquals("(Ljava/lang/Double;)D", eightInstruction.desc);
		assertEquals("ofValue", eightInstruction.name);
		assertEquals(false, eightInstruction.itf);
	}

	@Test
	public void testInterfaceMethodInvocation() throws Exception
	{
		MethodNode methodVisitor = new MethodNode();
		Type owner = new ReferenceType(Runnable.class);
		Callable function = new Function(owner, Runnable.class.getMethod("run"));
		Expression expression = new Variable(1, owner);
		Expression[] arguments = new Expression[0];

		InvokeMethod invokeMethod = new InvokeMethod(function, expression, arguments);

		invokeMethod.compile(methodVisitor);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(2, instructions.size());
		assertEquals("java.lang.Void", invokeMethod.getType().toFullyQualifiedType());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);

		MethodInsnNode secondInstruction = (MethodInsnNode) instructions.get(1);

		assertEquals(Opcodes.INVOKEINTERFACE, secondInstruction.getOpcode());
		assertEquals("java/lang/Runnable", secondInstruction.owner);
		assertEquals("run", secondInstruction.name);
		assertEquals("()V", secondInstruction.desc);
		assertTrue(secondInstruction.itf);
	}
}
