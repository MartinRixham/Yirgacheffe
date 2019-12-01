package yirgacheffe.compiler.statement;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.BinaryOperation;
import yirgacheffe.compiler.expression.Delegate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.InvokeMethod;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.Num;
import yirgacheffe.compiler.expression.Streeng;
import yirgacheffe.compiler.expression.This;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.implementation.InterfaceImplementation;
import yirgacheffe.compiler.operator.Operator;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.lang.Array;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BlockTest
{
	public Comparable<String> getComparable()
	{
		return null;
	}

	public void add(BlockTest test)
	{
	}

	@Test
	public void testFailToReadVariableDeclaredInBlock()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Coordinate coordinate = new Coordinate(3, 5);
		VariableDeclaration variableDeclaration =
			new VariableDeclaration("myVariable", PrimitiveType.DOUBLE);
		Block block = new Block(coordinate, new Array<>(variableDeclaration));
		LocalVariables variables = new LocalVariables(new HashMap<>());

		block.compile(variables, caller);

		VariableRead variableRead = new VariableRead(coordinate, "myVariable");

		variableRead.getType(variables);
		variableRead.compile(variables);

		assertEquals(1, variables.getErrors().length());
		assertEquals(
			"line 3:5 Unknown local variable 'myVariable'.",
			variables.getErrors().get(0).toString());
	}

	@Test
	public void testUnreachableCode()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Coordinate coordinate = new Coordinate(4, 0);
		Return returnStatement = new Return(coordinate, PrimitiveType.VOID);
		Array<Statement> statements = new Array<>(returnStatement, returnStatement);
		Block block = new Block(coordinate, statements);
		LocalVariables variables = new LocalVariables(new HashMap<>());
		Result result = block.compile(variables, caller);

		assertEquals(1, result.getErrors().length());
		assertEquals("line 4:0 Unreachable code.", result.getErrors().get(0).toString());
	}

	@Test
	public void testReturnAtEndOfBlock()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Coordinate coordinate = new Coordinate(4, 0);
		Return returnStatement = new Return(coordinate, PrimitiveType.VOID);
		Array<Statement> statements = new Array<>(returnStatement);
		Block block = new Block(coordinate, statements);
		LocalVariables variables = new LocalVariables(new HashMap<>());
		Result result = block.compile(variables, caller);

		assertEquals(0, result.getErrors().length());
	}

	@Test
	public void testOptimiseRedundantLocalVariableForReturn()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Coordinate coordinate = new Coordinate(4, 0);

		VariableDeclaration variableDeclaration =
			new VariableDeclaration("myVariable", PrimitiveType.DOUBLE);

		Expression number = new Num(coordinate, "1.0");
		VariableWrite variableWrite = new VariableWrite(coordinate, "myVariable", number);
		VariableRead variableRead = new VariableRead(coordinate, "myVariable");

		Return returnStatement =
			new Return(coordinate, PrimitiveType.DOUBLE, variableRead);

		Array<Statement> statements =
			new Array<>(variableDeclaration, variableWrite, returnStatement);

		Block block = new Block(coordinate, statements);
		LocalVariables variables = new LocalVariables(new HashMap<>());

		Result result = block.compile(variables, caller);

		assertEquals(0, variables.getStack().length());
		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(2, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCONST_1, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DRETURN, secondInstruction.getOpcode());
	}

	@Test
	public void testOptimiseRedundantLocalVariableForMethodCall()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Coordinate coordinate = new Coordinate(4, 0);

		VariableDeclaration variableDeclaration =
			new VariableDeclaration("myVariable", PrimitiveType.DOUBLE);

		Expression number = new Num(coordinate, "1.0");
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
		LocalVariables variables = new LocalVariables(new HashMap<>());

		Result result = block.compile(variables, caller);

		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(6, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCONST_1, firstInstruction.getOpcode());

		MethodInsnNode secondInstruction = (MethodInsnNode) instructions.get(1);

		assertEquals(Opcodes.INVOKESTATIC, secondInstruction.getOpcode());
		assertEquals("valueOf", secondInstruction.name);

		LabelNode thirdInstruction = (LabelNode) instructions.get(2);
		Label label = thirdInstruction.getLabel();

		LineNumberNode fourthInstruction = (LineNumberNode) instructions.get(3);

		assertEquals(label, fourthInstruction.start.getLabel());

		InvokeDynamicInsnNode fifthInstruction =
			(InvokeDynamicInsnNode) instructions.get(4);

		assertEquals(Opcodes.INVOKEDYNAMIC, fifthInstruction.getOpcode());
		assertEquals("toString", fifthInstruction.name);

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.POP, sixthInstruction.getOpcode());
	}

	@Test
	public void testOptimiseTwoLocalVariablesForMethodCall()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Coordinate coordinate = new Coordinate(4, 0);

		VariableDeclaration variableDeclaration =
				new VariableDeclaration("myVariable", PrimitiveType.DOUBLE);
		VariableDeclaration notherVariableDeclaration =
			new VariableDeclaration("notherVariable", PrimitiveType.DOUBLE);

		Expression one = new Num(coordinate, "1.0");
		Expression two = new Num(coordinate, "2.0");
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
				variableDeclaration,
				notherVariableDeclaration,
				variableWrite,
				notherVariableWrite,
				functionCall);

		Block block = new Block(coordinate, statements);
		LocalVariables variables = new LocalVariables(new HashMap<>());

		Result result = block.compile(variables, caller);

		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(8, instructions.length());

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

		LabelNode fifthInstruction = (LabelNode) instructions.get(4);
		Label label = fifthInstruction.getLabel();

		LineNumberNode sixthInstruction = (LineNumberNode) instructions.get(5);

		assertEquals(label, sixthInstruction.start.getLabel());

		InvokeDynamicInsnNode seventhInstruction =
			(InvokeDynamicInsnNode) instructions.get(6);

		assertEquals(Opcodes.INVOKEDYNAMIC, seventhInstruction.getOpcode());
		assertEquals("equals", seventhInstruction.name);

		InsnNode eighthInstruction = (InsnNode) instructions.get(7);

		assertEquals(Opcodes.POP, eighthInstruction.getOpcode());
	}

	@Test
	public void testDoNotOptimiseVariableReferencedTwice()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Coordinate coordinate = new Coordinate(4, 0);

		VariableDeclaration variableDeclaration =
			new VariableDeclaration("myVariable", PrimitiveType.DOUBLE);

		Expression number = new Num(coordinate, "1.0");
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
		LocalVariables variables = new LocalVariables(new HashMap<>());

		Result result = block.compile(variables, caller);

		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(10, instructions.length());

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

		LabelNode fifthInstruction = (LabelNode) instructions.get(4);
		Label label = fifthInstruction.getLabel();

		LineNumberNode sixthInstruction = (LineNumberNode) instructions.get(5);

		assertEquals(label, sixthInstruction.start.getLabel());

		InvokeDynamicInsnNode seventhInstruction =
			(InvokeDynamicInsnNode) instructions.get(6);

		assertEquals(Opcodes.INVOKEDYNAMIC, seventhInstruction.getOpcode());
		assertEquals("toString", seventhInstruction.name);

		InsnNode eighthInstruction = (InsnNode) instructions.get(7);

		assertEquals(Opcodes.POP, eighthInstruction.getOpcode());

		VarInsnNode ninthInstruction = (VarInsnNode) instructions.get(8);

		assertEquals(Opcodes.DLOAD, ninthInstruction.getOpcode());

		InsnNode tenthInstruction = (InsnNode) instructions.get(9);

		assertEquals(Opcodes.DRETURN, tenthInstruction.getOpcode());
	}

	@Test
	public void testDoNotOptimiseVariableReferencedTwiceAndNotFirstOperand()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Coordinate coordinate = new Coordinate(4, 0);

		This testClass = new This(coordinate, new ReferenceType(this.getClass()));

		VariableDeclaration variableDeclaration =
			new VariableDeclaration(
				"myVariable",
				new ReferenceType(this.getClass()));

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
		LocalVariables variables = new LocalVariables(new HashMap<>());

		Result result = block.compile(variables, caller);

		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(7, instructions.length());

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

		LabelNode fifthInstruction = (LabelNode) instructions.get(4);
		Label label = fifthInstruction.getLabel();

		LineNumberNode sixthInstruction = (LineNumberNode) instructions.get(5);

		assertEquals(label, sixthInstruction.start.getLabel());

		InvokeDynamicInsnNode seventhInstruction =
			(InvokeDynamicInsnNode) instructions.get(6);

		assertEquals(Opcodes.INVOKEDYNAMIC, seventhInstruction.getOpcode());
		assertEquals("add", seventhInstruction.name);
	}

	@Test
	public void testOptimiseSequenceOfLocalVariableLoadCalls()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Coordinate coordinate = new Coordinate(4, 0);

		VariableDeclaration variableDeclaration =
			new VariableDeclaration("var", PrimitiveType.DOUBLE);

		Expression number = new Num(coordinate, "1.0");
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
		LocalVariables variables = new LocalVariables(new HashMap<>());

		Result result = block.compile(variables, caller);

		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(2, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCONST_1, firstInstruction.getOpcode());

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.DSTORE, secondInstruction.getOpcode());
		assertEquals(1, secondInstruction.var);
	}

	@Test
	public void testOptimiseSequenceOfVariableDeclarationAndLoadCalls()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Coordinate coordinate = new Coordinate(4, 0);

		VariableDeclaration firstDeclaration =
			new VariableDeclaration("var1", PrimitiveType.DOUBLE);
		Expression number = new Num(coordinate, "1.0");
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
		LocalVariables variables = new LocalVariables(new HashMap<>());

		Result result = block.compile(variables, caller);

		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(2, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCONST_1, firstInstruction.getOpcode());

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.DSTORE, secondInstruction.getOpcode());
		assertEquals(1, secondInstruction.var);
	}

	@Test
	public void testOptimiseVariableInAddition()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Coordinate coordinate = new Coordinate(4, 0);

		VariableDeclaration firstDeclaration =
			new VariableDeclaration("var1", PrimitiveType.DOUBLE);
		Expression number = new Num(coordinate, "0.0");
		VariableWrite firstWrite = new VariableWrite(coordinate, "var1", number);

		VariableDeclaration secondDeclaration =
			new VariableDeclaration("var2", PrimitiveType.DOUBLE);
		VariableRead firstRead = new VariableRead(coordinate, "var1");
		Expression one = new Num(coordinate, "1.0");

		Expression addition =
			new BinaryOperation(
				coordinate,
				Operator.ADD,
				firstRead, one);

		VariableWrite secondWrite = new VariableWrite(coordinate, "var2", addition);

		Array<Statement> statements =
			new Array<>(
				firstDeclaration,
				firstWrite,
				secondDeclaration,
				secondWrite);

		Block block = new Block(coordinate, statements);
		LocalVariables variables = new LocalVariables(new HashMap<>());

		Result result = block.compile(variables, caller);

		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(4, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCONST_0, firstInstruction.getOpcode());

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
		assertFalse(block.getFieldAssignments().contains(""));
	}

	@Test
	public void testDoNotCommuteReadAndWrite()
	{
		Signature caller = new Signature(new NullType(), "method", new Array<>());
		Coordinate coordinate = new Coordinate(4, 0);

		VariableDeclaration variableDeclaration =
				new VariableDeclaration("myVariable", PrimitiveType.DOUBLE);
		VariableDeclaration notherVariableDeclaration =
			new VariableDeclaration("notherVariable", PrimitiveType.DOUBLE);

		Expression one = new Num(coordinate, "1.0");
		VariableWrite variableWrite = new VariableWrite(coordinate, "myVariable", one);
		VariableRead variableRead = new VariableRead(coordinate, "myVariable");

		VariableWrite notherVariableWrite =
			new VariableWrite(coordinate, "notherVariable", variableRead);

		VariableRead notherVariableRead = new VariableRead(coordinate, "notherVariable");

		Return returnStatement =
			new Return(coordinate, PrimitiveType.DOUBLE, notherVariableRead);

		Array<Statement> statements =
			new Array<>(
				variableDeclaration,
				notherVariableDeclaration,
				variableWrite,
				notherVariableWrite,
				variableWrite,
				returnStatement);

		Block block = new Block(coordinate, statements);
		LocalVariables variables = new LocalVariables(new HashMap<>());

		Result result = block.compile(variables, caller);

		assertEquals(0, result.getErrors().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		assertEquals(6, instructions.length());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCONST_1, firstInstruction.getOpcode());

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.DSTORE, secondInstruction.getOpcode());
		assertEquals(3, secondInstruction.var);

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DCONST_1, thirdInstruction.getOpcode());

		VarInsnNode fourthInstruction = (VarInsnNode) instructions.get(3);

		assertEquals(Opcodes.DSTORE, fourthInstruction.getOpcode());
		assertEquals(1, fourthInstruction.var);

		VarInsnNode fifthInstruction = (VarInsnNode) instructions.get(4);

		assertEquals(Opcodes.DLOAD, fifthInstruction.getOpcode());
		assertEquals(3, fifthInstruction.var);

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.DRETURN, sixthInstruction.getOpcode());
	}

	@Test
	public void testDelegatedInterfacesFromLastDelegation() throws Exception
	{
		Coordinate coordinate = new Coordinate(2, 6);
		Array<Expression> emptyString = new Array<>(new Streeng(coordinate, "\"\""));
		Delegate stringDelegate = new Delegate(coordinate, "MyClass", emptyString);
		Statement first = new FunctionCall(stringDelegate);

		Type dub = new ReferenceType(Double.class);

		Delegate doubleDelegate =
			new Delegate(coordinate, "MyClass", new Array<>(new This(coordinate, dub)));

		Statement second = new FunctionCall(doubleDelegate);
		Block block = new Block(coordinate, new Array<>(first, second));

		Type string = new ReferenceType(String.class);
		Map<Delegate, Type> delegatedTypes = new HashMap<>();
		delegatedTypes.put(stringDelegate, new ReferenceType(String.class));
		delegatedTypes.put(doubleDelegate, new ReferenceType(Double.class));

		Implementation delegatedInterfaces =
			block.getDelegatedInterfaces(delegatedTypes, string);

		assertTrue(delegatedInterfaces instanceof InterfaceImplementation);
	}

	@Test
	public void testDelegatedInterfacesFromDelegation() throws Exception
	{
		Coordinate coordinate = new Coordinate(2, 6);
		Array<Expression> emptyString = new Array<>(new Streeng(coordinate, "\"\""));
		Delegate stringDelegate = new Delegate(coordinate, "MyClass", emptyString);
		Statement first = new FunctionCall(stringDelegate);

		Statement second = new Block(coordinate, new Array<>());

		Block block = new Block(coordinate, new Array<>(first, second));

		Type string = new ReferenceType(String.class);
		Map<Delegate, Type> delegatedTypes = new HashMap<>();
		delegatedTypes.put(stringDelegate, string);

		Implementation delegatedInterfaces =
			block.getDelegatedInterfaces(delegatedTypes, string);

		assertTrue(delegatedInterfaces instanceof InterfaceImplementation);
	}
}
