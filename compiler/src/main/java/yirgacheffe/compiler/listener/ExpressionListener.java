package yirgacheffe.compiler.listener;

import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variable;
import yirgacheffe.parser.YirgacheffeParser;

public class ExpressionListener extends StatementListener
{
	public ExpressionListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void enterVariableRead(YirgacheffeParser.VariableReadContext context)
	{
		if (this.localVariables.containsKey(context.getText()))
		{
			Variable variable = this.localVariables.get(context.getText());
			Type type = variable.getType();
			int index = variable.getIndex();

			this.methodVisitor.visitVarInsn(type.getLoadInstruction(), index);

			this.typeStack.push(type);
		}
		else
		{
			String message = "Unknown local variable '" + context.getText() + "'.";

			this.errors.add(new Error(context, message));

			this.typeStack.push(new NullType());
		}
	}

	@Override
	public void enterThisRead(YirgacheffeParser.ThisReadContext context)
	{
		try
		{
			String fullyQualifiedType =
				this.packageName == null ?
					this.className :
					this.packageName + "." + this.className;

			Type thisType = this.classes.loadClass(fullyQualifiedType);

			this.typeStack.push(thisType);
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}

		this.methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
	}

	@Override
	public void enterLiteral(YirgacheffeParser.LiteralContext context)
	{
		Object value;

		if (context.StringLiteral() != null)
		{
			value = context.getText().replace("\"", "");

			this.typeStack.push(new ReferenceType(String.class));
		}
		else if (context.CharacterLiteral() != null)
		{
			value = context.getText().charAt(1);

			this.typeStack.push(PrimitiveType.CHAR);
		}
		else if (context.BooleanLiteral() != null)
		{
			value = context.getText().equals("true");

			this.typeStack.push(PrimitiveType.BOOLEAN);
		}
		else
		{
			value = new Double(context.getText());

			this.typeStack.push(PrimitiveType.DOUBLE);
		}

		this.methodVisitor.visitLdcInsn(value);
	}
}
