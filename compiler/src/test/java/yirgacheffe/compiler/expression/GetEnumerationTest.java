package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;
import yirgacheffe.lang.EnumerationWithDefault;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class GetEnumerationTest implements EnumerationWithDefault<String>
{
	public static GetEnumerationTest thingy()
	{
		return null;
	}

	@Test
	public void testGettingConstant()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		Variables variables = new LocalVariables(new HashMap<>());
		Type type = new ReferenceType(this.getClass());
		Expression expression = new Streeng(coordinate, "\"thingy\"");

		Expression enumeration = new GetEnumeration(coordinate, type, expression);

		Result result = enumeration.compileCondition(variables, null, null);

		Array<VariableRead> reads = enumeration.getVariableReads();
		assertEquals(coordinate, enumeration.getCoordinate());

		assertEquals(1, variables.getStack().length());
		assertEquals(0, result.getErrors().length());
		assertFalse(enumeration.isCondition(variables));
		assertEquals(0, reads.length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(1, instructions.length());

		MethodInsnNode firstInstruction = (MethodInsnNode) instructions.get(0);

		assertEquals(Opcodes.INVOKESTATIC, firstInstruction.getOpcode());
		assertEquals("thingy", firstInstruction.name);

		assertEquals(
			"yirgacheffe/compiler/expression/GetEnumerationTest",
			firstInstruction.owner);

		assertEquals(
			"()Lyirgacheffe/compiler/expression/GetEnumerationTest;",
			firstInstruction.desc);
	}

	@Test
	public void testGettingMissingConstant()
	{
		Coordinate coordinate = new Coordinate(3, 6);
		Variables variables = new LocalVariables(new HashMap<>());
		Type type = new ReferenceType(this.getClass());
		Expression expression = new Streeng(coordinate, "\"sumpt\"");

		Expression enumeration = new GetEnumeration(coordinate, type, expression);

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
		Expression expression = new Streeng(coordinate, "\"length\"");

		Expression enumeration = new GetEnumeration(coordinate, type, expression);

		Result result = enumeration.compile(variables);
		Array<Error> errors = result.getErrors();

		assertEquals(1, errors.length());
		assertEquals(
			"line 3:6 java.lang.String is not an enumeration.", errors.get(0).toString());
	}

	@Test
	public void testConstantOfWrongType()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		Variables variables = new LocalVariables(new HashMap<>());
		Type type = new ReferenceType(this.getClass());
		Expression expression = new Bool(coordinate, "true");

		Expression enumeration = new GetEnumeration(coordinate, type, expression);

		Result result = enumeration.compile(variables);

		assertEquals(2, result.getErrors().length());
		assertEquals(
			"line 3:5 Expected enumeration constant of type " +
				"java.lang.String but found Bool.",
			result.getErrors().get(0).toString());
	}

	@Test
	public void testGettingEnumerationFromExpression()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		Variables variables = new LocalVariables(new HashMap<>());
		Type type = new ReferenceType(this.getClass());
		Expression expression = new This(coordinate, new ReferenceType(String.class));

		Expression enumeration = new GetEnumeration(coordinate, type, expression);

		Result result = enumeration.compile(variables);

		assertEquals(1, variables.getStack().length());
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(4, instructions.length());

		FieldInsnNode firstInstruction = (FieldInsnNode) instructions.get(0);

		assertEquals(Opcodes.GETSTATIC, firstInstruction.getOpcode());
		assertEquals("values", firstInstruction.name);

		assertEquals(
			"yirgacheffe/compiler/expression/GetEnumerationTest",
			firstInstruction.owner);

		assertEquals("Ljava/util/Map;", firstInstruction.desc);

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.ALOAD, secondInstruction.getOpcode());
		assertEquals(0, secondInstruction.var);

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKEINTERFACE, thirdInstruction.getOpcode());
		assertEquals("get", thirdInstruction.name);
		assertEquals("java/util/Map", thirdInstruction.owner);
		assertEquals("(Ljava/lang/Object;)Ljava/lang/Object;", thirdInstruction.desc);
	}
}
