package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class EnumerationTest implements yirgacheffe.lang.Enumeration<String>
{
	public static EnumerationTest thingy()
	{
		return null;
	}

	@Test
	public void testGettingConstant()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		Variables variables = new LocalVariables(new HashMap<>());
		Type type = new ReferenceType(this.getClass());
		Expression expression = new Streeng("\"thingy\"");

		Expression enumeration = new Enumeration(coordinate, type, expression);

		Result result = enumeration.compileCondition(variables, null, null);

		Array<VariableRead> reads = enumeration.getVariableReads();

		assertEquals(0, result.getErrors().length());
		assertFalse(enumeration.isCondition(variables));
		assertEquals(0, reads.length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(1, instructions.length());

		MethodInsnNode firstInstruction = (MethodInsnNode) instructions.get(0);

		assertEquals(Opcodes.INVOKESTATIC, firstInstruction.getOpcode());
		assertEquals("thingy", firstInstruction.name);

		assertEquals(
			"yirgacheffe/compiler/expression/EnumerationTest",
			firstInstruction.owner);

		assertEquals(
			"()Lyirgacheffe/compiler/expression/EnumerationTest;",
			firstInstruction.desc);
	}

	@Test
	public void testGettingMissingConstant()
	{
		Coordinate coordinate = new Coordinate(3, 6);
		Variables variables = new LocalVariables(new HashMap<>());
		Type type = new ReferenceType(this.getClass());
		Expression expression = new Streeng("\"sumpt\"");

		Expression enumeration = new Enumeration(coordinate, type, expression);

		Result result = enumeration.compile(variables);
		Array<Error> errors = result.getErrors();

		assertEquals(1, errors.length());
		assertEquals(
			"line 3:6 Unknown enumeration constant 'sumpt'.", errors.get(0).toString());
	}

	@Test
	public void testNotAnEnumeration()
	{
		Coordinate coordinate = new Coordinate(3, 6);
		Variables variables = new LocalVariables(new HashMap<>());
		Type type = new ReferenceType(String.class);
		Expression expression = new Streeng("\"length\"");

		Expression enumeration = new Enumeration(coordinate, type, expression);

		Result result = enumeration.compile(variables);
		Array<Error> errors = result.getErrors();

		assertEquals(1, errors.length());
		assertEquals(
			"line 3:6 java.lang.String is not an enumeration.", errors.get(0).toString());
	}
}
