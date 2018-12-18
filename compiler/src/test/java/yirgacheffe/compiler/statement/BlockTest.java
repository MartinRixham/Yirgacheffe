package yirgacheffe.compiler.statement;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.InvokeMethod;
import yirgacheffe.compiler.expression.Literal;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.This;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BlockTest
{
	public void add(BlockTest test)
	{
	}

	@Test
	public void testFailToReadVariableDeclaredInBlock()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		VariableDeclaration variableDeclaration =
			new VariableDeclaration("myVariable", PrimitiveType.DOUBLE);
		Block block = new Block(coordinate, new Array<>(variableDeclaration));
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		block.compile(methodVisitor, variables);

		VariableRead variableRead = new VariableRead("myVariable", coordinate);

		variableRead.getType(variables);
		variableRead.compile(methodVisitor, variables);

		assertEquals(1, variables.getErrors().length());
		assertEquals(
			"line 3:5 Unknown local variable 'myVariable'.",
			variables.getErrors().get(0).toString());
	}

	@Test
	public void testUnreachableCode()
	{
		Coordinate coordinate = new Coordinate(4, 0);
		Return returnStatement = new Return(coordinate);
		VariableDeclaration variableDeclaration =
			new VariableDeclaration("myVariable", PrimitiveType.DOUBLE);
		VariableDeclaration anotherDeclaration =
			new VariableDeclaration("anotherVariable", PrimitiveType.DOUBLE);
		Array<Statement> statements =
			new Array<>(returnStatement, variableDeclaration, anotherDeclaration);
		Block block = new Block(coordinate, statements);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		StatementResult result = block.compile(methodVisitor, variables);

		assertEquals(1, result.getErrors().length());
		assertEquals("line 4:0 Unreachable code.",
			result.getErrors().get(0).toString());
	}

	@Test
	public void testOptimiseRedundantLocalVariableForReturn()
	{
		Coordinate coordinate = new Coordinate(4, 0);

		VariableDeclaration variableDeclaration =
			new VariableDeclaration("myVariable", PrimitiveType.DOUBLE);

		Expression number = new Literal(PrimitiveType.DOUBLE, "1");
		VariableWrite variableWrite = new VariableWrite("myVariable", number, coordinate);
		VariableRead variableRead = new VariableRead("myVariable", coordinate);

		Return returnStatement =
			new Return(coordinate, PrimitiveType.DOUBLE, variableRead);

		Array<Statement> statements =
			new Array<>(variableDeclaration, variableWrite, returnStatement);

		Block block = new Block(coordinate, statements);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		StatementResult result = block.compile(methodVisitor, variables);

		assertEquals(0, result.getErrors().length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(2, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCONST_1, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DRETURN, secondInstruction.getOpcode());
	}

	@Test
	public void testOptimiseRedundantLocalVariableForMethodCall()
	{
		Coordinate coordinate = new Coordinate(4, 0);

		VariableDeclaration variableDeclaration =
			new VariableDeclaration("myVariable", PrimitiveType.DOUBLE);

		Expression number = new Literal(PrimitiveType.DOUBLE, "1");
		VariableWrite variableWrite = new VariableWrite("myVariable", number, coordinate);
		VariableRead variableRead = new VariableRead("myVariable", coordinate);

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				"toString",
				"java/lang/Object",
				variableRead,
				new Array<>());

		FunctionCall functionCall = new FunctionCall(invokeMethod);
		Array<Statement> statements =
			new Array<>(variableDeclaration, variableWrite, functionCall);
		Block block = new Block(coordinate, statements);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		StatementResult result = block.compile(methodVisitor, variables);

		assertEquals(0, result.getErrors().length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(4, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCONST_1, firstInstruction.getOpcode());

		MethodInsnNode secondInstruction = (MethodInsnNode) instructions.get(1);

		assertEquals(Opcodes.INVOKESTATIC, secondInstruction.getOpcode());
		assertEquals("valueOf", secondInstruction.name);

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKEVIRTUAL, thirdInstruction.getOpcode());
		assertEquals("toString", thirdInstruction.name);

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.POP, fourthInstruction.getOpcode());
	}

	@Test
	public void testDoNotOptimiseVariableReferencedTwice()
	{
		Coordinate coordinate = new Coordinate(4, 0);

		VariableDeclaration variableDeclaration =
			new VariableDeclaration("myVariable", PrimitiveType.DOUBLE);

		Expression number = new Literal(PrimitiveType.DOUBLE, "1");
		VariableWrite variableWrite = new VariableWrite("myVariable", number, coordinate);
		VariableRead returnVariableRead = new VariableRead("myVariable", coordinate);

		Return returnStatement =
			new Return(coordinate, PrimitiveType.DOUBLE, returnVariableRead);

		VariableRead variableRead = new VariableRead("myVariable", coordinate);

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				"toString",
				"java/lang/Object",
				variableRead,
				new Array<>());

		FunctionCall functionCall = new FunctionCall(invokeMethod);

		Array<Statement> statements =
			new Array<>(
				variableDeclaration,
				variableWrite,
				functionCall,
				returnStatement);

		Block block = new Block(coordinate, statements);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		StatementResult result = block.compile(methodVisitor, variables);

		assertEquals(0, result.getErrors().length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(8, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCONST_1, firstInstruction.getOpcode());

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.DSTORE, secondInstruction.getOpcode());
		assertEquals(1, secondInstruction.var);

		VarInsnNode thirdInstruction = (VarInsnNode) instructions.get(2);

		assertEquals(Opcodes.DLOAD, thirdInstruction.getOpcode());
		assertEquals(1, thirdInstruction.var);

		MethodInsnNode fourthInstruction = (MethodInsnNode) instructions.get(3);

		assertEquals(Opcodes.INVOKESTATIC, fourthInstruction.getOpcode());
		assertEquals("valueOf", fourthInstruction.name);

		MethodInsnNode fifthInstruction = (MethodInsnNode) instructions.get(4);

		assertEquals(Opcodes.INVOKEVIRTUAL, fifthInstruction.getOpcode());
		assertEquals("toString", fifthInstruction.name);

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.POP, sixthInstruction.getOpcode());

		VarInsnNode seventhInstruction = (VarInsnNode) instructions.get(6);

		assertEquals(Opcodes.DLOAD, seventhInstruction.getOpcode());

		InsnNode eigjthInstruction = (InsnNode) instructions.get(7);

		assertEquals(Opcodes.DRETURN, eigjthInstruction.getOpcode());
	}

	@Test
	public void testDoNotOptimiseVariableReferencedTwiceAndNotFirstOperand()
	{
		Coordinate coordinate = new Coordinate(4, 0);

		This testClass = new This(new ReferenceType(this.getClass()));

		VariableDeclaration variableDeclaration =
			new VariableDeclaration("myVariable", new ReferenceType(this.getClass()));
		VariableWrite variableWrite =
			new VariableWrite("myVariable", testClass, coordinate);

		VariableRead firstRead = new VariableRead("myVariable", coordinate);
		VariableRead secondRead = new VariableRead("myVariable", coordinate);

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				"add",
				"yirgacheffe/compiler/statement/BlockTest",
				firstRead,
				new Array<>(secondRead));

		FunctionCall functionCall = new FunctionCall(invokeMethod);

		Array<Statement> statements =
			new Array<>(variableDeclaration, variableWrite, functionCall);

		Block block = new Block(coordinate, statements);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		StatementResult result = block.compile(methodVisitor, variables);

		assertEquals(0, result.getErrors().length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(5, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.ASTORE, secondInstruction.getOpcode());
		assertEquals(1, secondInstruction.var);

		VarInsnNode thirdInstruction = (VarInsnNode) instructions.get(2);

		assertEquals(Opcodes.ALOAD, thirdInstruction.getOpcode());
		assertEquals(1, thirdInstruction.var);

		VarInsnNode fourthInstruction = (VarInsnNode) instructions.get(3);

		assertEquals(Opcodes.ALOAD, fourthInstruction.getOpcode());
		assertEquals(1, fourthInstruction.var);

		MethodInsnNode fifthInstruction = (MethodInsnNode) instructions.get(4);

		assertEquals(Opcodes.INVOKEVIRTUAL, fifthInstruction.getOpcode());
		assertEquals("add", fifthInstruction.name);
	}

	@Test
	public void testFirstOperandIsNothing()
	{
		Coordinate coordinate = new Coordinate(3, 4);
		Block block = new Block(coordinate, new Array<>());

		Expression operand = block.getFirstOperand();

		assertTrue(operand instanceof Nothing);
	}

	@Test
	public void testGettingVariableReads()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		VariableRead read = new VariableRead("myVariable", coordinate);
		VariableWrite write = new VariableWrite("var", read, coordinate);
		Statement block = new Block(coordinate, new Array<>(write));

		Array<VariableRead> reads = block.getVariableReads();

		assertTrue(reads.indexOf(read) >= 0);
		assertEquals(read, block.getFirstOperand());
	}
}
