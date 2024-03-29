package yirgacheffe.compiler.listener;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.function.FunctionSignature;
import yirgacheffe.compiler.member.Interface;
import yirgacheffe.compiler.function.TypeParameters;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.implementation.NullImplementation;
import yirgacheffe.compiler.member.Property;
import yirgacheffe.compiler.statement.Statement;
import yirgacheffe.compiler.type.BoundedType;
import yirgacheffe.compiler.type.ClassSignature;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.ParameterisedType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.VariableType;
import yirgacheffe.compiler.variables.LocalVariables;
import yirgacheffe.compiler.generated.DefaultConstructor;
import yirgacheffe.compiler.generated.DelegationMethod;
import yirgacheffe.compiler.generated.EnumerationInitialiser;
import yirgacheffe.compiler.generated.MainMethod;
import yirgacheffe.lang.Array;
import yirgacheffe.lang.Enumeration;
import yirgacheffe.lang.EnumerationWithDefault;
import yirgacheffe.parser.YirgacheffeParser;

import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClassListener extends PackageListener
{
	protected boolean hasConstructor = false;

	protected boolean hasDefaultConstructor = false;

	protected String mainMethodName;

	protected Array<Function> interfaceMethods = new Array<>();

	protected Implementation delegatedInterfaces = new NullImplementation();

	protected Array<Type> interfaces = new Array<>();

	protected Map<String, BoundedType> typeParameters = new HashMap<>();

	protected Type thisType = new NullType();

	private Type constantType;

	protected boolean hasDelegate = false;

	protected boolean inEnumeration = false;

	protected boolean inInterface = false;

	protected Array<Statement> staticStatements = new Array<>();

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

			this.thisType = getClassType();
		}
	}

	public Type getClassType()
	{
		try
		{
			ReferenceType thisType =
				this.classes.loadClass(this.className.replace("/", "."));

			Interface members = thisType.reflect();
			Array<TypeVariable<?>> parameters = members.getTypeParameters();

			return new TypeParameters(parameters, thisType).getType();
		}
		catch (ClassNotFoundException | NoClassDefFoundError e)
		{
			return new NullType();
		}
	}

	@Override
	public void exitClassDeclaration(YirgacheffeParser.ClassDeclarationContext context)
	{
		this.inInterface = false;

		if (context.Class() == null)
		{
			String message = "Expected declaration of class or interface.";

			this.errors.push(new Error(context, message));
		}
	}

	@Override
	public void exitInterfaceDeclaration(
		YirgacheffeParser.InterfaceDeclarationContext context)
	{
		this.inInterface = true;

		if (context.Identifier() == null)
		{
			String message = "Interface identifier expected.";

			this.errors.push(new Error(context, message));
		}
		else
		{
			this.className = this.directory + context.Identifier().getText();
			this.thisType = this.getClassType();
		}
	}

	@Override
	public void exitEnumeration(YirgacheffeParser.EnumerationContext context)
	{
		this.inEnumeration = true;

		constantType = this.types.getType(context.type());

		if (!constantType.isPrimitive() &&
			!constantType.isAssignableTo(new ReferenceType(String.class)))
		{
			String message = "Cannot enumerate type " + constantType + ".";

			this.errors.push(new Error(context.type(), message));
		}
	}

	@Override
	public void enterGenericTypes(YirgacheffeParser.GenericTypesContext context)
	{
		if (context != null)
		{
			for (YirgacheffeParser.GenericTypeContext type: context.genericType())
			{
				TerminalNode identifier = type.Identifier();

				if (identifier != null)
				{
					String name = identifier.getText();

					this.types.put(name, new VariableType(name));
				}
			}
		}
	}

	@Override
	public void exitGenericTypes(YirgacheffeParser.GenericTypesContext context)
	{
		Map<String, BoundedType> parameters = new HashMap<>();

		if (context != null)
		{
			for (YirgacheffeParser.GenericTypeContext type: context.genericType())
			{
				TerminalNode identifier = type.Identifier();

				if (identifier != null)
				{
					String name = identifier.getText();
					Type typeBound;

					if (type.type() == null)
					{
						typeBound = new ReferenceType(Object.class);
					}
					else
					{
						typeBound = this.types.getType(type.type());
					}

					parameters.put(name, new BoundedType(name, typeBound));
				}
			}
		}

		this.typeParameters = parameters;
	}

	@Override
	public void exitClassDefinition(YirgacheffeParser.ClassDefinitionContext context)
	{
		if (this.inEnumeration)
		{
			this.createEnumeration();
		}

		String[] interfaces = new String[this.interfaces.length()];

		for (int i = 0; i < this.interfaces.length(); i++)
		{
			interfaces[i] =
				this.interfaces.get(i).toFullyQualifiedType();
		}

		ClassSignature signature =
			new ClassSignature(
				this.interfaces, new Array<>(this.typeParameters.values()));

		this.classNode.visit(
			Opcodes.V1_8,
			Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_SUPER,
			this.className,
			signature.toString(),
			"java/lang/Object",
			interfaces);

		for (YirgacheffeParser.InterfaceMethodDeclarationContext interfaceMethod:
			context.interfaceMethodDeclaration())
		{
			String message = "Method requires method body.";

			this.errors.push(new Error(interfaceMethod, message));
		}

		if (this.mainMethodName != null)
		{
			this.classNode.methods.add(
				new MainMethod(this.className, this.mainMethodName).generate());
		}

		if (!this.hasConstructor && this.mainMethodName == null)
		{
			String message = "Class has no constructor.";

			this.errors.push(new Error(context, message));
		}

		if (!this.hasDefaultConstructor && this.mainMethodName != null)
		{
			this.checkFieldInitialisation(context);

			this.classNode.methods.add(
				new DefaultConstructor(this.className, this.thisType).generate());
		}

		this.checkInterfaceMethodImplementations(context);
	}

	@Override
	public void exitInterfaceDefinition(
		YirgacheffeParser.InterfaceDefinitionContext context)
	{
		if (this.inEnumeration)
		{
			this.createEnumeration();
		}

		String[] interfaces = new String[this.interfaces.length()];

		for (int i = 0; i < this.interfaces.length(); i++)
		{
			interfaces[i] =
				this.interfaces.get(i).toFullyQualifiedType();
		}

		ClassSignature signature =
			new ClassSignature(
				this.interfaces, new Array<>(this.typeParameters.values()));

		this.classNode.visit(
			Opcodes.V1_8,
			Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE,
			this.className,
			signature.toString(),
			"java/lang/Object",
			interfaces);

		if (context.field().size() > 0)
		{
			String message = "Interface cannot contain field.";

			this.errors.push(new Error(context.field(0), message));
		}
	}

	private void createEnumeration()
	{
		ReferenceType enumType = this.hasDefaultConstructor ?
			new ReferenceType(EnumerationWithDefault.class) :
			new ReferenceType(Enumeration.class);

		this.interfaces.push(new ParameterisedType(enumType, new Array<>(constantType)));

		this.classNode.fields.add(
			new FieldNode(
				Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
				"values",
				"Ljava/util/Map;",
				null,
				null));

		MethodNode method = new EnumerationInitialiser(this.thisType).generate();

		Result result = new Result();
		Signature signature = new FunctionSignature(new NullType(), "", new Array<>());
		LocalVariables variables =
			new LocalVariables(1, new HashMap<>(), new HashMap<>());

		for (Statement statement: this.staticStatements)
		{
			result = result.concat(statement.compile(variables, signature));
		}

		this.errors.push(result.getErrors());
		this.errors.push(variables.getErrors());

		for (AbstractInsnNode instruction: result.getInstructions())
		{
			method.instructions.add(instruction);
		}

		method.instructions.add(new InsnNode(Opcodes.RETURN));

		this.classNode.methods.add(method);
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
				this.classNode.methods.add(
					new DelegationMethod(this.className, method).generate());
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
		Interface members = this.thisType.reflect();
		Set<Function> methods = members.getMethods();

		Set<String> fieldNames =
			this.getFieldNames(members.getFields());

		for (Function method: methods)
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

	private Set<String> getFieldNames(Set<Property> fields)
	{
		Set<String> names = new HashSet<>();

		for (Property field: fields)
		{
			names.add(field.getName());
		}

		return names;
	}
}
