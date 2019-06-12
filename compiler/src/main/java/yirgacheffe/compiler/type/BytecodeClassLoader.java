package yirgacheffe.compiler.type;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class BytecodeClassLoader extends ClassLoader
{
	private ClassLoader fileLoader;

	{
		File file = new File(".");

		try
		{
			URL url = file.toURI().toURL();

			this.fileLoader =  new URLClassLoader(new URL[] {url});
		}
		catch (MalformedURLException e)
		{
			throw new RuntimeException(e);
		}
	}

	private Map<String, byte[]> bytecode = new HashMap<>();

	public void add(String name, byte[] bytes)
	{
		this.bytecode.put(name, bytes);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException
	{
		byte[] bytes = this.bytecode.get(name);

		if (bytes == null)
		{
			try
			{
				return this.fileLoader.loadClass(name);
			}
			catch (ClassNotFoundException | NoClassDefFoundError e)
			{
				return this.getParent().loadClass(name);
			}
		}
		else
		{
			return this.defineClass(name, bytes, 0, bytes.length);
		}
	}
}
