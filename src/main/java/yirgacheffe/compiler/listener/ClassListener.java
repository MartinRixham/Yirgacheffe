package yirgacheffe.compiler.listener;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.Type.BytecodeClassLoader;
import yirgacheffe.compiler.Type.DeclaredType;
import yirgacheffe.compiler.Type.ImportedType;
import yirgacheffe.compiler.Type.Type;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.error.ParseErrorListener;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.Map;

public class ClassListener extends YirgacheffeListener
{
	protected boolean hasDefaultConstructor = true;

	protected YirgacheffeParser.FieldDeclarationContext assignment;

	public ClassListener(
		String directory,
		Map<String, DeclaredType> declaredTypes,
		BytecodeClassLoader classLoader,
		ParseErrorListener errorListener,
		ClassWriter writer)
	{
		super(directory, declaredTypes, classLoader, errorListener, writer);
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
				this.directory + this.className,
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
			this.className = context.Identifier().getText();

			this.writer.visit(
				Opcodes.V1_8,
				Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE,
				this.directory + this.className,
				null,
				"java/lang/Object",
				null);
		}
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

			if (this.assignment != null)
			{
				Object value = this.getValue(this.assignment.expression());
				String identifier = this.assignment.Identifier().getText();
				String type = this.getType(this.assignment.type()).toJVMType();

				methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
				methodVisitor.visitLdcInsn(value);
				methodVisitor.visitFieldInsn(
					Opcodes.PUTFIELD,
					this.className,
					identifier,
					type);
			}

			methodVisitor.visitInsn(Opcodes.RETURN);
			methodVisitor.visitMaxs(1, 1);
		}
	}

	private Object getValue(YirgacheffeParser.ExpressionContext expression)
	{
		YirgacheffeParser.LiteralContext literal = expression.literal();

		if (literal.IntegerLiteral() == null)
		{
			return expression.getText().replace("\"", "");
		}
		else
		{
			return new Double(expression.getText());
		}
	}

	@Override
	public void exitCompilationUnit(YirgacheffeParser.CompilationUnitContext context)
	{
		DeclaredType type = new DeclaredType(this.packageName, this.className);

		this.declaredTypes.put(this.className, type);
	}

	protected Type getType(YirgacheffeParser.TypeContext context)
	{
		String typeName = context.getText();
		Type type;

		if (this.importedTypes.containsKey(typeName))
		{
			type = this.importedTypes.get(typeName);
		}
		else if (this.declaredTypes.containsKey(typeName))
		{
			type = this.declaredTypes.get(typeName);
		}
		else
		{
			type = new ImportedType(context);
		}

		return type;
	}
}
