package yirgacheffe.compiler;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import yirgacheffe.parser.YirgacheffeBaseListener;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.ArrayList;
import java.util.List;

public class YirgacheffeListener extends YirgacheffeBaseListener
{
	private ClassWriter writer;

	private ParseErrorListener errorListener;

	private List<Error> errors = new ArrayList<>();

	public YirgacheffeListener(
		ClassWriter writer,
		ParseErrorListener errorListener)
	{
		this.writer = writer;
		this.errorListener = errorListener;
	}

	@Override
	public void enterMalformedDeclaration(
		YirgacheffeParser.MalformedDeclarationContext context)
	{
		this.errors.add(
			new Error(context, "Expected declaration of class or interface."));
	}

	@Override
	public void enterClassDeclaration(
		YirgacheffeParser.ClassDeclarationContext context)
	{
		if (context.Identifier() == null)
		{
			this.errors.add(new Error(context, "Class identifier expected."));
		}
		else
		{
			String className = context.Identifier().getText();

			this.writer.visit(
				Opcodes.V1_8,
				Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
				className,
				null,
				"java/lang/Object",
				null);
		}
	}

	@Override
	public void enterInterfaceDeclaration(
		YirgacheffeParser.InterfaceDeclarationContext context)
	{
		if (context.Identifier() == null)
		{
			this.errors.add(new Error(context, "Interface identifier expected."));
		}
		else
		{
			String className = context.Identifier().getText();

			this.writer.visit(
				Opcodes.V1_8,
				Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE,
				className,
				null,
				"java/lang/Object",
				null);
		}
	}

	@Override
	public void enterFieldDeclaration(YirgacheffeParser.FieldDeclarationContext context)
	{
		if (context.Type() == null)
		{
			Error error =
				new Error(context, "Field declaration should start with type.");

			this.errors.add(error);
		}
		else
		{
			Type type = Type.parse(context.Type().getSymbol().getText());
			String identifier = context.Identifier().getSymbol().getText();

			this.writer
				.visitField(
					Opcodes.ACC_PRIVATE,
					identifier,
					type.getJVMType(),
					null,
					null);
		}
	}

	@Override
	public void enterInterfaceFieldDeclaration(
		YirgacheffeParser.InterfaceFieldDeclarationContext context)
	{
		this.errors.add(new Error(context, "Interface cannot contain field."));
	}

	@Override
	public void enterMethodDeclaration(
		YirgacheffeParser.MethodDeclarationContext context)
	{
		if (context.Modifier() == null)
		{
			MethodDescriptor descriptor =
				new MethodDescriptor(context.parameter(), context.Type());

			this.writer.visitMethod(
				Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT,
				context.Identifier().getText(),
				descriptor.toString(),
				null,
				null);
		}
		else
		{
			Error error =
				new Error(
					context,
					"Access modifier is not required for interface method declaration.");

			this.errors.add(error);
		}
	}

	@Override
	public void enterParameter(YirgacheffeParser.ParameterContext context)
	{
		if (context.Type() == null)
		{
			Error error =
				new Error(context, "Expected type before argument identifier");

			this.errors.add(error);
		}
	}

	public CompilationResult getCompilationResult()
	{
		if (this.errorListener.hasError())
		{
			return new CompilationResult(this.errorListener.getErrors());
		}
		else if (this.errors.size() > 0)
		{
			return new CompilationResult(this.errors);
		}
		else
		{
			return new CompilationResult(this.writer.toByteArray());
		}
	}
}
