package yirgacheffe.compiler.type;

public class Classes
{
	private BytecodeClassLoader classLoaderIn = new BytecodeClassLoader();

	private BytecodeClassLoader classLoaderOut = new BytecodeClassLoader();

	public void addClass(String name, byte[] bytes)
	{
		this.classLoaderOut.add(name, bytes);
	}

	public ReferenceType loadClass(String name) throws ClassNotFoundException
	{
		return new ReferenceType(this.classLoaderIn.loadClass(name));
	}

	public void clearCache()
	{
		this.classLoaderIn = this.classLoaderOut;
		this.classLoaderOut = new BytecodeClassLoader();
	}
}
