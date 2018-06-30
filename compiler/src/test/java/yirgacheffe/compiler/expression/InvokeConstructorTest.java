package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import yirgacheffe.compiler.function.Callable;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class InvokeConstructorTest
{
	@Test
	public void testCompilingInvocationWithGenericReturnType() throws Exception
	{
		MethodNode methodVisitor = new MethodNode();
		Type owner = new ReferenceType(Double.class);
		Callable function =
			new Function(owner, Double.class.getConstructor(double.class));
		Expression one = new Literal(PrimitiveType.DOUBLE, "1");
		Expression[] arguments = new Expression[] {one};

		InvokeConstructor invokeConstructor = new InvokeConstructor(function, arguments);

		invokeConstructor.compile(methodVisitor);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(4, instructions.size());

		TypeInsnNode firstInstruction = (TypeInsnNode) instructions.get(0);

		assertEquals(Opcodes.NEW, firstInstruction.getOpcode());
		assertEquals("java/lang/Double", firstInstruction.desc);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		LdcInsnNode thirdInstruction = (LdcInsnNode) instructions.get(2);

		assertEquals(Opcodes.LDC, thirdInstruction.getOpcode());
		assertEquals(1.0, thirdInstruction.cst);

		MethodInsnNode fourthInstruction = (MethodInsnNode) instructions.get(3);

		assertEquals(Opcodes.INVOKESPECIAL, fourthInstruction.getOpcode());
		assertEquals("java/lang/Double", fourthInstruction.owner);
		assertEquals("<init>", fourthInstruction.name);
		assertEquals("(D)V", fourthInstruction.desc);
		assertFalse(fourthInstruction.itf);
	}
}
