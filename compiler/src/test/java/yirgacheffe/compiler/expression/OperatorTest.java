package yirgacheffe.compiler.expression;

import org.objectweb.asm.Opcodes;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OperatorTest
{
	@Test
	public void testAdd()
	{
		Operator add = Operator.ADD;

		assertEquals("add", add.getDescription());
		assertEquals(Opcodes.DADD, add.getDoubleOpcode());
		assertEquals(Opcodes.LADD, add.getLongOpcode());
		assertEquals(Opcodes.IADD, add.getIntOpcode());
	}

	@Test
	public void testSubtract()
	{
		Operator add = Operator.SUBTRACT;

		assertEquals("subtract", add.getDescription());
		assertEquals(Opcodes.DSUB, add.getDoubleOpcode());
		assertEquals(Opcodes.LSUB, add.getLongOpcode());
		assertEquals(Opcodes.ISUB, add.getIntOpcode());
	}

	@Test
	public void testMultiply()
	{
		Operator add = Operator.MULTIPLY;

		assertEquals("multiply", add.getDescription());
		assertEquals(Opcodes.DMUL, add.getDoubleOpcode());
		assertEquals(Opcodes.LMUL, add.getLongOpcode());
		assertEquals(Opcodes.IMUL, add.getIntOpcode());
	}

	@Test
	public void testDivide()
	{
		Operator add = Operator.DIVIDE;

		assertEquals("divide", add.getDescription());
		assertEquals(Opcodes.DDIV, add.getDoubleOpcode());
		assertEquals(Opcodes.LDIV, add.getLongOpcode());
		assertEquals(Opcodes.IDIV, add.getIntOpcode());
	}

	@Test
	public void testRemainder()
	{
		Operator add = Operator.REMAINDER;

		assertEquals("find remainder of", add.getDescription());
		assertEquals(Opcodes.DREM, add.getDoubleOpcode());
		assertEquals(Opcodes.LREM, add.getLongOpcode());
		assertEquals(Opcodes.IREM, add.getIntOpcode());
	}
}
