package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.statement.FunctionCall;
import yirgacheffe.compiler.statement.TailCall;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.compiler.type.ParameterisedType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InvokeMethodTest
{
	private void method()
	{
	}

	public void method(Double num)
	{
	}

	@Test
	public void testCompilingToStringInvocation()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();
		Coordinate coordinate = new Coordinate(0, 1);
		Type stringType = new ReferenceType(String.class);
		Expression expression = new Literal(stringType, "\"thingy\"");

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				"toString",
				"MyClass",
				expression,
				new Array<>());

		Type type = invokeMethod.getType(variables);

		invokeMethod.compile(methodVisitor, variables);

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

		assertEquals("java.lang.String", type.toFullyQualifiedType());
	}

	@Test
	public void testCompilingPrivateMethodInvocation()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();
		Coordinate coordinate = new Coordinate(0, 1);
		Type testClass = new ReferenceType(this.getClass());
		Expression expression = new This(testClass);

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				"method",
				"yirgacheffe.compiler.expression.InvokeMethodTest",
				expression,
				new Array<>());

		Type type = invokeMethod.getType(variables);

		Array<Error> errors = invokeMethod.compile(methodVisitor, variables);

		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(2, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		MethodInsnNode secondInstruction = (MethodInsnNode) instructions.get(1);

		assertEquals(Opcodes.INVOKESPECIAL, secondInstruction.getOpcode());
		assertEquals("yirgacheffe/compiler/expression/InvokeMethodTest",
			secondInstruction.owner);
		assertEquals("method", secondInstruction.name);
		assertEquals("()V", secondInstruction.desc);
		assertFalse(secondInstruction.itf);

		assertEquals("java.lang.Void", type.toFullyQualifiedType());
	}

	@Test
	public void testInvocationCallWithArgument()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();
		Coordinate coordinate = new Coordinate(0, 1);
		Type stringType = new ReferenceType(String.class);
		Expression expression = new Literal(stringType, "\"thingy\"");
		Array<Expression> arguments = new Array<>(new Literal(stringType, "\"sumpt\""));

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				"concat",
				"myClass",
				expression,
				arguments);

		Type type = invokeMethod.getType(variables);

		invokeMethod.compile(methodVisitor, variables);

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

		assertEquals("java.lang.String", type.toFullyQualifiedType());
	}

	@Test
	public void testCompileArguments()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();
		Coordinate coordinate = new Coordinate(0, 1);
		Type stringType = new ReferenceType(String.class);
		Expression expression = new Literal(stringType, "\"thingy\"");
		Array<Expression> arguments = new Array<>(new Literal(stringType, "\"sumpt\""));

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				"concat",
				"myClass",
				expression,
				arguments);

		Type type = invokeMethod.getType(variables);

		Array<Error> errors = invokeMethod.compileArguments(methodVisitor, variables);

		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(1, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals("sumpt", firstInstruction.cst);
		assertEquals("java.lang.String", type.toFullyQualifiedType());
	}

	@Test
	public void testCompilingInvocationWithGenericReturnType()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();
		Coordinate coordinate = new Coordinate(0, 1);
		Array<Type> typeParameters =
			new Array<>(PrimitiveType.DOUBLE, PrimitiveType.DOUBLE);
		Type owner =
			new ParameterisedType(new ReferenceType(HashMap.class), typeParameters);

		Expression expression =
			new InvokeConstructor(
				coordinate,
				owner,
				new Array<>());

		Array<Expression> arguments =
			new Array<>(new Literal(PrimitiveType.DOUBLE, "1"));

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				"get",
				"MyClass",
				expression,
				arguments);

		Type type = invokeMethod.getType(variables);

		invokeMethod.compile(methodVisitor, variables);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(8, instructions.size());
		assertEquals("java.lang.Double", type.toFullyQualifiedType());

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
		assertFalse(fifthInstruction.itf);

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
		assertFalse(eightInstruction.itf);
	}

	@Test
	public void testInterfaceMethodInvocation()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();
		Type owner = new ReferenceType(Runnable.class);

		variables.declare("myVariable", owner);

		Coordinate coordinate = new Coordinate(0, 1);
		VariableRead expression = new VariableRead("myVariable", coordinate);

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				"run",
				"MyClass",
				expression,
				new Array<>());

		Type type = invokeMethod.getType(variables);

		invokeMethod.compile(methodVisitor, variables);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(2, instructions.size());
		assertEquals("java.lang.Void", type.toFullyQualifiedType());

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

	@Test
	public void testInvalidArgument()
	{
		Coordinate coordinate = new Coordinate(2, 4);
		This testClass = new This(new ReferenceType(this.getClass()));
		String name = "method";
		Array<Expression> arguments =
			new Array<>(new InvalidExpression(PrimitiveType.DOUBLE));

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				name,
				"yirgacheffe/compiler/expression/InvokeMethodTest",
				testClass,
				arguments);

		MethodVisitor methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Array<Error> errors = invokeMethod.compile(methodVisitor, variables);

		assertEquals(1, errors.length());
		assertEquals("line 0:0 This expression is not valid.", errors.get(0).toString());
	}

	@Test
	public void testEqualsTailCall()
	{
		Coordinate coordinate = new Coordinate(2, 4);
		This testClass = new This(new ReferenceType(this.getClass()));
		String name = "myMethod";
		Literal number = new Literal(PrimitiveType.DOUBLE, "1");
		Array<Expression> arguments = new Array<>(number);
		Variables variables = new Variables();

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				name,
				"MyClass",
				testClass,
				arguments);

		Array<Type> parameters = new Array<>(PrimitiveType.DOUBLE);
		Signature signature = new Signature(name, parameters);

		TailCall tailCall =
			new TailCall(new FunctionCall(invokeMethod), signature, variables);

		assertEquals(invokeMethod, tailCall);
		assertEquals(name.hashCode() + arguments.hashCode(), invokeMethod.hashCode());
	}
}
