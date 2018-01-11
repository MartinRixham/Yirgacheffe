package yirgacheffe.compiler.listener;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.parser.YirgacheffeParser;

public class ClassListener extends YirgacheffeListener
{
	protected boolean hasDefaultConstructor = true;

	protected String mainMethodName;

	public ClassListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void enterCompilationUnit(YirgacheffeParser.CompilationUnitContext context)
	{
		int classCount =
			context.interfaceDeclaration().size() + context.classDeclaration().size();

		if (classCount > 1)
		{
			String message = "File contains multiple class declarations.";

			this.errors.add(new Error(1, 0, message));
		}
	}

	@Override
	public void enterPackageDeclaration(
		YirgacheffeParser.PackageDeclarationContext context)
	{
		if (context.packageName() == null && this.directory.length() > 0)
		{
			String message =
				"Missing package declaration for file path " + this.directory + ".";

			this.errors.add(new Error(context, message));
		}
		else if (context.packageName() != null)
		{
			this.packageName = context.packageName().getText();
			String packageLocation =
				this.packageName.replace('.', '/') + "/";

			if (!packageLocation.equals(this.directory))
			{
				String message =
					"Package name " + this.packageName +
						" does not correspond to the file path " + this.directory + ".";

				this.errors.add(new Error(context.packageName(), message));
			}
		}
	}

	@Override
	public void enterClassDeclaration(YirgacheffeParser.ClassDeclarationContext context)
	{
		if (context.Class() == null)
		{
			this.errors.add(
				new Error(context, "Expected declaration of class or interface."));
		}

		if (context.Identifier().size() == 0)
		{
			this.errors.add(new Error(context, "Class identifier expected."));
		}
		else
		{
			this.className = context.Identifier().get(0).getText();
		}

		for (YirgacheffeParser.InterfaceMethodDeclarationContext interfaceMethod:
			context.interfaceMethodDeclaration())
		{
			String message = "Method requires method body.";

			this.errors.add(new Error(interfaceMethod, message));
		}

		this.writer.visit(
			Opcodes.V1_8,
			Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
			this.directory + this.className,
			null,
			"java/lang/Object",
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
		}

		if (context.field().size() > 0)
		{
			String message = "Interface cannot contain field.";

			this.errors.add(new Error(context.field(0), message));
		}

		for (YirgacheffeParser.FunctionContext interfaceMethod: context.function())
		{
			String message = "Method body not permitted for interface method.";

			this.errors.add(new Error(interfaceMethod, message));
		}

		this.writer.visit(
			Opcodes.V1_8,
			Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE,
			this.directory + this.className,
			null,
			"java/lang/Object",
			null);
	}

	@Override
	public void exitClassDeclaration(YirgacheffeParser.ClassDeclarationContext context)
	{
		if (this.mainMethodName != null)
		{
			this.makeMainMethod();
		}

		if (this.hasDefaultConstructor)
		{
			this.makeDefaultConstructor();
		}
	}

	private void makeMainMethod()
	{
		MethodVisitor methodVisitor =
			this.writer.visitMethod(
				Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC,
				"main",
				"([Ljava/lang/String;)V",
				null,
				null);

		methodVisitor.visitTypeInsn(Opcodes.NEW, this.className);
		methodVisitor.visitInsn(Opcodes.DUP);

		methodVisitor.visitMethodInsn(
			Opcodes.INVOKESPECIAL,
			this.className,
			"<init>",
			"()V",
			false);

		methodVisitor.visitMethodInsn(
			Opcodes.INVOKEVIRTUAL,
			this.className,
			this.mainMethodName,
			"()V",
			false);

		methodVisitor.visitInsn(Opcodes.RETURN);
		methodVisitor.visitMaxs(2, 1);
	}

	private void makeDefaultConstructor()
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