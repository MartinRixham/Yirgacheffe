package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;

public class CharTest
{
	@Test
	public void testCompilingCharacter()
	{
		MethodNode methodVisitor = new MethodNode();
		Variables variables = new Variables();

		Char literal = new Char("'r'");

		Type type = literal.getType(variables);

		Array<Error> errors = literal.compile(methodVisitor, variables);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(0, literal.getVariableReads().length());
		assertEquals(0, errors.length());
		assertEquals(1, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals('r', firstInstruction.cst);

		assertEquals("java.lang.Character", type.toFullyQualifiedType());
	}
}
