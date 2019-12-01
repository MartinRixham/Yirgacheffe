package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.type.BytecodeClassLoader;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class InvokeInterfaceConstructorTest
{
	@Test
	public void testCompilingInvocationWithGenericReturnType() throws Exception
	{
		Variables variables = new LocalVariables(new HashMap<>());
		Coordinate coordinate = new Coordinate(1, 0);
		Type owner = new ReferenceType(this.getInterfaceClass());
		Expression one = new Num(coordinate, "1.0");
		Array<Expression> arguments = new Array<>(one);

		InvokeInterfaceConstructor invokeConstructor =
			new InvokeInterfaceConstructor(
				coordinate,
				owner,
				arguments);

		Type type = invokeConstructor.getType(variables);
		Result result = invokeConstructor.compileCondition(variables, null, null);
		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(1, variables.getStack().length());
		assertFalse(invokeConstructor.isCondition(variables));
		assertEquals(0, result.getErrors().length());
		assertEquals(4, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCONST_1, firstInstruction.getOpcode());

		LabelNode secondInstruction = (LabelNode) instructions.get(1);
		Label label = secondInstruction.getLabel();

		LineNumberNode thirdInstruction = (LineNumberNode) instructions.get(2);

		assertEquals(1, thirdInstruction.line);

		MethodInsnNode fourthInstruction = (MethodInsnNode) instructions.get(3);

		assertEquals(Opcodes.INVOKESTATIC, fourthInstruction.getOpcode());
		assertEquals("MyInterface", fourthInstruction.owner);
		assertEquals("0this", fourthInstruction.name);
		assertEquals("(D)LMyInterface;", fourthInstruction.desc);
		assertFalse(fourthInstruction.itf);

		assertEquals("MyInterface", type.toFullyQualifiedType());
	}

	@Test
	public void testGettingFirstOperand() throws Exception
	{
		Variables variables = new LocalVariables(new HashMap<>());
		Coordinate coordinate = new Coordinate(1, 0);
		Type owner = new ReferenceType(this.getInterfaceClass());
		Expression one = new Num(coordinate, "1.0");
		Array<Expression> arguments = new Array<>(one);

		InvokeInterfaceConstructor invokeConstructor =
			new InvokeInterfaceConstructor(
				coordinate,
				owner,
				arguments);

		Result result = invokeConstructor.compile(variables);

		assertEquals(0, result.getErrors().length());
		assertEquals(coordinate, invokeConstructor.getCoordinate());
	}

	@Test
	public void testGettingNoFirstOperand() throws Exception
	{
		Variables variables = new LocalVariables(new HashMap<>());
		Coordinate coordinate = new Coordinate(1, 0);
		Type owner = new ReferenceType(this.getInterfaceClass());

		Array<Expression> arguments =
			new Array<>(new This(coordinate, PrimitiveType.DOUBLE));

		InvokeInterfaceConstructor invokeConstructor =
			new InvokeInterfaceConstructor(
				coordinate,
				owner,
				arguments);

		Result result = invokeConstructor.compile(variables);

		assertEquals(0, result.getErrors().length());
	}

	private Class<?> getInterfaceClass() throws Exception
	{
		String source =
			"interface MyInterface\n" +
			"{\n" +
				"MyInterface(Num number)\n" +
				"{\n" +
					"return new MutableReference<MyInterface>().get();\n" +
				"}\n" +
			"}\n";

		Classes classes = new Classes();

		Compiler compiler = new Compiler("", source);

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		BytecodeClassLoader classLoader = new BytecodeClassLoader();

		classLoader.add("MyInterface", result.getBytecode());

		return classLoader.loadClass("MyInterface");
	}
}
