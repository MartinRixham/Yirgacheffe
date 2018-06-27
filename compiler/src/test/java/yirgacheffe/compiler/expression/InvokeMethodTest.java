package yirgacheffe.compiler.expression;

import org.junit.Test;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.function.Callable;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.type.ParameterisedType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class InvokeMethodTest
{
	@Test
	public void testCompilingInvocationWithGenericReturnType() throws Exception
	{
		MethodNode methodVisitor = new MethodNode();
		List<Type> typeParameters =
			Arrays.asList(PrimitiveType.DOUBLE, PrimitiveType.DOUBLE);
		Type owner = new ParameterisedType(new ReferenceType(Map.class), typeParameters);
		Callable function =
			new Function(owner, Map.class.getMethod("get", Object.class));
		Callable constructor = new Function(owner, HashMap.class.getConstructor());
		Expression expression = new InvokeConstructor(constructor, new Expression[0]);
		Expression[] arguments = new Expression[] {new Literal(1)};

		InvokeMethod invokeMethod = new InvokeMethod(function, expression, arguments);

		invokeMethod.compile(methodVisitor);

		InsnList instructions = methodVisitor.instructions;

		assertEquals(6, instructions.size());
	}
}
