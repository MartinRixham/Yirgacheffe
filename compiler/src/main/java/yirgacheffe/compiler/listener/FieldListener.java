package yirgacheffe.compiler.listener;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.FieldRead;
import yirgacheffe.compiler.expression.Literal;
import yirgacheffe.compiler.expression.This;
import yirgacheffe.compiler.statement.FieldWrite;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.parser.YirgacheffeParser;

public class FieldListener extends ConstructorListener
{
	public FieldListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void enterFieldDeclaration(YirgacheffeParser.FieldDeclarationContext context)
	{
		if (context.modifier() != null)
		{
			String message = "Field cannot be declared with access modifier.";

			this.errors.push(new Error(context, message));
		}
	}

	@Override
	public void exitFieldInitialisation(
		YirgacheffeParser.FieldInitialisationContext context)
	{
		YirgacheffeParser.FieldDeclarationContext declaration =
			context.fieldDeclaration();
		Variables variables = new LocalVariables(this.constants);
		Expression expression = this.expressions.pop();
		Type expressionType = expression.getType(variables);
		Type fieldType = this.types.getType(declaration.type());
		String fieldName = declaration.Identifier().getText();

		if (!expressionType.isAssignableTo(fieldType))
		{
			String message =
				"Cannot assign " + expressionType.toString() +
				" to field of type " + fieldType.toString() + ".";

			this.errors.push(new Error(context, message));
		}

		if (declaration.Const() == null)
		{
			Expression self = this.expressions.pop();

			Result result = self.compile(variables)
				.concat(expression.compile(variables)
				.add(new FieldInsnNode(
					Opcodes.PUTFIELD,
					this.className,
					fieldName,
					fieldType.toJVMType()))
				.add(new InsnNode(Opcodes.RETURN)));

			for (AbstractInsnNode instruction: result.getInstructions())
			{
				this.methodNode.instructions.add(instruction);
			}
		}
		else
		{
			if (expression instanceof Literal)
			{
				Object constant = ((Literal) expression).getValue();

				this.constants.put(fieldName, constant);
			}
		}
	}

	@Override
	public void exitField(YirgacheffeParser.FieldContext context)
	{
		if (context.fieldDeclaration() != null &&
			context.fieldDeclaration().Const() != null &&
			context.fieldInitialisation() == null)
		{
			String fieldName = context.fieldDeclaration().Identifier().getText();
			String message = "Missing value of constant '" + fieldName + "'.";

			this.errors.push(new Error(context, message));
		}
	}

	@Override
	public void enterFieldRead(YirgacheffeParser.FieldReadContext context)
	{
		if (!this.inConstructor && !this.inMethod)
		{
			String message =
				"Cannot read unconstructed field '" + context.Identifier() + "'.";

			this.errors.push(new Error(context, message));
		}

		Coordinate coordinate = new Coordinate(context);
		String fieldName = context.Identifier().getText();
		Expression owner = this.expressions.pop();

		Expression fieldRead = new FieldRead(coordinate, owner, fieldName);

		this.expressions.push(fieldRead);
	}

	@Override
	public void exitFieldWrite(YirgacheffeParser.FieldWriteContext context)
	{
		Expression value = this.expressions.pop();
		Expression owner = this.expressions.pop();

		if (!this.inConstructor || value instanceof This)
		{
			String message = "Fields must be assigned in initialisers or constructors.";

			this.errors.push(new Error(context, message));
		}

		Coordinate coordinate = new Coordinate(context);
		String fieldName = context.Identifier().getText();

		this.statements.push(new FieldWrite(coordinate, fieldName, owner, value));
	}

	@Override
	public void exitConstantConstructor(
		YirgacheffeParser.ConstantConstructorContext context)
	{
		Literal literal = Literal.parse(context.literal().getText());

		Result result = new Result()
			.add(new VarInsnNode(Opcodes.ALOAD, 0))
			.add(new FieldInsnNode(
				Opcodes.GETFIELD,
				this.className,
				"0values",
				"Ljava/util/Map;"))
			.add(new LdcInsnNode(literal.getValue()))
			.add(new MethodInsnNode(
				Opcodes.INVOKEVIRTUAL,
				"java/util/Map",
				"get",
				"(" + new ReferenceType(literal.getValue().getClass()).toJVMType() + ")" +
					"L" + this.className + ";"))
			.add(new InsnNode(Opcodes.ARETURN));

		for (AbstractInsnNode instruction: result.getInstructions())
		{
			this.methodNode.instructions.add(instruction);
		}
	}
}
