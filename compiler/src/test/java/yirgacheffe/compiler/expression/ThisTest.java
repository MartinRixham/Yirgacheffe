package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ThisTest
{
	@Test
	public void testCompilingThis()
	{
		Variables variables = new Variables(new HashMap<>());

		This thisRead = new This(new ReferenceType(this.getClass()));

		Type type = thisRead.getType(variables);
		Result result = thisRead.compileCondition(variables, null, null);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(0, result.getErrors().length());
		assertEquals(1, instructions.length());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertFalse(thisRead.isCondition(variables));
		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		assertEquals(
			"yirgacheffe/compiler/expression/ThisTest",
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
