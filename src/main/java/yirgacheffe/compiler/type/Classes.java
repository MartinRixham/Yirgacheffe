package yirgacheffe.compiler.type;

public class Classes
{
	private BytecodeClassLoader classLoaderIn = new BytecodeClassLoader();

	private BytecodeClassLoader classLoaderOut = new BytecodeClassLoader();

	public void addClass(String name, byte[] bytes)
	{
		this.classLoaderOut.add(name, bytes);
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException
	{
		return this.classLoaderIn.loadClass(name);
	}

	public void clearCache()
	{
		this.classLoaderIn = this.classLoaderOut;
		this.classLoaderOut = new BytecodeClassLoader();
	}
}
