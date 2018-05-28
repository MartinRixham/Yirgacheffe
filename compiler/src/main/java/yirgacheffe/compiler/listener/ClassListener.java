package yirgacheffe.compiler.listener;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.parser.YirgacheffeParser;

public class ClassListener extends PackageListener
{
	private static final int MAIN_METHOD_STACK_SIZE = 4;

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
		methodVisitor.visitMaxs(MAIN_METHOD_STACK_SIZE, 1);
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
		methodVisitor.visitMaxs(1, 1);
	}
}
