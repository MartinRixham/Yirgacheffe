package yirgacheffe.compiler.function;

import yirgacheffe.compiler.type.Type;

import java.lang.reflect.Executable;
import java.util.HashSet;
import java.util.Set;

public class ClassInterface implements Interface
{
	private Type type;

	private Class<?> clazz;

	public ClassInterface(Type type, Class<?> clazz)
	{
		this.type = type;
		this.clazz = clazz;
	}

	public Set<Function> getConstructors()
	{
		return this.makeFunctions(this.clazz.getDeclaredConstructors());
	}

	public Set<Function> getPublicConstructors()
	{
		return this.makeFunctions(this.clazz.getConstructors());
	}

	public Set<Function> getMethods()
	{
		return this.makeFunctions(this.clazz.getDeclaredMethods());
	}

	public Set<Function> getPublicMethods()
	{
		return this.makeFunctions(this.clazz.getMethods());
	}

	private Set<Function> makeFunctions(Executable[] executables)
	{
		Set<Function> functions = new HashSet<>();

		for (Executable executable: executables)
		{
			functions.add(new ClassFunction(this.type, executable));
		}

		return functions;
	}
}
