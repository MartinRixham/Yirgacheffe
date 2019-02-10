package yirgacheffe.compiler.listener;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.parser.YirgacheffeParser;

public class ClassListener extends PackageListener
{
	protected boolean hasDefaultConstructor = true;

	protected String mainMethodName;

	protected int initialiserCount = 0;

	public ClassListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void enterClassDeclaration(YirgacheffeParser.ClassDeclarationContext context)
	{
		if (context.Class() == null)
		{
			String message = "Expected declaration of class or interface.";

			this.errors.push(new Error(context, message));
		}

		if (context.Identifier().size() == 0)
		{
			String message = "Class identifier expected.";

			this.errors.push(new Error(context, message));
		}
		else
		{
			this.className = context.Identifier().get(0).getText();
		}

		for (YirgacheffeParser.InterfaceMethodDeclarationContext interfaceMethod:
			context.interfaceMethodDeclaration())
		{
			String message = "Method requires method body.";

			this.errors.push(new Error(interfaceMethod, message));
		}

		this.writer.visit(
			Opcodes.V1_8,
			Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_SUPER,
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
			String message = "Interface identifier expected.";

			this.errors.push(new Error(context, message));
		}
		else
		{
			this.className = context.Identifier().getText();
		}

		if (context.field().size() > 0)
		{
			String message = "Interface cannot contain field.";

			this.errors.push(new Error(context.field(0), message));
		}

		for (YirgacheffeParser.FunctionContext interfaceMethod: context.function())
		{
			String message = "Method body not permitted for interface method.";

			this.errors.push(new Error(interfaceMethod, message));
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
		else if (this.mainMethodName != null)
		{
			String message = "Main class must have default constructor.";

			this.errors.push(new Error(context, message));
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

		methodVisitor.visitTypeInsn(Opcodes.NEW, "yirgacheffe/lang/Array");
		methodVisitor.visitInsn(Opcodes.DUP);
		methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

		methodVisitor.visitMethodInsn(
			Opcodes.INVOKESPECIAL,
			"yirgacheffe/lang/Array",
			"<init>",
			"([Ljava/lang/Object;)V",
			false);

		methodVisitor.visitMethodInsn(
			Opcodes.INVOKEVIRTUAL,
			this.className,
			this.mainMethodName,
			"(Lyirgacheffe/lang/Array;)V",
			false);

		methodVisitor.visitInsn(Opcodes.RETURN);
		methodVisitor.visitMaxs(0, 0);
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

		for (int i = 0; i < this.initialiserCount; i++)
		{
			methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

			methodVisitor.visitMethodInsn(
				Opcodes.INVOKEVIRTUAL,
				this.className,
				i + "_init_field",
				"()V",
				false);
		}

		methodVisitor.visitInsn(Opcodes.RETURN);
		methodVisitor.visitMaxs(0, 0);
	}
}
