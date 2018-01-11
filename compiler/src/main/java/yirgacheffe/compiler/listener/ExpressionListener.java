package yirgacheffe.compiler.listener;

import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.Type;
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
			this.typeStack.push(this.localVariables.get(context.getText()).getType());
		}

		this.methodVisitor.visitVarInsn(Opcodes.DLOAD, 1);
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
}
