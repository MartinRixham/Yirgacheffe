package yirgacheffe.compiler.listener;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.Type.BytecodeClassLoader;
import yirgacheffe.compiler.Type.DeclaredType;
import yirgacheffe.compiler.Type.Types;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.error.ParseErrorListener;
import yirgacheffe.parser.YirgacheffeParser;

public class ClassListener extends YirgacheffeListener
{
	protected boolean hasDefaultConstructor = true;

	public ClassListener(
		String directory,
		Types types,
		BytecodeClassLoader classLoader,
		ParseErrorListener errorListener,
		ClassWriter writer)
	{
		super(directory, types, classLoader, errorListener, writer);
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
	public void enterMalformedDeclaration(
		YirgacheffeParser.MalformedDeclarationContext context)
	{
		this.errors.add(
			new Error(context, "Expected declaration of class or interface."));
	}

	@Override
	public void enterClassDeclaration(YirgacheffeParser.ClassDeclarationContext context)
	{
		if (context.classIdentifier() == null)
		{
			this.errors.add(new Error(context, "Class identifier expected."));
		}
	}

	@Override
	public void enterClassIdentifier(
		YirgacheffeParser.ClassIdentifierContext context)
	{
		this.className = context.getText();

		this.writer.visit(
			Opcodes.V1_8,
			Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
			this.directory + context.getText(),
			null,
			"java/lang/Object",
			null);
	}

	@Override
	public void enterInterfaceDeclaration(
		YirgacheffeParser.InterfaceDeclarationContext context)
	{
		if (context.interfaceIdentifier() == null)
		{
			this.errors.add(new Error(context, "Interface identifier expected."));
		}

		if (context.field().size() > 0)
		{
			String message = "Interface cannot contain field.";

			this.errors.add(new Error(context.field(0), message));
		}
	}

	@Override
	public void enterInterfaceIdentifier(
		YirgacheffeParser.InterfaceIdentifierContext context)
	{
		this.className = context.getText();

		this.writer.visit(
			Opcodes.V1_8,
			Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE,
			this.directory + context.getText(),
			null,
			"java/lang/Object",
			null);
	}

	@Override
	public void exitClassDeclaration(YirgacheffeParser.ClassDeclarationContext context)
	{
		if (this.hasDefaultConstructor)
		{
			MethodVisitor methodVisitor =
				this.writer.visitMethod(
					Opcodes.ACC_PUBLIC,
					"<init>",
					"()V",
					null,
					null);

			methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

			methodVisitor.visitMethodInsn(
				Opcodes.INVOKESPECIAL,
				"java/lang/Object",
				"<init>",
				"()V",
				false);

			methodVisitor.visitInsn(Opcodes.RETURN);
			methodVisitor.visitMaxs(1, 1);
		}
	}

	@Override
	public void exitCompilationUnit(YirgacheffeParser.CompilationUnitContext context)
	{
		DeclaredType type = new DeclaredType(this.packageName, this.className);

		this.types.putDeclaredType(this.className, type);
	}
}
