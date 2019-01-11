package yirgacheffe.compiler.statement;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.BinaryNumericOperation;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.InvokeMethod;
import yirgacheffe.compiler.expression.Literal;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.This;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BlockTest
{
	public void add(BlockTest test)
	{
	}

	@Test
	public void testFailToReadVariableDeclaredInBlock()
	{
		Signature caller = new Signature("method", new Array<>());
		Coordinate coordinate = new Coordinate(3, 5);
		VariableDeclaration variableDeclaration =
			new VariableDeclaration("myVariable", PrimitiveType.DOUBLE);
		Block block = new Block(coordinate, new Array<>(variableDeclaration));
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		block.compile(methodVisitor, variables, caller);

		VariableRead variableRead = new VariableRead(coordinate, "myVariable");

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
		Signature caller = new Signature("method", new Array<>());
		Coordinate coordinate = new Coordinate(4, 0);
		Return returnStatement = new Return(coordinate);
		Expression one = new Literal(PrimitiveType.DOUBLE, "1");
		VariableDeclaration variableDeclaration =
			new VariableDeclaration("myVariable", PrimitiveType.DOUBLE);
		VariableWrite variableWrite =
			new VariableWrite(coordinate, "myVariable", one);
		Array<Statement> statements =
			new Array<>(returnStatement, variableDeclaration, variableWrite);
		Block block = new Block(coordinate, statements);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Array<Error> errors = block.compile(methodVisitor, variables, caller);

		assertEquals(1, errors.length());
		assertEquals("line 4:0 Unreachable code.", errors.get(0).toString());
	}

	@Test
	public void testOptimiseRedundantLocalVariableForReturn()
	{
		Signature caller = new Signature("method", new Array<>());
		Coordinate coordinate = new Coordinate(4, 0);

		VariableDeclaration variableDeclaration =
			new VariableDeclaration("myVariable", PrimitiveType.DOUBLE);

		Expression number = new Literal(PrimitiveType.DOUBLE, "1");
		VariableWrite variableWrite = new VariableWrite(coordinate, "myVariable", number);
		VariableRead variableRead = new VariableRead(coordinate, "myVariable");

		Return returnStatement =
			new Return(coordinate, PrimitiveType.DOUBLE, variableRead);

		Array<Statement> statements =
			new Array<>(variableDeclaration, variableWrite, returnStatement);

		Block block = new Block(coordinate, statements);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Array<Error> errors = block.compile(methodVisitor, variables, caller);

		assertEquals(0, errors.length());

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
		Signature caller = new Signature("method", new Array<>());
		Coordinate coordinate = new Coordinate(4, 0);

		VariableDeclaration variableDeclaration =
			new VariableDeclaration("myVariable", PrimitiveType.DOUBLE);

		Expression number = new Literal(PrimitiveType.DOUBLE, "1");
		VariableWrite variableWrite = new VariableWrite(coordinate, "myVariable", number);
		VariableRead variableRead = new VariableRead(coordinate, "myVariable");

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

		Array<Error> errors = block.compile(methodVisitor, variables, caller);

		assertEquals(0, errors.length());

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
	public void testOptimiseTwoLocalVariablesForMethodCall()
	{
		Signature caller = new Signature("method", new Array<>());
		Coordinate coordinate = new Coordinate(4, 0);

		VariableDeclaration variableDeclaration =
				new VariableDeclaration("myVariable", PrimitiveType.DOUBLE);

		Expression one = new Literal(PrimitiveType.DOUBLE, "1");
		Expression two = new Literal(PrimitiveType.DOUBLE, "2");
		VariableWrite variableWrite = new VariableWrite(coordinate, "myVariable", one);
		VariableRead variableRead = new VariableRead(coordinate, "myVariable");

		VariableWrite notherVariableWrite =
				new VariableWrite(coordinate, "notherVariable", two);

		VariableRead notherVariableRead = new VariableRead(coordinate, "notherVariable");

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				"equals",
				"java/lang/Object",
				variableRead,
				new Array<>(notherVariableRead));

		FunctionCall functionCall = new FunctionCall(invokeMethod);

		Array<Statement> statements =
			new Array<>(
				variableDeclaration, variableWrite, notherVariableWrite, functionCall);

		Block block = new Block(coordinate, statements);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Array<Error> errors = block.compile(methodVisitor, variables, caller);

		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(6, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCONST_1, firstInstruction.getOpcode());

		MethodInsnNode secondInstruction = (MethodInsnNode) instructions.get(1);

		assertEquals(Opcodes.INVOKESTATIC, secondInstruction.getOpcode());
		assertEquals("valueOf", secondInstruction.name);

		LdcInsnNode thirdInstructionNode = (LdcInsnNode) instructions.get(2);

		assertEquals(Opcodes.LDC, thirdInstructionNode.getOpcode());
		assertEquals(2.0, thirdInstructionNode.cst);

		MethodInsnNode fourthInstructionNode = (MethodInsnNode) instructions.get(3);

		assertEquals(Opcodes.INVOKESTATIC, fourthInstructionNode.getOpcode());
		assertEquals("valueOf", fourthInstructionNode.name);

		MethodInsnNode fifthInstructionNode = (MethodInsnNode) instructions.get(4);

		assertEquals(Opcodes.INVOKEVIRTUAL, fifthInstructionNode.getOpcode());
		assertEquals("equals", fifthInstructionNode.name);

		InsnNode sixthInstructionNode = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.POP, sixthInstructionNode.getOpcode());
	}

	@Test
	public void testDoNotOptimiseVariableReferencedTwice()
	{
		Signature caller = new Signature("method", new Array<>());
		Coordinate coordinate = new Coordinate(4, 0);

		VariableDeclaration variableDeclaration =
			new VariableDeclaration("myVariable", PrimitiveType.DOUBLE);

		Expression number = new Literal(PrimitiveType.DOUBLE, "1");
		VariableWrite variableWrite = new VariableWrite(coordinate, "myVariable", number);
		VariableRead returnVariableRead = new VariableRead(coordinate, "myVariable");

		Return returnStatement =
			new Return(coordinate, PrimitiveType.DOUBLE, returnVariableRead);

		VariableRead variableRead = new VariableRead(coordinate, "myVariable");

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

		Array<Error> errors = block.compile(methodVisitor, variables, caller);

		assertEquals(0, errors.length());

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
		Signature caller = new Signature("method", new Array<>());
		Coordinate coordinate = new Coordinate(4, 0);

		This testClass = new This(new ReferenceType(this.getClass()));

		VariableDeclaration variableDeclaration =
			new VariableDeclaration("myVariable", new ReferenceType(this.getClass()));
		VariableWrite variableWrite =
			new VariableWrite(coordinate, "myVariable", testClass);

		VariableRead firstRead = new VariableRead(coordinate, "myVariable");
		VariableRead secondRead = new VariableRead(coordinate, "myVariable");

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

		Array<Error> errors = block.compile(methodVisitor, variables, caller);

		assertEquals(0, errors.length());

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
	public void testOptimiseSequenceOfLocalVariableLoadCalls()
	{
		Signature caller = new Signature("method", new Array<>());
		Coordinate coordinate = new Coordinate(4, 0);

		VariableDeclaration variableDeclaration =
			new VariableDeclaration("var", PrimitiveType.DOUBLE);

		Expression number = new Literal(PrimitiveType.DOUBLE, "1");
		VariableWrite firstWrite = new VariableWrite(coordinate, "var", number);

		VariableRead firstRead = new VariableRead(coordinate, "var");
		VariableWrite secondWrite = new VariableWrite(coordinate, "var", firstRead);

		VariableRead secondRead = new VariableRead(coordinate, "var");
		VariableWrite thirdWrite = new VariableWrite(coordinate, "var", secondRead);

		VariableRead thirdRead = new VariableRead(coordinate, "var");
		VariableWrite fourthWrite = new VariableWrite(coordinate, "var", thirdRead);

		Array<Statement> statements =
			new Array<>(
				variableDeclaration,
				firstWrite,
				secondWrite,
				thirdWrite,
				fourthWrite);

		Block block = new Block(coordinate, statements);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Array<Error> errors = block.compile(methodVisitor, variables, caller);

		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(2, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCONST_1, firstInstruction.getOpcode());

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.DSTORE, secondInstruction.getOpcode());
		assertEquals(1, secondInstruction.var);
	}

	@Test
	public void testOptimiseSequenceOfVariableDeclarationAndLoadCalls()
	{
		Signature caller = new Signature("method", new Array<>());
		Coordinate coordinate = new Coordinate(4, 0);

		VariableDeclaration firstDeclaration =
			new VariableDeclaration("var1", PrimitiveType.DOUBLE);
		Expression number = new Literal(PrimitiveType.DOUBLE, "1");
		VariableWrite firstWrite = new VariableWrite(coordinate, "var1", number);

		VariableDeclaration secondDeclaration =
			new VariableDeclaration("var2", PrimitiveType.DOUBLE);
		VariableRead firstRead = new VariableRead(coordinate, "var1");
		VariableWrite secondWrite = new VariableWrite(coordinate, "var2", firstRead);

		VariableDeclaration thirdDeclaration =
			new VariableDeclaration("var3", PrimitiveType.DOUBLE);
		VariableRead secondRead = new VariableRead(coordinate, "var2");
		VariableWrite thirdWrite = new VariableWrite(coordinate, "var3", secondRead);

		VariableDeclaration fourthDeclaration =
			new VariableDeclaration("var4", PrimitiveType.DOUBLE);
		VariableRead thirdRead = new VariableRead(coordinate, "var3");
		VariableWrite fourthWrite = new VariableWrite(coordinate, "var4", thirdRead);

		Array<Statement> statements =
			new Array<>(
				firstDeclaration,
				firstWrite,
				secondDeclaration,
				secondWrite,
				thirdDeclaration,
				thirdWrite,
				fourthDeclaration,
				fourthWrite);

		Block block = new Block(coordinate, statements);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Array<Error> errors = block.compile(methodVisitor, variables, caller);

		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(2, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCONST_1, firstInstruction.getOpcode());

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.DSTORE, secondInstruction.getOpcode());
		assertEquals(1, secondInstruction.var);
	}

	@Test
	public void testOptimiseVariableInAddition()
	{
		Signature caller = new Signature("method", new Array<>());
		Coordinate coordinate = new Coordinate(4, 0);

		VariableDeclaration firstDeclaration =
			new VariableDeclaration("var1", PrimitiveType.DOUBLE);
		Expression number = new Literal(PrimitiveType.DOUBLE, "1");
		VariableWrite firstWrite = new VariableWrite(coordinate, "var1", number);

		VariableDeclaration secondDeclaration =
			new VariableDeclaration("var2", PrimitiveType.DOUBLE);
		VariableRead firstRead = new VariableRead(coordinate, "var1");
		Expression one = new Literal(PrimitiveType.DOUBLE, "1");

		Expression addition =
			new BinaryNumericOperation(
				coordinate,
				Opcodes.DADD,
				"add",
				firstRead, one);

		VariableWrite secondWrite = new VariableWrite(coordinate, "var2", addition);

		Array<Statement> statements =
			new Array<>(
				firstDeclaration,
				firstWrite,
				secondDeclaration,
				secondWrite);

		Block block = new Block(coordinate, statements);
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Array<Error> errors = block.compile(methodVisitor, variables, caller);

		assertEquals(0, errors.length());

		InsnList instructions = methodVisitor.instructions;

		assertEquals(4, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCONST_1, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DCONST_1, secondInstruction.getOpcode());
	}

	@Test
	public void testEmptyBlock()
	{
		Coordinate coordinate = new Coordinate(3, 4);
		Block block = new Block(coordinate, new Array<>());

		Expression expression = block.getExpression();

		assertTrue(expression instanceof Nothing);
		assertTrue(block.isEmpty());
	}

	@Test
	public void testGettingVariables()
	{
		Coordinate coordinate = new Coordinate(3, 5);
		VariableRead read = new VariableRead(coordinate, "myVariable");
		VariableWrite write = new VariableWrite(coordinate, "var", read);
		Statement block = new Block(coordinate, new Array<>(write));

		Array<VariableRead> reads = block.getVariableReads();

		assertTrue(reads.indexOf(read) >= 0);

		Array<VariableWrite> writes = block.getVariableWrites();

		assertTrue(writes.indexOf(write) >= 0);

		assertEquals(read, block.getExpression());
		assertFalse(block.isEmpty());
	}
}
