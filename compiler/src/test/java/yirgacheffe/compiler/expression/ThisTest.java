package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;

public class ThisTest
{
	@Test
	public void testCompilingThis()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		This thisRead = new This(new ReferenceType(this.getClass()));

		Type type = thisRead.getType(variables);

		thisRead.compile(methodVisitor, variables);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(1, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		assertEquals(
			"yirgacheffe.compiler.expression.ThisTest",
			type.toFullyQualifiedType());
	}

	@Test
	public void testGettingVariableReads()
	{
		Expression thisStatement = new This(new ReferenceType(this.getClass()));

		Array<VariableRead> reads = thisStatement.getVariableReads();

		assertEquals(0, reads.length());
	}
}
