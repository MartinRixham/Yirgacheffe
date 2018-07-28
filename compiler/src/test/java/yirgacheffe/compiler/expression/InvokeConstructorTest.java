package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.statement.StatementResult;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class InvokeConstructorTest
{
	@Test
	public void testCompilingInvocationWithGenericReturnType()
	{
		MethodNode methodVisitor = new MethodNode();
		StatementResult result = new StatementResult();
		Coordinate coordinate = new Coordinate(1, 0);
		Type owner = new ReferenceType(Double.class);
		Expression one = new Literal(PrimitiveType.DOUBLE, "1");
		Array<Expression> arguments = new Array<Expression>(one);

		InvokeConstructor invokeConstructor =
			new InvokeConstructor(
				coordinate,
				owner,
				arguments);

		Type type = invokeConstructor.check(result);

		invokeConstructor.compile(methodVisitor);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(4, instructions.size());

		TypeInsnNode firstInstruction = (TypeInsnNode) instructions.get(0);

		assertEquals(Opcodes.NEW, firstInstruction.getOpcode());
		assertEquals("java/lang/Double", firstInstruction.desc);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DCONST_1, thirdInstruction.getOpcode());

		MethodInsnNode fourthInstruction = (MethodInsnNode) instructions.get(3);

		assertEquals(Opcodes.INVOKESPECIAL, fourthInstruction.getOpcode());
		assertEquals("java/lang/Double", fourthInstruction.owner);
		assertEquals("<init>", fourthInstruction.name);
		assertEquals("(D)V", fourthInstruction.desc);
		assertFalse(fourthInstruction.itf);

		assertEquals("java.lang.Double", type.toFullyQualifiedType());
	}
}
