/*
 * Copyright 2005-2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Created on Jun 16, 2005
 */
package pcgen.base.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * A DoubleKeyMap is a relative of java.util.Map, however, each value is stored
 * with two keys instead of one.
 * 
 * This class is reference-semantic. In appropriate cases (such as calling the
 * addToListFor method), DoubleKeyMap will maintain a reference to the given
 * Object. DoubleKeyMap will not modify any of the Objects it is passed;
 * however, it reserves the right to return references to Objects it contains to
 * other Objects.
 * 
 * However, when any method in which DoubleKeyMap returns a Collection,
 * ownership of the Collection itself is transferred to the calling Object, but
 * the contents of the Collection (keys, values, etc.) are references whose
 * ownership should be respected.
 * 
 * @param <K1>
 *            The type of the primary keys in this DoubleKeyMap
 * @param <K2>
 *            The type of the secondary keys in this DoubleKeyMap
 * @param <V>
 *            The type of the values in this DoubleKeyMap
 */
public class DoubleKeyMap<K1, K2, V> implements Cloneable
{

	private final Class<? extends Map> firstClass;
	private final Class<? extends Map> secondClass;

	/**
	 * The internal Map to Map structure used to store the objects in this
	 * DoubleKeyMap
	 */
	private Map<K1, Map<K2, V>> map;

	/**
	 * Creates a new, empty DoubleKeyMap
	 */
	public DoubleKeyMap()
	{
		super();
		firstClass = secondClass = HashMap.class;
		map = new HashMap<K1, Map<K2, V>>();
	}

	/**
	 * Creates a new, empty DoubleKeyMap
	 */
	public DoubleKeyMap(Class<? extends Map> cl1, Class<? extends Map> cl2)
	{
		super();
		firstClass = cl1;
		secondClass = cl2;
		map = createGlobalMap();
		createLocalMap();
	}

	/**
	 * Constructs a new DoubleKeyMap with the same mappings as the given
	 * DoubleKeyMap.
	 * 
	 * No reference is maintained to the internal structure of the given
	 * DoubleKeyMap, so modifications to this Map are not reflected in the given
	 * Map (and vice versa). However, the Key and Value objects from the given
	 * Map are maintained by reference, so modification to the Keys or Values of
	 * either this Map or the given Map will be reflected in the other Map (this
	 * is consistent behavior with the analogous constructors in the
	 * java.util.Map implementations)
	 * 
	 * @param otherMap
	 *            The DoubleKeyMap to use as a source of mappings for
	 *            initializing this DoubleKeyMap
	 */
	public DoubleKeyMap(final DoubleKeyMap<K1, K2, V> otherMap)
	{
		this();
		putAll(otherMap);
	}

	/**
	 * Put the given value into this DoubleKeyMap for the given keys. If this
	 * DoubleKeyMap already contained a mapping for the given keys, the previous
	 * value is returned. Otherwise, null is returned.
	 * 
	 * @param key1
	 *            The primary key for storing the given value
	 * @param key2
	 *            The secondary key for storing the given value
	 * @param value
	 *            The value to be stored for the given keys
	 * @return Object The previous value stored for the given keys; null if the
	 *         given keys did not previously have a mapping
	 */
	public V put(K1 key1, K2 key2, V value)
	{
		Map<K2, V> localMap = map.get(key1);
		if (localMap == null)
		{
			localMap = createLocalMap();
			map.put(key1, localMap);
		}
		return localMap.put(key2, value);
	}

	/**
	 * Copies the key/value combinations from the given DoubleKeyMap into this
	 * DoubleKeyMap. If this DoubleKeyMap already contained a mapping for the
	 * any of the key combinations in the given DoubleKeyMap, the previous value
	 * is overwritten.
	 * 
	 * @param dkm
	 *            The DoubleKeyMap for which the key/value combinations should
	 *            be placed into this DoubleKeyMap
	 */
	public void putAll(DoubleKeyMap<K1, K2, V> dkm)
	{
		for (Entry<K1, Map<K2, V>> me : dkm.map.entrySet())
		{
			Map<K2, V> localMap = map.get(me.getKey());
			if (localMap == null)
			{
				localMap = createLocalMap();
				map.put(me.getKey(), localMap);
			}
			localMap.putAll(me.getValue());
		}
	}

	/**
	 * Get the value from DoubleKeyMap for the given keys. If this DoubleKeyMap
	 * does not a mapping for the given keys, null is returned.
	 * 
	 * @param key1
	 *            The primary key for retrieving the given value
	 * @param key2
	 *            The secondary key for retrieving the given value
	 * @return Object The value stored for the given keys
	 */
	public V get(K1 key1, K2 key2)
	{
		Map<K2, V> localMap = map.get(key1);
		if (localMap == null)
		{
			return null;
		}
		return localMap.get(key2);
	}

	/**
	 * Returns trus if the DoubleKeyMap contains a value stored under the given
	 * primary key and any secondary key.
	 * 
	 * @param key1
	 *            The primary key for retrieving the given value
	 * @return true If a value is in the map under the given primary key
	 */
	public boolean containsKey(K1 key1)
	{
		return map.containsKey(key1);
	}

	/**
	 * Returns trus if the DoubleKeyMap contains a value for the given keys.
	 * 
	 * @param key1
	 *            The primary key for retrieving the given value
	 * @param key2
	 *            The secondary key for retrieving the given value
	 * @return true If a value is in the map given two keys
	 */
	public boolean containsKey(K1 key1, K2 key2)
	{
		Map<K2, V> localMap = map.get(key1);
		if (localMap == null)
		{
			return false;
		}
		return localMap.containsKey(key2);
	}

	/**
	 * Removes the value from DoubleKeyMap for the given keys and returns the
	 * value that was removed from the DoubleKeyMap. If this DoubleKeyMap did
	 * not a mapping for the given keys, null is returned.
	 * 
	 * @param key1
	 *            The primary key for retrieving the given value
	 * @param key2
	 *            The secondary key for retrieving the given value
	 * @return Object The value previously mapped to the given keys
	 */
	public V remove(K1 key1, K2 key2)
	{
		Map<K2, V> localMap = map.get(key1);
		if (localMap == null)
		{
			return null;
		}
		V o = localMap.remove(key2);
		// cleanup!
		if (localMap.isEmpty())
		{
			map.remove(key1);
		}
		return o;
	}

	/**
	 * Removes all objects with the given primary key from the DoubleKeyMap.
	 * 
	 * @param key1
	 *            The primary key used to remove the value in this DoubleKeyMap.
	 * @return the Map of objects stored in this DoubleKeyMap for the given
	 *         primary keys. null if this DoubleKeyMap does not have an object
	 *         stored with the given primary key.
	 */
	public Map<K2, V> removeAll(K1 key1)
	{
		return map.remove(key1);
	}

	/**
	 * Returns a Set of the primary keys for this DoubleKeyMap
	 * 
	 * Note: This Set is reference-semantic. The ownership of the Set is
	 * transferred to the calling Object; therefore, changes to the returned Set
	 * will NOT impact the DoubleKeyMap.
	 * 
	 * @return A Set of the primary keys for this DoubleKeyMap
	 */
	public Set<K1> getKeySet()
	{
		return new HashSet<K1>(map.keySet());
	}

	/**
	 * Returns a Set of the secondary keys for the given primary key in this
	 * DoubleKeyMap
	 * 
	 * Note: This Set is reference-semantic. The ownership of the Set is
	 * transferred to the calling Object; therefore, changes to the returned Set
	 * will NOT impact the DoubleKeyMap.
	 * 
	 * @param aPrimaryKey
	 *            The primary key to retrieve keys for.
	 * 
	 * @return A <tt>Set</tt> of secondary key objects for the given primary
	 *         key.
	 */
	public Set<K2> getSecondaryKeySet(final K1 aPrimaryKey)
	{
		final Map<K2, V> localMap = map.get(aPrimaryKey);
		if (localMap == null)
		{
			return Collections.emptySet();
		}
		return new HashSet<K2>(localMap.keySet());
	}

	/**
	 * Clears this DoubleKeyMap
	 */
	public void clear()
	{
		map.clear();
	}

	/**
	 * Returns a Set of the values stored in this DoubleKeyMap for the given
	 * primary key.
	 * 
	 * Note: This Set is reference-semantic. The ownership of the Set is
	 * transferred to the calling Object; therefore, changes to the returned Set
	 * will NOT impact the DoubleKeyMap.
	 * 
	 * @param key1
	 *            The primary key for which the values will be returned
	 * @return a Set of the values stored in this DoubleKeyMap for the given
	 *         primary key
	 */
	public Set<V> values(K1 key1)
	{
		final Map<K2, V> localMap = map.get(key1);
		if (localMap == null)
		{
			return Collections.emptySet();
		}
		return new HashSet<V>(localMap.values());
	}

	/**
	 * Returns true if the DuobleKeyMap is empty; false otherwise
	 * 
	 * @return true if the DuobleKeyMap is empty; false otherwise
	 */
	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	/**
	 * Returns the number of primary keys in this DoubleKeyMap
	 * 
	 * @return the number of primary keys in this DoubleKeyMap
	 */
	public int primaryKeyCount()
	{
		return map.size();
	}

	/**
	 * Returns the number of secondary keys in this DoubleKeyMap for the given
	 * primary key
	 * 
	 * @param aPrimaryKey
	 *            The primary key for which the number of secondary keys will be
	 *            returned
	 * @return the number of secondary keys in this DoubleKeyMap for the given
	 *         primary key
	 */
	public int secondaryKeyCount(K1 aPrimaryKey)
	{
		final Map<K2, V> localMap = map.get(aPrimaryKey);
		if (localMap == null)
		{
			return 0;
		}
		return localMap.size();
	}

	@SuppressWarnings("unchecked")
	@Override
	public DoubleKeyMap<K1, K2, V> clone() throws CloneNotSupportedException
	{
		DoubleKeyMap<K1, K2, V> dkm = (DoubleKeyMap<K1, K2, V>) super.clone();
		dkm.map = createGlobalMap();
		for (Map.Entry<K1, Map<K2, V>> me : map.entrySet())
		{
			dkm.map.put(me.getKey(), new HashMap<K2, V>(me.getValue()));
		}
		return dkm;
	}

	/**
	 * Removes the given value from DoubleKeyMap for the given primary key.
	 * Returns true if there was a mapping removed for the given value under the
	 * given primary key.
	 * 
	 * @param key1
	 *            The primary key for removing the given value
	 * @return Object true if there was a mapping removed for the given value
	 *         under the given primary key; false otherwise
	 */
	public boolean removeValue(K1 class1, V obj)
	{
		final Map<K2, V> localMap = map.get(class1);
		if (localMap != null)
		{
			return localMap.values().remove(obj);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return map.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof DoubleKeyMap
				&& map.equals(((DoubleKeyMap<?, ?, ?>) o).map);
	}

	private Map<K2, V> createLocalMap()
	{
		try
		{
			return secondClass.newInstance();
		}
		catch (InstantiationException e)
		{
			throw new IllegalArgumentException(
					"Class for DoubleKeyMap must possess a zero-argument constructor",
					e);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalArgumentException(
					"Class for DoubleKeyMap must possess a public zero-argument constructor",
					e);
		}
	}

	private Map<K1, Map<K2, V>> createGlobalMap()
	{
		try
		{
			return firstClass.newInstance();
		}
		catch (InstantiationException e)
		{
			throw new IllegalArgumentException(
					"Class for DoubleKeyMap must possess a zero-argument constructor",
					e);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalArgumentException(
					"Class for DoubleKeyMap must possess a public zero-argument constructor",
					e);
		}
	}

}
