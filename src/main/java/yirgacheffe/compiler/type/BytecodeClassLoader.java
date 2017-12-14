package yirgacheffe.compiler.type;

import java.util.HashMap;
import java.util.Map;

public class BytecodeClassLoader extends ClassLoader
{
	private Map<String, byte[]> bytecode = new HashMap<>();

	public void addClass(String name, byte[] bytes)
	{
		this.bytecode.put(name, bytes);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException
	{
		byte[] bytes = this.bytecode.get(name);

		if (bytes == null)
		{
			throw new ClassNotFoundException();
		}

		return this.defineClass(name, bytes, 0, bytes.length);
	}
}
