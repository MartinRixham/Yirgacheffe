package yirgacheffe.compiler.listener;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.type.ClassSignature;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.VariableType;
import yirgacheffe.lang.Array;
import yirgacheffe.parser.YirgacheffeParser;

public class ClassListener extends PackageListener
{
	protected boolean hasConstructor = false;

	protected boolean hasDefaultConstructor = false;

	protected String mainMethodName;

	protected Array<String> initialisers = new Array<>();

	protected Array<Function> interfaceMethods = new Array<>();

	protected Array<Type> interfaces = new Array<>();

	private Array<String> typeParameters = new Array<>();

	public ClassListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void exitClassDeclaration(YirgacheffeParser.ClassDeclarationContext context)
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

		String[] interfaces = new String[this.interfaces.length()];

		for (int i = 0; i < this.interfaces.length(); i++)
		{
			interfaces[i] =
				this.interfaces.get(i).toFullyQualifiedType();
		}

		ClassSignature signature =
			new ClassSignature(this.interfaces, this.typeParameters);

		this.writer.visit(
			Opcodes.V1_8,
			Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_SUPER,
			this.directory + this.className,
			signature.toString(),
			"java/lang/Object",
			interfaces);
	}

	@Override
	public void exitInterfaceDeclaration(
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

		ClassSignature signature =
			new ClassSignature(this.interfaces, this.typeParameters);

		this.writer.visit(
			Opcodes.V1_8,
			Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE,
			this.directory + this.className,
			signature.toString(),
			"java/lang/Object",
			null);
	}

	@Override
	public void exitGenericTypes(YirgacheffeParser.GenericTypesContext context)
	{
		Array<String> parameters = new Array<>();

		if (context != null)
		{
			for (TerminalNode type: context.Identifier())
			{
				String name = type.getText();

				parameters.push(name);
				this.types.put(name, new VariableType(name));
			}
		}

		this.typeParameters = parameters;
	}

	@Override
	public void exitClassDefinition(YirgacheffeParser.ClassDefinitionContext context)
	{
		for (YirgacheffeParser.InterfaceMethodDeclarationContext interfaceMethod:
			context.interfaceMethodDeclaration())
		{
			String message = "Method requires method body.";

			this.errors.push(new Error(interfaceMethod, message));
		}

		if (this.mainMethodName != null)
		{
			this.makeMainMethod();
		}

		if (!this.hasConstructor && this.mainMethodName == null)
		{
			String message = "Class has no constructor.";

			this.errors.push(new Error(context, message));
		}

		if (!this.hasDefaultConstructor && this.mainMethodName != null)
		{
			this.makeDefaultConstructor();
		}

		this.checkInterfaceMethodImplementations(context);
	}

	@Override
	public void exitInterfaceDefinition(
		YirgacheffeParser.InterfaceDefinitionContext context)
	{
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

		for (String initialiser: this.initialisers)
		{
			methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

			methodVisitor.visitMethodInsn(
				Opcodes.INVOKEVIRTUAL,
				this.className,
				"0" + initialiser + "_init_field",
				"()V",
				false);
		}

		methodVisitor.visitInsn(Opcodes.RETURN);
		methodVisitor.visitMaxs(0, 0);
	}

	private void checkInterfaceMethodImplementations(
		YirgacheffeParser.ClassDefinitionContext context)
	{
		for (Function method: this.interfaceMethods)
		{
			String message =
				"Missing implementation of interface method " + method.toString() + ".";

			this.errors.push(new Error(context, message));
		}
	}
}
