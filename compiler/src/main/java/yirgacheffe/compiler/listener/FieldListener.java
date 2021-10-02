package yirgacheffe.compiler.listener;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.FieldRead;
import yirgacheffe.compiler.expression.InvokeConstructor;
import yirgacheffe.compiler.expression.InvokeInterfaceConstructor;
import yirgacheffe.compiler.expression.Literal;
import yirgacheffe.compiler.expression.This;
import yirgacheffe.compiler.statement.FieldWrite;
import yirgacheffe.compiler.statement.StoreConstant;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;
import yirgacheffe.parser.YirgacheffeParser;

public class FieldListener extends ConstructorListener
{
	protected Array<Expression> arguments;

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
		Variables variables = new LocalVariables(1, this.constants, this.numberTypes);
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
		Coordinate coordinate = new Coordinate(context);
		Literal literal = Literal.parse(coordinate, context.literal().getText());

		Result result = new Result()
			.add(new FieldInsnNode(
				Opcodes.GETSTATIC,
				this.className,
				"values",
				"Ljava/util/Map;"))
			.add(new LdcInsnNode(literal.getValue()))
			.concat(literal.getType().convertTo(new ReferenceType(Object.class)))
			.add(new MethodInsnNode(
				Opcodes.INVOKEINTERFACE,
				"java/util/Map",
				"get",
				"(Ljava/lang/Object;)Ljava/lang/Object;",
				true))
			.concat(new ReferenceType(Object.class).convertTo(this.thisType))
			.add(new InsnNode(Opcodes.ARETURN));

		for (AbstractInsnNode instruction: result.getInstructions())
		{
			this.methodNode.instructions.add(instruction);
		}

		Expression invokeConstructor = this.getConstructorCall(coordinate);

		this.staticStatements.push(
			new StoreConstant(this.thisType, literal, invokeConstructor));
	}

	private Expression getConstructorCall(Coordinate coordinate)
	{
		if (this.inInterface)
		{
			return
				new InvokeInterfaceConstructor(coordinate, this.thisType, this.arguments);
		}
		else
		{
			return new InvokeConstructor(coordinate, this.thisType, this.arguments);
		}
	}
}
