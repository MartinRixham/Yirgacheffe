package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.statement.FunctionCall;
import yirgacheffe.compiler.statement.TailCall;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.compiler.type.ParameterisedType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class InvokeMethodTest
{
	private void method()
	{
	}

	public void method(Double num)
	{
	}

	public void takeInt(double num)
	{
	}

	@Test
	public void testCompilingToStringInvocation()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());
		Coordinate coordinate = new Coordinate(0, 1);
		Expression expression = new Streeng("\"thingy\"");

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

		assertFalse(invokeMethod.isCondition(variables));
		assertEquals(4, instructions.size());

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
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());
		Coordinate coordinate = new Coordinate(0, 1);
		Type testClass = new ReferenceType(this.getClass());
		Expression expression = new This(testClass);

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				"method",
				"yirgacheffe/compiler/expression/InvokeMethodTest",
				expression,
				new Array<>());

		Type type = invokeMethod.getType(variables);

		Array<Error> errors = invokeMethod.compile(methodVisitor, variables);

		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(4, instructions.size());

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
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());
		Coordinate coordinate = new Coordinate(0, 1);
		Expression expression = new Streeng("\"thingy\"");
		Array<Expression> arguments = new Array<>(new Streeng("\"sumpt\""));

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

		assertEquals(5, instructions.size());

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
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());
		Coordinate coordinate = new Coordinate(0, 1);
		Expression expression = new Streeng("\"thingy\"");
		Array<Expression> arguments = new Array<>(new Streeng("\"sumpt\""));

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
		assertEquals("java/lang/String", type.toFullyQualifiedType());
	}

	@Test
	public void testCompilingInvocationWithGenericReturnType()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());
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
			new Array<>(new Num("1.0"));

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

		assertEquals(12, instructions.size());
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

		assertEquals(Opcodes.DCONST_1, sixthInstruction.getOpcode());

		MethodInsnNode seventhInstruction = (MethodInsnNode) instructions.get(6);

		assertEquals(Opcodes.INVOKESTATIC, seventhInstruction.getOpcode());
		assertEquals("java/lang/Double", seventhInstruction.owner);
		assertEquals("(D)Ljava/lang/Double;", seventhInstruction.desc);
		assertEquals("valueOf", seventhInstruction.name);
		assertFalse(seventhInstruction.itf);

		LabelNode eighthInstruction = (LabelNode) instructions.get(7);
		Label notherLabel = eighthInstruction.getLabel();

		LineNumberNode ninthInstruction = (LineNumberNode) instructions.get(8);

		assertEquals(notherLabel, ninthInstruction.start.getLabel());
		assertEquals(0, ninthInstruction.line);

		InvokeDynamicInsnNode tenthInstruction =
			(InvokeDynamicInsnNode) instructions.get(9);

		assertEquals(Opcodes.INVOKEDYNAMIC, tenthInstruction.getOpcode());
		assertEquals("get", tenthInstruction.name);
		assertEquals(
			"(Ljava/util/HashMap;Ljava/lang/Object;)Ljava/lang/Object;",
			tenthInstruction.desc);

		TypeInsnNode eleventhInstruction = (TypeInsnNode) instructions.get(10);

		assertEquals(Opcodes.CHECKCAST, eleventhInstruction.getOpcode());
		assertEquals("java/lang/Double", eleventhInstruction.desc);

		MethodInsnNode twelfthInstruction = (MethodInsnNode) instructions.get(11);

		assertEquals(Opcodes.INVOKESTATIC, twelfthInstruction.getOpcode());
		assertEquals("yirgacheffe/lang/Boxer", twelfthInstruction.owner);
		assertEquals("(Ljava/lang/Double;)D", twelfthInstruction.desc);
		assertEquals("ofValue", twelfthInstruction.name);
		assertFalse(twelfthInstruction.itf);
	}

	@Test
	public void testPassingIntegerToNumberMethod()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());
		Coordinate coordinate = new Coordinate(0, 1);
		This testClass = new This(new ReferenceType(this.getClass()));
		Array<Expression> arguments = new Array<>(new Num("1"));

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				"takeInt",
				"yirgacheffe/compiler/expression/InvokeMethodTest",
				testClass,
				arguments);

		Array<Error> errors = invokeMethod.compile(methodVisitor, variables);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(0, errors.length());
		assertEquals(6, instructions.size());

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
		assertEquals("takeInt", sixthInstruction.name);
		assertEquals(
			"(Lyirgacheffe/compiler/expression/InvokeMethodTest;D)V",
			sixthInstruction.desc);
	}

	@Test
	public void testInterfaceMethodInvocation()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());
		Type owner = new ReferenceType(Runnable.class);

		variables.declare("myVariable", owner);

		Coordinate coordinate = new Coordinate(0, 1);
		VariableRead expression = new VariableRead(coordinate, "myVariable");

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				"run",
				"MyClass",
				expression,
				new Array<>());

		Type type = invokeMethod.getType(variables);

		Array<Error> errors =
			invokeMethod.compileCondition(methodVisitor, variables, new Label());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(0, errors.length());
		assertEquals(4, instructions.size());
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
		Variables variables = new Variables(new HashMap<>());

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
		Num number = new Num("1.0");
		Array<Expression> arguments = new Array<>(number);
		Variables variables = new Variables(new HashMap<>());

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				name,
				"MyClass",
				testClass,
				arguments);

		Array<Type> parameters = new Array<>(PrimitiveType.DOUBLE);
		Signature signature = new Signature(new NullType(), name, parameters);

		TailCall tailCall =
			new TailCall(new FunctionCall(invokeMethod), signature, variables);

		assertEquals(invokeMethod, tailCall);
		assertEquals(name.hashCode() + arguments.hashCode(), invokeMethod.hashCode());
	}
}
