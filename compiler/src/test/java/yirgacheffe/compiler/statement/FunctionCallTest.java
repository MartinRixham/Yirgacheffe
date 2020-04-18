package yirgacheffe.compiler.statement;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.InvokeConstructor;
import yirgacheffe.compiler.expression.Num;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.FunctionSignature;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.implementation.NullImplementation;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FunctionCallTest
{
	@Test
	public void testFunctionThatReturnsDouble()
	{
		Signature caller = new FunctionSignature(new NullType(), "method", new Array<>());
		Coordinate coordinate = new Coordinate(1, 0);
		Array<Expression> arguments =
			new Array<>(new Num(coordinate, "3.0"));

		InvokeConstructor invoke =
			new InvokeConstructor(
				coordinate,
				PrimitiveType.DOUBLE,
				arguments);

		FunctionCall functionCall = new FunctionCall(invoke);

		LocalVariables variables = new LocalVariables(1, new HashMap<>());
		Result result = functionCall.compile(variables, caller);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(0, variables.getStack().length());
		assertEquals(7, instructions.length());

		TypeInsnNode firstInstruction = (TypeInsnNode) instructions.get(0);

		assertEquals(Opcodes.NEW, firstInstruction.getOpcode());
		assertEquals("java/lang/Double", firstInstruction.desc);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		LdcInsnNode thirdInstruction = (LdcInsnNode) instructions.get(2);

		assertEquals(Opcodes.LDC, thirdInstruction.getOpcode());
		assertEquals(3.0, thirdInstruction.cst);

		assertTrue(instructions.get(3) instanceof LabelNode);
		assertTrue(instructions.get(4) instanceof LineNumberNode);

		MethodInsnNode sixthInstruction = (MethodInsnNode) instructions.get(5);

		assertEquals(Opcodes.INVOKESPECIAL, sixthInstruction.getOpcode());

		InsnNode seventhInstruction = (InsnNode) instructions.get(6);

		assertEquals(Opcodes.POP2, seventhInstruction.getOpcode());
	}

	@Test
	public void testGettingVariableReads()
	{
		Coordinate coordinate = new Coordinate(2, 4);
		VariableRead variable = new VariableRead(coordinate, "var");

		Expression expression =
			new Expression()
			{
				public Type getType(Variables variables)
				{
					return new NullType();
				}

				public Result compile(Variables variables)
				{
					return new Result();
				}

				public Result compileCondition(
					Variables variables,
					Label trueLabel,
					Label falseLabel)
				{
					return new Result();
				}

				public boolean isCondition(Variables variables)
				{
					return false;
				}

				public Array<VariableRead> getVariableReads()
				{

					return new Array<>(variable);
				}

				public Coordinate getCoordinate()
				{
					return new Coordinate(0, 0);
				}
			};

		FunctionCall functionCall = new FunctionCall(expression);

		assertEquals(expression.hashCode(), functionCall.hashCode());
		assertEquals(1, functionCall.getVariableReads().length());
		assertEquals(variable, functionCall.getVariableReads().get(0));
		assertFalse(functionCall.getFieldAssignments().contains(""));
		assertFalse(functionCall.isEmpty());

		Type thisType = new ReferenceType(this.getClass());
		Implementation implementation =
			functionCall.getDelegatedInterfaces(new HashMap<>(), thisType);

		assertTrue(implementation instanceof NullImplementation);
	}
}
