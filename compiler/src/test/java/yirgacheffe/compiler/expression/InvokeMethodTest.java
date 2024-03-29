package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.function.Caller;
import yirgacheffe.compiler.function.FunctionSignature;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.statement.FunctionCall;
import yirgacheffe.compiler.statement.TailCall;
import yirgacheffe.compiler.statement.VariableDeclaration;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.type.ParameterisedType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
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

	public void method(double num)
	{
	}

	public void method(double... ints)
	{
	}

	@Test
	public void testCompilingToStringInvocation()
	{
		Variables variables = new LocalVariables(1, new HashMap<>(), new HashMap<>());
		Coordinate coordinate = new Coordinate(0, 1);
		Expression expression = new Streeng(coordinate, "\"thingy\"");

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				"toString",
				new Caller("MyClass", new HashMap<>()),
				expression,
				new Array<>());

		Type type = invokeMethod.getType(variables);
		Result result = invokeMethod.compile(variables);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(1, variables.getStack().length());
		assertFalse(invokeMethod.isCondition(variables));
		assertEquals(4, instructions.length());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals("thingy", firstInstruction.cst);

		LabelNode secondInstruction = (LabelNode) instructions.get(1);
		Label label = secondInstruction.getLabel();

		LineNumberNode thirdInstruction = (LineNumberNode) instructions.get(2);

		assertEquals(label, thirdInstruction.start.getLabel());
		assertEquals(0, thirdInstruction.line);

		InvokeDynamicInsnNode fourthInstruction =
			(InvokeDynamicInsnNode) instructions.get(3);

		assertEquals(Opcodes.INVOKEDYNAMIC, fourthInstruction.getOpcode());
		assertEquals("toString", fourthInstruction.name);
		assertEquals("bootstrapPublic", fourthInstruction.bsm.getName());
		assertEquals(
			"(Ljava/lang/String;)Ljava/lang/String;",
			fourthInstruction.desc);

		assertEquals("java/lang/String", type.toFullyQualifiedType());
	}

	@Test
	public void testCompilingPrivateMethodInvocation()
	{
		Variables variables = new LocalVariables(1, new HashMap<>(), new HashMap<>());
		Coordinate coordinate = new Coordinate(0, 1);
		Type testClass = new ReferenceType(this.getClass());
		Expression expression = new This(coordinate, testClass);

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				"method",
				new Caller(
					"yirgacheffe/compiler/expression/InvokeMethodTest",
					new HashMap<>()),
				expression,
				new Array<>());

		Type type = invokeMethod.getType(variables);
		Result result = invokeMethod.compile(variables);

		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(4, instructions.length());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		LabelNode secondInstruction = (LabelNode) instructions.get(1);
		Label label = secondInstruction.getLabel();

		LineNumberNode thirdInstruction = (LineNumberNode) instructions.get(2);

		assertEquals(label, thirdInstruction.start.getLabel());
		assertEquals(0, thirdInstruction.line);

		InvokeDynamicInsnNode fourthInstruction =
			(InvokeDynamicInsnNode) instructions.get(3);

		assertEquals(Opcodes.INVOKEDYNAMIC, fourthInstruction.getOpcode());
		assertEquals("method", fourthInstruction.name);
		assertEquals("bootstrapPrivate", fourthInstruction.bsm.getName());
		assertEquals(
			"(Lyirgacheffe/compiler/expression/InvokeMethodTest;)V",
			fourthInstruction.desc);

		assertEquals("java/lang/Void", type.toFullyQualifiedType());
	}

	@Test
	public void testInvocationCallWithArgument()
	{
		Variables variables = new LocalVariables(1, new HashMap<>(), new HashMap<>());
		Coordinate coordinate = new Coordinate(0, 1);
		Expression expression = new Streeng(coordinate, "\"thingy\"");
		Array<Expression> arguments = new Array<>(new Streeng(coordinate, "\"sumpt\""));

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				"concat",
				new Caller("MyClass", new HashMap<>()),
				expression,
				arguments);

		Type type = invokeMethod.getType(variables);
		Result result = invokeMethod.compile(variables);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(5, instructions.length());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals("thingy", firstInstruction.cst);

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(Opcodes.LDC, secondInstruction.getOpcode());
		assertEquals("sumpt", secondInstruction.cst);

		LabelNode thirdInstruction = (LabelNode) instructions.get(2);
		Label label = thirdInstruction.getLabel();

		LineNumberNode fourthInstruction = (LineNumberNode) instructions.get(3);

		assertEquals(label, fourthInstruction.start.getLabel());
		assertEquals(0, fourthInstruction.line);

		InvokeDynamicInsnNode fifthInstruction =
			(InvokeDynamicInsnNode) instructions.get(4);

		assertEquals(Opcodes.INVOKEDYNAMIC, fifthInstruction.getOpcode());
		assertEquals("concat", fifthInstruction.name);
		assertEquals(
			"(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;",
			fifthInstruction.desc);

		assertEquals("java/lang/String", type.toFullyQualifiedType());
	}

	@Test
	public void testCompileArguments()
	{
		Variables variables = new LocalVariables(1, new HashMap<>(), new HashMap<>());
		Coordinate coordinate = new Coordinate(0, 1);
		Expression expression = new Streeng(coordinate, "\"thingy\"");
		Array<Expression> arguments = new Array<>(new Streeng(coordinate, "\"sumpt\""));

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				"concat",
				new Caller("MyClass", new HashMap<>()),
				expression,
				arguments);

		Type type = invokeMethod.getType(variables);
		Result result = invokeMethod.compileArguments(variables);

		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(1, instructions.length());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals("sumpt", firstInstruction.cst);
		assertEquals("java/lang/String", type.toFullyQualifiedType());
		assertEquals(coordinate, invokeMethod.getCoordinate());
	}

	@Test
	public void testCompilingInvocationWithGenericReturnType()
	{
		Variables variables = new LocalVariables(1, new HashMap<>(), new HashMap<>());
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
			new Array<>(new Num(coordinate, "1.0"));

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				"get",
				new Caller("MyClass", new HashMap<>()),
				expression,
				arguments);

		Type type = invokeMethod.getType(variables);
		Result result = invokeMethod.compile(variables);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(14, instructions.length());
		assertEquals("java/lang/Double", type.toFullyQualifiedType());

		LabelNode thirdInstruction = (LabelNode) instructions.get(2);
		Label label = thirdInstruction.getLabel();

		LineNumberNode fourthInstruction = (LineNumberNode) instructions.get(3);

		assertEquals(label, fourthInstruction.start.getLabel());
		assertEquals(0, fourthInstruction.line);

		MethodInsnNode fifthInstruction = (MethodInsnNode) instructions.get(4);

		assertEquals(Opcodes.INVOKESPECIAL, fifthInstruction.getOpcode());
		assertEquals("java/util/HashMap", fifthInstruction.owner);
		assertEquals("()V", fifthInstruction.desc);
		assertEquals("<init>", fifthInstruction.name);
		assertFalse(fifthInstruction.itf);

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.DUP, sixthInstruction.getOpcode());

		LdcInsnNode seventhInstruction = (LdcInsnNode) instructions.get(6);

		assertEquals(Opcodes.LDC, seventhInstruction.getOpcode());

		assertEquals(
			"java.lang.Double,java.lang.Double",
			seventhInstruction.cst);

		MethodInsnNode eighthInstruction = (MethodInsnNode) instructions.get(7);

		assertEquals(Opcodes.INVOKESTATIC, eighthInstruction.getOpcode());
		assertEquals("(Ljava/lang/Object;Ljava/lang/String;)V", eighthInstruction.desc);
		assertEquals("cacheObjectSignature", eighthInstruction.name);
		assertEquals("yirgacheffe/lang/Bootstrap", eighthInstruction.owner);

		InsnNode ninthInstruction = (InsnNode) instructions.get(8);

		assertEquals(Opcodes.DCONST_1, ninthInstruction.getOpcode());

		MethodInsnNode tenthInstruction = (MethodInsnNode) instructions.get(9);

		assertEquals(Opcodes.INVOKESTATIC, tenthInstruction.getOpcode());
		assertEquals("java/lang/Double", tenthInstruction.owner);
		assertEquals("(D)Ljava/lang/Double;", tenthInstruction.desc);
		assertEquals("valueOf", tenthInstruction.name);
		assertFalse(tenthInstruction.itf);

		LabelNode eleventhInstruction = (LabelNode) instructions.get(10);
		Label notherLabel = eleventhInstruction.getLabel();

		LineNumberNode twelfthInstruction = (LineNumberNode) instructions.get(11);

		assertEquals(notherLabel, twelfthInstruction.start.getLabel());
		assertEquals(0, twelfthInstruction.line);

		InvokeDynamicInsnNode thirteenthInstruction =
			(InvokeDynamicInsnNode) instructions.get(12);

		assertEquals(Opcodes.INVOKEDYNAMIC, thirteenthInstruction.getOpcode());
		assertEquals("get", thirteenthInstruction.name);
		assertEquals(
			"(Ljava/util/HashMap;Ljava/lang/Object;)Ljava/lang/Object;",
			thirteenthInstruction.desc);

		MethodInsnNode fourteenthInstruction = (MethodInsnNode) instructions.get(13);

		assertEquals(Opcodes.INVOKESTATIC, fourteenthInstruction.getOpcode());
		assertEquals("yirgacheffe/lang/Boxer", fourteenthInstruction.owner);
		assertEquals("(Ljava/lang/Object;)D", fourteenthInstruction.desc);
		assertEquals("toDouble", fourteenthInstruction.name);
		assertFalse(fourteenthInstruction.itf);
	}

	@Test
	public void testCompilingInvocationWithArrayReturnType()
	{
		Variables variables = new LocalVariables(1, new HashMap<>(), new HashMap<>());
		Coordinate coordinate = new Coordinate(0, 1);
		Expression expression = new This(coordinate, new ReferenceType(String.class));
		Array<Expression> arguments = new Array<>(new Streeng(coordinate, "\" \""));

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				"split",
				new Caller("MyClass", new HashMap<>()),
				expression,
				arguments);

		Type type = invokeMethod.getType(variables);
		Result result = invokeMethod.compile(variables);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(0, result.getErrors().length());
		assertEquals(6, instructions.length());
		assertEquals("yirgacheffe/lang/Array", type.toFullyQualifiedType());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(Opcodes.LDC, secondInstruction.getOpcode());
		assertEquals(" ", secondInstruction.cst);

		assertTrue(instructions.get(2) instanceof LabelNode);
		assertTrue(instructions.get(3) instanceof LineNumberNode);

		InvokeDynamicInsnNode fifthInstruction =
			(InvokeDynamicInsnNode) instructions.get(4);

		assertEquals(Opcodes.INVOKEDYNAMIC, fifthInstruction.getOpcode());
		assertEquals("split", fifthInstruction.name);

		MethodInsnNode sixthInstruction = (MethodInsnNode) instructions.get(5);

		assertEquals(Opcodes.INVOKESTATIC, sixthInstruction.getOpcode());
		assertEquals("fromArray", sixthInstruction.name);
	}

	@Test
	public void testPassingIntegerToNumberMethod()
	{
		Variables variables = new LocalVariables(1, new HashMap<>(), new HashMap<>());
		Coordinate coordinate = new Coordinate(0, 1);
		This testClass = new This(coordinate, new ReferenceType(this.getClass()));
		Array<Expression> arguments = new Array<>(new Num(coordinate, "1"));

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				"method",
				new Caller(
					"yirgacheffe/compiler/expression/InvokeMethodTest",
					new HashMap<>()),
				testClass,
				arguments);

		Result result = invokeMethod.compile(variables);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(0, result.getErrors().length());
		assertEquals(6, instructions.length());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.ICONST_1, secondInstruction.getOpcode());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.I2D, thirdInstruction.getOpcode());

		LabelNode fourthInstruction = (LabelNode) instructions.get(3);
		Label label = fourthInstruction.getLabel();

		LineNumberNode fifthInstruction = (LineNumberNode) instructions.get(4);

		assertEquals(label, fifthInstruction.start.getLabel());

		InvokeDynamicInsnNode sixthInstruction =
			(InvokeDynamicInsnNode) instructions.get(5);

		assertEquals(Opcodes.INVOKEDYNAMIC, sixthInstruction.getOpcode());
		assertEquals("method", sixthInstruction.name);
		assertEquals(
			"(Lyirgacheffe/compiler/expression/InvokeMethodTest;D)V",
			sixthInstruction.desc);
	}

	@Test
	public void testInterfaceMethodInvocation()
	{
		Variables variables = new LocalVariables(1, new HashMap<>(), new HashMap<>());
		Type owner = new ReferenceType(Runnable.class);
		Coordinate coordinate = new Coordinate(0, 1);

		variables.declare(new VariableDeclaration(coordinate, "myVariable", owner));

		VariableRead expression = new VariableRead(coordinate, "myVariable");

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				"run",
				new Caller("MyClass", new HashMap<>()),
				expression,
				new Array<>());

		Type type = invokeMethod.getType(variables);
		Result result = invokeMethod.compileCondition(variables, null, null);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(0, result.getErrors().length());
		assertEquals(4, instructions.length());
		assertEquals("java/lang/Void", type.toFullyQualifiedType());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);

		LabelNode secondInstruction = (LabelNode) instructions.get(1);
		Label label = secondInstruction.getLabel();

		LineNumberNode thirdInstruction = (LineNumberNode) instructions.get(2);

		assertEquals(label, thirdInstruction.start.getLabel());
		assertEquals(0, thirdInstruction.line);

		InvokeDynamicInsnNode fourthInstruction =
			(InvokeDynamicInsnNode) instructions.get(3);

		assertEquals(Opcodes.INVOKEDYNAMIC, fourthInstruction.getOpcode());
		assertEquals("run", fourthInstruction.name);
		assertEquals("(Ljava/lang/Runnable;)V", fourthInstruction.desc);
	}

	@Test
	public void testInvalidArgument()
	{
		Coordinate coordinate = new Coordinate(2, 4);
		This testClass = new This(coordinate, new ReferenceType(this.getClass()));
		String name = "method";
		Array<Expression> arguments =
			new Array<>(new InvalidExpression(PrimitiveType.DOUBLE));

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				name,
				new Caller(
					"yirgacheffe/compiler/expression/InvokeMethodTest",
					new HashMap<>()),
				testClass,
				arguments);

		Variables variables = new LocalVariables(1, new HashMap<>(), new HashMap<>());

		Result result = invokeMethod.compile(variables);

		assertEquals(1, result.getErrors().length());

		assertEquals(
			"line 0:0 This expression is not valid.",
			result.getErrors().get(0).toString());
	}

	@Test
	public void testEqualsTailCall()
	{
		Coordinate coordinate = new Coordinate(2, 4);
		This testClass = new This(coordinate, new ReferenceType(this.getClass()));
		String name = "myMethod";
		Num number = new Num(coordinate, "1.0");
		Array<Expression> arguments = new Array<>(number);
		Variables variables = new LocalVariables(1, new HashMap<>(), new HashMap<>());

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				name,
				new Caller("MyClass", new HashMap<>()),
				testClass,
				arguments);

		Array<Type> parameters = new Array<>(PrimitiveType.DOUBLE);
		Signature signature = new FunctionSignature(new NullType(), name, parameters);

		TailCall tailCall =
			new TailCall(new FunctionCall(invokeMethod), signature, variables);

		assertEquals(invokeMethod, tailCall);
		assertEquals(name.hashCode() + arguments.hashCode(), invokeMethod.hashCode());
	}

	@Test
	public void testVariableArguments()
	{
		Variables variables = new LocalVariables(1, new HashMap<>(), new HashMap<>());
		Coordinate coordinate = new Coordinate(0, 1);
		Type testClass = new ReferenceType(this.getClass());
		Expression expression = new This(coordinate, testClass);

		Array<Expression> arguments =
			new Array<>(new Num(coordinate, "1.0"), new Num(coordinate, "2.0"));

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				"method",
				new Caller(
					"yirgacheffe/compiler/expression/InvokeMethodTest",
					new HashMap<>()),
				expression,
				arguments);

		Result result = invokeMethod.compile(variables);

		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(14, instructions.length());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(Opcodes.LDC, secondInstruction.getOpcode());
		assertEquals(2, secondInstruction.cst);

		IntInsnNode thirdInstruction = (IntInsnNode) instructions.get(2);

		assertEquals(Opcodes.NEWARRAY, thirdInstruction.getOpcode());
		assertEquals(Opcodes.T_DOUBLE, thirdInstruction.operand);

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.DUP, fourthInstruction.getOpcode());

		LdcInsnNode fifthInstruction = (LdcInsnNode) instructions.get(4);

		assertEquals(Opcodes.LDC, fifthInstruction.getOpcode());
		assertEquals(0, fifthInstruction.cst);

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.DCONST_1, sixthInstruction.getOpcode());

		InsnNode seventhInstruction = (InsnNode) instructions.get(6);

		assertEquals(Opcodes.DASTORE, seventhInstruction.getOpcode());

		InsnNode eighthInstruction = (InsnNode) instructions.get(7);

		assertEquals(Opcodes.DUP, eighthInstruction.getOpcode());

		LdcInsnNode ninthInstruction = (LdcInsnNode) instructions.get(8);

		assertEquals(Opcodes.LDC, ninthInstruction.getOpcode());
		assertEquals(1, ninthInstruction.cst);

		LdcInsnNode tenthInstruction = (LdcInsnNode) instructions.get(9);

		assertEquals(Opcodes.LDC, tenthInstruction.getOpcode());
		assertEquals(2.0, tenthInstruction.cst);

		InsnNode eleventhInstruction = (InsnNode) instructions.get(10);

		assertEquals(Opcodes.DASTORE, eleventhInstruction.getOpcode());

		assertTrue(instructions.get(11) instanceof LabelNode);
		assertTrue(instructions.get(12) instanceof LineNumberNode);

		InvokeDynamicInsnNode fourteenthInstruction =
			(InvokeDynamicInsnNode) instructions.get(13);

		assertEquals(Opcodes.INVOKEDYNAMIC, fourteenthInstruction.getOpcode());
		assertEquals("method", fourteenthInstruction.name);
		assertEquals(
			"(Lyirgacheffe/compiler/expression/InvokeMethodTest;[D)V",
			fourteenthInstruction.desc);
	}

	@Test
	public void testInvalidVariableArguments()
	{
		Variables variables = new LocalVariables(1, new HashMap<>(), new HashMap<>());
		Coordinate coordinate = new Coordinate(0, 1);
		Type testClass = new ReferenceType(this.getClass());
		Expression expression = new This(coordinate, testClass);

		Array<Expression> arguments =
			new Array<>(
				new Streeng(coordinate, "\"one\""),
				new Streeng(coordinate, "\"two\""));

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				"method",
				new Caller(
					"yirgacheffe/compiler/expression/InvokeMethodTest",
					new HashMap<>()),
				expression,
				arguments);

		Result result = invokeMethod.compile(variables);

		assertEquals(1, result.getErrors().length());
		assertEquals(
			"line 0:1 Invoked method " +
			"yirgacheffe.compiler.expression.InvokeMethodTest" +
			".method(java.lang.String,java.lang.String) not found.",
			result.getErrors().get(0).toString());
	}
}
