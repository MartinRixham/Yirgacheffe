package yirgacheffe.compiler;

import org.antlr.v4.runtime.Token;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import yirgacheffe.parser.YirgacheffeBaseListener;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.ArrayList;
import java.util.List;

public class YirgacheffeListener extends YirgacheffeBaseListener
{
	private ClassWriter writer;

	private List<String> errors = new ArrayList<>();

	public YirgacheffeListener(ClassWriter writer)
	{
		this.writer = writer;
	}

	@Override
	public void enterTypeDeclaration(
		YirgacheffeParser.TypeDeclarationContext context)
	{
		if (context.classDeclaration() == null &&
			context.interfaceDeclaration() == null)
		{
			Token start = context.getStart();
			int line = start.getLine();
			int index = start.getTokenIndex();

			this.errors.add(
				"line " + line + ":" + index +
				" Declaration should be of class or interface.");
		}
	}

	@Override
	public void enterClassDeclaration(
		YirgacheffeParser.ClassDeclarationContext context)
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

	@Override
	public void enterInterfaceDeclaration(
		YirgacheffeParser.InterfaceDeclarationContext context)
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

	@Override
	public void enterFieldDeclaration(YirgacheffeParser.FieldDeclarationContext context)
	{
		Type type = Type.parse(context.Type().getSymbol().getText());
		String identifier = context.Identifier().getSymbol().getText();

		this.writer
			.visitField(Opcodes.ACC_PRIVATE, identifier, type.getJVMType(), null, null);
	}

	@Override
	public void enterInterfaceFieldDeclaration(
		YirgacheffeParser.InterfaceFieldDeclarationContext context)
	{
		Token start = context.getStart();
		int line = start.getLine();
		int index = start.getTokenIndex();

		this.errors.add(
			"line " + line + ":" + index +
				" Interface cannot contain field.");
	}

	public CompilationResult getCompilationResult()
	{
		if (this.errors.size() > 0)
		{
			return new CompilationResult(this.errors);
		}
		else
		{
			return new CompilationResult(this.writer.toByteArray());
		}
	}
}
