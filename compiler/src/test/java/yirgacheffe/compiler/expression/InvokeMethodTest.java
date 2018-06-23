package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.type.GenericType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;

import static org.junit.Assert.assertEquals;

public class InvokeMethodTest
{
	@Test
	public void testCompilingInvokationWithGenericReturnType()
	{
		MethodNode methodVisitor = new MethodNode();
		Type owner = new ReferenceType(java.util.Map.class);
		String name = "get";
		String descriptor = "(Ljava/lang/String;)Ljava/lang/String;";
		Type stringType = new ReferenceType(java.lang.String.class);
		Type returnType = new GenericType(stringType);

		InvokeMethod invokeMethod =
			new InvokeMethod(
				owner,
				name,
				descriptor,
				returnType);

		invokeMethod.compile(methodVisitor);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(2, instructions.size());
	}
}
