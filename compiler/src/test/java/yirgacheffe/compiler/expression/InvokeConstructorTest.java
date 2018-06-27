package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import yirgacheffe.compiler.function.Callable;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;

import static org.junit.Assert.assertEquals;

public class InvokeConstructorTest
{
	@Test
	public void testCompilingInvocationWithGenericReturnType() throws Exception
	{
		MethodNode methodVisitor = new MethodNode();
		Type owner = new ReferenceType(Double.class);
		Callable function =
			new Function(owner, Double.class.getConstructor(double.class));
		Expression[] arguments = new Expression[] {new Literal(1)};

		InvokeConstructor invokeMethod = new InvokeConstructor(function, arguments);

		invokeMethod.compile(methodVisitor);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(4, instructions.size());

		TypeInsnNode firstInstruction = (TypeInsnNode) instructions.get(0);

		assertEquals(Opcodes.NEW, firstInstruction.getOpcode());
		assertEquals("java/lang/Double", firstInstruction.desc);
	}
}
