package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.function.Callable;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.type.ParameterisedType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class InvokeMethodTest
{
	@Test
	public void testCompilingToStringInvocation() throws Exception
	{
		MethodNode methodVisitor = new MethodNode();
		Type owner = new ReferenceType(String.class);
		Callable function =
			new Function(owner, String.class.getMethod("toString"));
		Expression expression = new Literal("thingy");

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

		assertEquals(1, invokeMethod.getStackHeight());
	}

	@Test
	public void testInvocationCallWithArgument() throws Exception
	{
		MethodNode methodVisitor = new MethodNode();

		Type owner = new ReferenceType(String.class);
		Function function =
			new Function(owner, String.class.getMethod("concat", String.class));
		Expression expression = new Literal("thingy");
		Expression[] arguments = new Expression[] {new Literal("sumpt")};

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

		assertEquals(2, invokeMethod.getStackHeight());
	}

	@Test
	public void testCompilingInvocationWithGenericReturnType() throws Exception
	{
		MethodNode methodVisitor = new MethodNode();
		List<Type> typeParameters =
			Arrays.asList(PrimitiveType.DOUBLE, PrimitiveType.DOUBLE);
		Type owner = new ParameterisedType(new ReferenceType(Map.class), typeParameters);
		Callable function =
			new Function(owner, Map.class.getMethod("get", Object.class));
		Callable constructor = new Function(owner, HashMap.class.getConstructor());
		Expression expression = new InvokeConstructor(constructor, new Expression[0]);
		Expression[] arguments = new Expression[] {new Literal(1)};

		InvokeMethod invokeMethod = new InvokeMethod(function, expression, arguments);

		invokeMethod.compile(methodVisitor);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(6, instructions.size());
	}
}
