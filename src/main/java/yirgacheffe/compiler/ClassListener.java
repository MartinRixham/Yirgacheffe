package yirgacheffe.compiler;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.Map;

public class ClassListener extends YirgacheffeListener
{
	private String packageName;

	public ClassListener(
		String directory,
		Map<String, Type> importedTypes,
		ParseErrorListener errorListener,
		ClassWriter writer)
	{
		super(directory, importedTypes, errorListener, writer);
	}

	@Override
	public void enterPackageDeclaration(
		YirgacheffeParser.PackageDeclarationContext context)
	{
		this.packageName = context.packageName().getText();
		String packageLocation =
			this.packageName.replace('.', '/') +  "/";

		if (!packageLocation.equals(this.directory))
		{
			String message =
				"Package name " + this.packageName +
				" does not correspond to the file path " + this.directory + ".";

			this.errors.add(new Error(context.packageName(), message));
		}
	}

	@Override
	public void enterImportStatement(YirgacheffeParser.ImportStatementContext context)
	{
		String identifier = context.fullyQualifiedType().Identifier().getText();
		Type type = new ImportedType(context.fullyQualifiedType());

		this.importedTypes.put(identifier, type);
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
			this.className = context.Identifier().getText();

			this.writer.visit(
				Opcodes.V1_8,
				Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
				this.className,
				null,
				"java/lang/Object",
				null);

			this.makeConstructor();
		}
	}

	private void makeConstructor()
	{
		this.writer.visitMethod(
			Opcodes.ACC_PUBLIC,
			"<init>",
			"()V",
			null,
			null);
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
			this.className = context.Identifier().getText();

			this.writer.visit(
				Opcodes.V1_8,
				Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE,
				this.className,
				null,
				"java/lang/Object",
				null);
		}
	}

	@Override
	public void exitCompilationUnit(YirgacheffeParser.CompilationUnitContext context)
	{
		Type type = new DeclaredType(this.packageName, this.className);

		this.importedTypes.put(this.className, type);
	}
}
