package yirgacheffe.compiler.listener;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.implementation.NullImplementation;
import yirgacheffe.compiler.type.ClassSignature;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.ParameterisedType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.VariableType;
import yirgacheffe.lang.Array;
import yirgacheffe.lang.Bootstrap;
import yirgacheffe.parser.YirgacheffeParser;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.HashSet;
import java.util.Set;

public class ClassListener extends PackageListener
{
	protected boolean hasConstructor = false;

	protected boolean hasDefaultConstructor = false;

	protected String mainMethodName;

	protected Array<Function> interfaceMethods = new Array<>();

	protected Implementation delegatedInterfaces = new NullImplementation();

	protected Array<Type> interfaces = new Array<>();

	private Array<String> typeParameters = new Array<>();

	protected Type thisType = new NullType();

	protected boolean hasDelegate = false;

	public ClassListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void enterClassDeclaration(YirgacheffeParser.ClassDeclarationContext context)
	{
		if (context.Identifier().size() == 0)
		{
			String message = "Class identifier expected.";

			this.errors.push(new Error(context, message));
		}
		else
		{
			this.className = this.directory + context.Identifier().get(0).getText();

			try
			{
				ReferenceType thisType =
					this.classes.loadClass(this.className.replace("/", "."));

				Class<?> clazz = thisType.reflectionClass();
				TypeVariable[] parameters = clazz.getTypeParameters();

				if (parameters.length == 0)
				{
					this.thisType = thisType;
				}
				else
				{
					Array<Type> parameterTypes = new Array<>();

					for (TypeVariable parameter: parameters)
					{
						parameterTypes.push(new VariableType(parameter.getName()));
					}

					this.thisType = new ParameterisedType(thisType, parameterTypes);
				}
			}
			catch (ClassNotFoundException | NoClassDefFoundError e)
			{
			}
		}
	}

	@Override
	public void exitClassDeclaration(YirgacheffeParser.ClassDeclarationContext context)
	{
		if (context.Class() == null)
		{
			String message = "Expected declaration of class or interface.";

			this.errors.push(new Error(context, message));
		}

		String[] interfaces = new String[this.interfaces.length()];

		for (int i = 0; i < this.interfaces.length(); i++)
		{
			interfaces[i] =
				this.interfaces.get(i).toFullyQualifiedType();
		}

		ClassSignature signature =
			new ClassSignature(this.interfaces, this.typeParameters);

		this.classNode.visit(
			Opcodes.V1_8,
			Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_SUPER,
			this.className,
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
			this.className = this.directory + context.Identifier().getText();
		}

		ClassSignature signature =
			new ClassSignature(this.interfaces, this.typeParameters);

		this.classNode.visit(
			Opcodes.V1_8,
			Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE,
			this.className,
			signature.toString(),
			"java/lang/Object",
			null);
	}

	@Override
	public void exitEnumerationDeclaration(
		YirgacheffeParser.EnumerationDeclarationContext context)
	{
		this.className = this.directory + context.Identifier().getText();

		try
		{
			this.thisType =
				this.classes.loadClass(this.className.replace("/", "."));
		}
		catch (ClassNotFoundException | NoClassDefFoundError e)
		{
		}

		this.classNode.visit(
			Opcodes.V1_8,
			Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_SUPER,
			this.className,
			null,
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
			this.makeDefaultConstructor(context);
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
			this.classNode.visitMethod(
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
	}

	private void makeDefaultConstructor(YirgacheffeParser.ClassDefinitionContext context)
	{
		this.checkFieldInitialisation(context);

		MethodVisitor methodVisitor =
			this.classNode.visitMethod(
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

		Method[] methods = this.thisType.reflectionClass().getDeclaredMethods();

		for (Method method: methods)
		{
			if (method.getName().startsWith("0init_field"))
			{
				methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

				methodVisitor.visitMethodInsn(
					Opcodes.INVOKEVIRTUAL,
					this.className,
					method.getName(),
					"()V",
					false);
			}
		}

		methodVisitor.visitInsn(Opcodes.RETURN);
	}

	private void checkInterfaceMethodImplementations(
		YirgacheffeParser.ClassDefinitionContext context)
	{
		if (this.hasDelegate)
		{
			this.classNode.visitField(
				Opcodes.ACC_PRIVATE,
				"0delegate",
				"Ljava/lang/Object;",
				null,
				null);
		}

		for (Function method : this.interfaceMethods)
		{
			if (this.delegatedInterfaces.implementsMethod(method, this.thisType))
			{
				MethodVisitor methodVisitor =
					this.classNode.visitMethod(
						Opcodes.ACC_PUBLIC,
						method.getName(),
						method.getSignature().getDescriptor(),
						method.getSignature().getSignature(),
						null);

				methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

				methodVisitor.visitFieldInsn(
					Opcodes.GETFIELD,
					this.className,
					"0delegate",
					"Ljava/lang/Object;");

				methodVisitor.visitTypeInsn(
					Opcodes.CHECKCAST,
					method.getOwner().toFullyQualifiedType());

				Array<Type> parameters = method.getParameterTypes();

				for (int i = 0; i < parameters.length(); i++)
				{
					Type parameter = parameters.get(i);

					methodVisitor.visitVarInsn(parameter.getLoadInstruction(), i + 1);
				}

				MethodType methodType =
					MethodType.methodType(
						CallSite.class,
						MethodHandles.Lookup.class,
						String.class,
						MethodType.class);

				Handle bootstrapMethod =
					new Handle(
						Opcodes.H_INVOKESTATIC,
						Bootstrap.class.getName().replace(".", "/"),
						"bootstrapPrivate",
						methodType.toMethodDescriptorString(),
						false);

				String descriptor =
					"(" + method.getOwner().toJVMType() +
						method.getSignature().getDescriptor().substring(1);

				methodVisitor.visitInvokeDynamicInsn(
					method.getName(),
					descriptor,
					bootstrapMethod);

				methodVisitor.visitInsn(method.getReturnType().getReturnInstruction());
			}
			else
			{
				String message =
					"Missing implementation of interface method " +
						method.toString() + ".";

				this.errors.push(new Error(context, message));
			}
		}
	}

	private void checkFieldInitialisation(
		YirgacheffeParser.ClassDefinitionContext context)
	{
		String initialiserPrefix = "0init_field";
		Class<?> reflectionClass = this.thisType.reflectionClass();

		Method[] methods = reflectionClass.getDeclaredMethods();

		Set<String> fieldNames =
			this.getFieldNames(reflectionClass.getDeclaredFields());

		for (Method method: methods)
		{
			if (method.getName().startsWith(initialiserPrefix))
			{
				fieldNames.remove(
					method.getName().substring(initialiserPrefix.length() + 1));
			}
		}

		for (String field: fieldNames)
		{
			String message =
				"Default constructor does not initialise field '" + field + "'.";

			this.errors.push(new Error(context, message));
		}
	}

	private Set<String> getFieldNames(Field[] fields)
	{
		Set<String> names = new HashSet<>();

		for (Field field: fields)
		{
			names.add(field.getName());
		}

		return names;
	}
}
