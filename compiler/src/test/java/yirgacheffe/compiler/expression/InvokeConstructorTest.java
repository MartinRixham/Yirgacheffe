package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class InvokeConstructorTest
{
	@Test
	public void testCompilingInvocationWithGenericReturnType()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());
		Coordinate coordinate = new Coordinate(1, 0);
		Type owner = new ReferenceType(Double.class);
		Expression one = new Num("1.0");
		Array<Expression> arguments = new Array<Expression>(one);

		InvokeConstructor invokeConstructor =
			new InvokeConstructor(
				coordinate,
				owner,
				arguments);

		Type type = invokeConstructor.getType(variables);

		invokeConstructor.compile(methodVisitor, variables);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(6, instructions.size());

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

		assertEquals("java.lang.Double", type.toFullyQualifiedType());
	}

	@Test
	public void testGettingFirstOperand()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());
		Coordinate coordinate = new Coordinate(1, 0);
		Type owner = new ReferenceType(Double.class);
		Expression one = new Num("1.0");
		Array<Expression> arguments = new Array<Expression>(one);

		InvokeConstructor invokeConstructor =
			new InvokeConstructor(
				coordinate,
				owner,
				arguments);

		Array<Error> errors = invokeConstructor.compile(methodVisitor, variables);

		assertEquals(0, errors.length());
	}

	@Test
	public void testGettingNoFirstOperand()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables(new HashMap<>());
		Coordinate coordinate = new Coordinate(1, 0);
		Type owner = new ReferenceType(String.class);
		Array<Expression> arguments = new Array<Expression>();

		InvokeConstructor invokeConstructor =
			new InvokeConstructor(
				coordinate,
				owner,
				arguments);

		Array<Error> errors = invokeConstructor.compile(methodVisitor, variables);

		assertEquals(0, errors.length());
	}
}
