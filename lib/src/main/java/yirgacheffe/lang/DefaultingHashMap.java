package yirgacheffe.lang;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DefaultingHashMap<K, V> implements Map<K, V>
{
	private Map<K, V> map;

	private V defaultValue;

	public DefaultingHashMap(V defaultValue)
	{
		this.map = new HashMap<>();
		this.defaultValue = defaultValue;
	}

	public DefaultingHashMap(Map<K, V> map, V defaultValue)
	{
		this.map = new HashMap<>(map);
		this.defaultValue = defaultValue;
	}

	public DefaultingHashMap(DefaultingHashMap<K, V> defaultingHashMap)
	{
		this.map = new HashMap<>(defaultingHashMap.map);
		this.defaultValue = defaultingHashMap.defaultValue;
	}

	public int size()
	{
		return this.map.size();
	}

	public boolean isEmpty()
	{
		return this.map.isEmpty();
	}

	public boolean containsKey(Object o)
	{
		return this.map.containsKey(o);
	}

	public boolean containsValue(Object o)
	{
		return this.map.containsValue(o);
	}

	public V get(Object o)
	{
		V value = this.map.get(o);

		if (value == null)
		{
			return this.defaultValue;
		}

		return value;
	}

	public V put(K k, V v)
	{
		V value = this.map.put(k, v);

		if (value == null)
		{
			return this.defaultValue;
		}

		return value;
	}

	public V remove(Object o)
	{
		return this.map.remove(o);
	}

	public void putAll(Map<? extends K, ? extends V> map)
	{
		this.map.putAll(map);
	}

	public void clear()
	{
		this.map.clear();
	}

	public Set<K> keySet()
	{
		return this.map.keySet();
	}

	public Collection<V> values()
	{
		return this.map.values();
	}

	public Set<Entry<K, V>> entrySet()
	{
		return this.map.entrySet();
	}
}
