package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.type.ParameterisedType;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InvokeConstructorTest
{
	@Test
	public void testCompilingInvocationWithGenericReturnType()
	{
		Variables variables = new LocalVariables(1, new HashMap<>());
		Coordinate coordinate = new Coordinate(1, 0);
		Type owner = new ReferenceType(Double.class);
		Expression one = new Num(coordinate, "1.0");
		Array<Expression> arguments = new Array<>(one);

		InvokeConstructor invokeConstructor =
			new InvokeConstructor(
				coordinate,
				owner,
				arguments);

		Type type = invokeConstructor.getType(variables);
		Result result = invokeConstructor.compileCondition(variables, null, null);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(1, variables.getStack().length());
		assertFalse(invokeConstructor.isCondition(variables));
		assertEquals(0, result.getErrors().length());
		assertEquals(6, instructions.length());

		TypeInsnNode firstInstruction = (TypeInsnNode) instructions.get(0);

		assertEquals(Opcodes.NEW, firstInstruction.getOpcode());
		assertEquals("java/lang/Double", firstInstruction.desc);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DCONST_1, thirdInstruction.getOpcode());

		LabelNode fourthInstruction = (LabelNode) instructions.get(3);
		Label label = fourthInstruction.getLabel();

		LineNumberNode fifthInstruction = (LineNumberNode) instructions.get(4);

		assertEquals(1, fifthInstruction.line);

		MethodInsnNode sixthInstruction = (MethodInsnNode) instructions.get(5);

		assertEquals(Opcodes.INVOKESPECIAL, sixthInstruction.getOpcode());
		assertEquals("java/lang/Double", sixthInstruction.owner);
		assertEquals("<init>", sixthInstruction.name);
		assertEquals("(D)V", sixthInstruction.desc);
		assertFalse(sixthInstruction.itf);

		assertEquals("java/lang/Double", type.toFullyQualifiedType());
	}

	@Test
	public void testGettingFirstOperand()
	{
		Variables variables = new LocalVariables(1, new HashMap<>());
		Coordinate coordinate = new Coordinate(1, 0);
		Type owner = new ReferenceType(Double.class);
		Expression one = new Num(coordinate, "1.0");
		Array<Expression> arguments = new Array<>(one);

		InvokeConstructor invokeConstructor =
			new InvokeConstructor(
				coordinate,
				owner,
				arguments);

		Result result = invokeConstructor.compile(variables);

		assertEquals(0, result.getErrors().length());
	}

	@Test
	public void testGettingNoFirstOperand()
	{
		Variables variables = new LocalVariables(1, new HashMap<>());
		Coordinate coordinate = new Coordinate(1, 0);
		Type owner = new ReferenceType(String.class);
		Array<Expression> arguments = new Array<>();

		InvokeConstructor invokeConstructor =
			new InvokeConstructor(
				coordinate,
				owner,
				arguments);

		Result result = invokeConstructor.compile(variables);

		assertEquals(0, result.getErrors().length());
		assertEquals(coordinate, invokeConstructor.getCoordinate());
	}

	@Test
	public void testConstructArray()
	{
		Variables variables = new LocalVariables(1, new HashMap<>());
		Coordinate coordinate = new Coordinate(1, 0);
		ReferenceType array = new ReferenceType(Array.class);
		Array<Type> typeParameters = new Array<>(new ReferenceType(String.class));
		Type owner = new ParameterisedType(array, typeParameters);
		Expression one = new Streeng(coordinate, "\"one\"");
		Expression two = new Streeng(coordinate, "\"two\"");
		Array<Expression> arguments = new Array<>(one, two);

		InvokeConstructor invokeConstructor =
			new InvokeConstructor(
				coordinate,
				owner,
				arguments);

		Type type = invokeConstructor.getType(variables);
		Result result = invokeConstructor.compileCondition(variables, null, null);

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals("yirgacheffe/lang/Array", type.toFullyQualifiedType());
		assertFalse(invokeConstructor.isCondition(variables));
		assertEquals(0, result.getErrors().length());
		assertEquals(22, instructions.length());

		TypeInsnNode firstInstruction = (TypeInsnNode) instructions.get(0);

		assertEquals(Opcodes.NEW, firstInstruction.getOpcode());
		assertEquals("yirgacheffe/lang/Array", firstInstruction.desc);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		LdcInsnNode thirdInstruction = (LdcInsnNode) instructions.get(2);

		assertEquals(Opcodes.LDC, thirdInstruction.getOpcode());
		assertEquals(2, thirdInstruction.cst);

		TypeInsnNode fourthInstruction = (TypeInsnNode) instructions.get(3);

		assertEquals(Opcodes.ANEWARRAY, fourthInstruction.getOpcode());
		assertEquals("java/lang/String", fourthInstruction.desc);

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.DUP, fifthInstruction.getOpcode());

		LdcInsnNode sixthInstruction = (LdcInsnNode) instructions.get(5);

		assertEquals(Opcodes.LDC, sixthInstruction.getOpcode());
		assertEquals(0, sixthInstruction.cst);

		LdcInsnNode seventhInstruction = (LdcInsnNode) instructions.get(6);

		assertEquals(Opcodes.LDC, seventhInstruction.getOpcode());
		assertEquals("one", seventhInstruction.cst);

		InsnNode eighthInstruction = (InsnNode) instructions.get(7);

		assertEquals(Opcodes.AASTORE, eighthInstruction.getOpcode());

		InsnNode ninthInstruction = (InsnNode) instructions.get(8);

		assertEquals(Opcodes.DUP, ninthInstruction.getOpcode());

		LdcInsnNode tenthInstruction = (LdcInsnNode) instructions.get(9);

		assertEquals(Opcodes.LDC, tenthInstruction.getOpcode());
		assertEquals(1, tenthInstruction.cst);

		LdcInsnNode eleventhInstruction = (LdcInsnNode) instructions.get(10);

		assertEquals(Opcodes.LDC, eleventhInstruction.getOpcode());
		assertEquals("two", eleventhInstruction.cst);

		InsnNode twelfthInstruction = (InsnNode) instructions.get(11);

		assertEquals(Opcodes.AASTORE, twelfthInstruction.getOpcode());

		assertTrue(instructions.get(12) instanceof LabelNode);
		assertTrue(instructions.get(13) instanceof LineNumberNode);

		MethodInsnNode fifteenthInstruction = (MethodInsnNode) instructions.get(14);

		assertEquals(Opcodes.INVOKESPECIAL, fifteenthInstruction.getOpcode());
		assertEquals("yirgacheffe/lang/Array", fifteenthInstruction.owner);
		assertEquals("<init>", fifteenthInstruction.name);
		assertEquals("([Ljava/lang/Object;)V", fifteenthInstruction.desc);
	}

	@Test
	public void testConstructArrayWithInvalidArgument()
	{
		Variables variables = new LocalVariables(1, new HashMap<>());
		Coordinate coordinate = new Coordinate(1, 0);
		ReferenceType array = new ReferenceType(Array.class);
		Array<Type> typeParameters = new Array<>(new ReferenceType(String.class));
		Type owner = new ParameterisedType(array, typeParameters);
		Expression one = new Num(coordinate, "1");
		Expression two = new Num(coordinate, "2");
		Array<Expression> arguments = new Array<>(one, two);

		InvokeConstructor invokeConstructor =
			new InvokeConstructor(
				coordinate,
				owner,
				arguments);

		Result result = invokeConstructor.compile(variables);

		assertEquals(2, result.getErrors().length());

		assertEquals(
			"line 1:0 Argument of type Num cannot be assigned to " +
				"generic parameter of type java.lang.String.",
			result.getErrors().get(0).toString());

		assertEquals(
			"line 1:0 Argument of type Num cannot be assigned to " +
				"generic parameter of type java.lang.String.",
			result.getErrors().get(1).toString());
	}
}
