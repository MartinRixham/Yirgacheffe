package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variable;

import static org.junit.Assert.assertEquals;

public class VariableReadTest
{
	@Test
	public void testCompilingStringRead()
	{
		MethodNode methodVisitor = new MethodNode();
		Type owner = new ReferenceType(String.class);
		Variable variable = new Variable(1, owner);

		VariableRead variableRead = new VariableRead(variable);

		variableRead.compile(methodVisitor);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(1, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);
	}

	@Test
	public void testCompilingNumberRead()
	{
		MethodNode methodVisitor = new MethodNode();
		Type owner = PrimitiveType.DOUBLE;
		Variable variable = new Variable(1, owner);

		VariableRead variableRead = new VariableRead(variable);

		variableRead.compile(methodVisitor);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(1, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.DLOAD, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);
	}
}
