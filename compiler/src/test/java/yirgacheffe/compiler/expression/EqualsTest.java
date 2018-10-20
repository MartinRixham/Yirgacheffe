package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;

public class EqualsTest
{
	@Test
	public void testCompilingEqualObjects()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();
		This firstOperand = new This(new ReferenceType(String.class));
		This secondOperand = new This(new ReferenceType(String.class));
		Equals equals = new Equals(firstOperand, secondOperand);

		Type type = equals.getType(variables);

		Array<Error> errors = equals.compile(methodVisitor, variables);

		assertEquals(PrimitiveType.BOOLEAN, type);
		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(2, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, secondInstruction.getOpcode());
		assertEquals(0, secondInstruction.var);
	}
}
