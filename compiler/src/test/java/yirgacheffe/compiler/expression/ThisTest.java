package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ThisTest
{
	@Test
	public void testCompilingThis()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		Variables variables = new LocalVariables(1, new HashMap<>(), new HashMap<>());

		This thisRead = new This(coordinate, new ReferenceType(this.getClass()));

		Type type = thisRead.getType(variables);
		Result result = thisRead.compileCondition(variables, null, null);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(1, variables.getStack().length());
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
		Coordinate coordinate = new Coordinate(3, 65);

		Expression thisStatement =
			new This(coordinate, new ReferenceType(this.getClass()));

		Array<VariableRead> reads = thisStatement.getVariableReads();

		assertEquals(0, reads.length());
		assertEquals(coordinate, thisStatement.getCoordinate());
	}
}
