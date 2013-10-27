/*
 * Copyright 2007 (C) Tom Parker <thpr@sourceforge.net>
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
 * Represents a map where the objects are stored using three keys rather than
 * the traditional single key (single key is provided by the Map interface from
 * java.util).
 * 
 * This class protects its internal structure from modification, but
 * TripleKeyMap is generally reference-semantic. TripleKeyMap will not modify
 * any of the Objects it is passed; however, it reserves the right to return
 * references to Objects it contains to other Objects.
 * 
 * In order to protect its internal structure, any Collection returned by the
 * methods of TripleKeyMap (with the exception of actual keys or values that
 * happen to be Collections) is NOT associated with the TripleKeyMap, and
 * modification of the returned Collection will not modify the internal
 * structure of TripleKeyMap.
 * 
 * CAUTION: If you are not looking for the value-semantic protection of this
 * class (of preventing accidental modification of underlying parts of a
 * three-key Map structure, then this is a convenience method and is not
 * appropriate for use in Java 1.5 (Typed Collections are probably more
 * appropriate).
 */
public class TripleKeyMap<K1, K2, K3, V> implements Cloneable
{

	/**
	 * The underlying map - of primary keys to Maps - for the TripleKeyMap. This
	 * class protects its internal structure, so no method should ever return an
	 * object capable of modifying the maps. All modifications should be done
	 * through direct calls to the methods of TripleKeyMap.
	 */
	private Map<K1, Map<K2, Map<K3, V>>> map =
			new HashMap<K1, Map<K2, Map<K3, V>>>();

	/**
	 * Constructs a new (empty) TripleKeyMap
	 */
	public TripleKeyMap()
	{
		super();
	}

	/**
	 * Constructs a new TripleKeyMap, with the same contents as the given
	 * TripleKeyMap.
	 * 
	 * The given TripleKeyMap is not modified and the constructed TripleKeyMap
	 * will be independent of the given TripleKeyMap other than the Objects used
	 * to represent the keys and values. (In other words, modification of the
	 * given TripleKeyMap will not alter the constructed TripleKeyMap, and vice
	 * versa)
	 * 
	 * @param otherMap
	 *            The TripleKeyMap whose contents should be copied into this
	 *            TripleKeyMap.
	 */
	public TripleKeyMap(final TripleKeyMap<K1, K2, K3, V> otherMap)
	{
		for (Entry<K1, Map<K2, Map<K3, V>>> me : otherMap.map.entrySet())
		{
			HashMap<K2, Map<K3, V>> localMap = new HashMap<K2, Map<K3, V>>();
			map.put(me.getKey(), localMap);
			for (Entry<K2, Map<K3, V>> subME : me.getValue().entrySet())
			{
				localMap.put(subME.getKey(), new HashMap<K3, V>(subME
					.getValue()));
			}
		}
	}

	/**
	 * Puts a new object into the TripleKeyMap.
	 * 
	 * @param key1
	 *            The primary key used to store the value in this TripleKeyMap.
	 * @param key2
	 *            The secondary key used to store the value in this
	 *            TripleKeyMap.
	 * @param key3
	 *            The tertiary key used to store the value in this TripleKeyMap.
	 * @param value
	 *            The value to be stored in this TripleKeyMap.
	 * @return the Object previously stored in this TripleKeyMap with the given
	 *         keys. null if this TripleKeyMap did not previously have an object
	 *         stored with the given keys.
	 */
	public V put(K1 key1, K2 key2, K3 key3, V value)
	{
		Map<K2, Map<K3, V>> localMap = map.get(key1);
		Map<K3, V> subMap = null;
		if (localMap == null)
		{
			localMap = new HashMap<K2, Map<K3, V>>();
			map.put(key1, localMap);
		}
		else
		{
			subMap = localMap.get(key2);
		}
		if (subMap == null)
		{
			subMap = new HashMap<K3, V>();
			localMap.put(key2, subMap);
		}
		return subMap.put(key3, value);
	}

	/**
	 * Gets an object from the TripleKeyMap.
	 * 
	 * @param key1
	 *            The primary key used to get the value in this TripleKeyMap.
	 * @param key2
	 *            The secondary key used to get the value in this TripleKeyMap.
	 * @param key3
	 *            The tertiary key used to get the value in this TripleKeyMap.
	 * @param value
	 *            The value stored in this TripleKeyMap for the given keys.
	 * @return the Object stored in this TripleKeyMap for the given keys. null
	 *         if this TripleKeyMap does not have an object stored with the
	 *         given keys.
	 */
	public V get(K1 key1, K2 key2, K3 key3)
	{
		Map<K2, Map<K3, V>> localMap = map.get(key1);
		if (localMap == null)
		{
			return null;
		}
		Map<K3, V> subMap = localMap.get(key2);
		if (subMap == null)
		{
			return null;
		}
		return subMap.get(key3);
	}

	/**
	 * Returns true if an object is stored in this TripleKeyMap for the given
	 * keys.
	 * 
	 * @param key1
	 *            The primary key to be tested for containing a value in this
	 *            TripleKeyMap.
	 * @param key2
	 *            The secondary key to be tested for containing a value in this
	 *            TripleKeyMap.
	 * @param key3
	 *            The tertiary key to be tested for containing a value in this
	 *            TripleKeyMap.
	 * @return true if this TripleKeyMap has an Object stored in this
	 *         TripleKeyMap for the given keys; false otherwise
	 */
	public boolean containsKey(K1 key1, K2 key2, K3 key3)
	{
		Map<K2, Map<K3, V>> localMap = map.get(key1);
		if (localMap == null)
		{
			return false;
		}
		Map<K3, V> subMap = localMap.get(key2);
		if (subMap == null)
		{
			return false;
		}
		return subMap.containsKey(key3);
	}

	/**
	 * Removes an object from the TripleKeyMap.
	 * 
	 * @param key1
	 *            The primary key used to remove the value in this TripleKeyMap.
	 * @param key2
	 *            The secondary key used to remove the value in this
	 *            TripleKeyMap.
	 * @param key3
	 *            The tertiary key used to remove the value in this
	 *            TripleKeyMap.
	 * @return the Object stored in this TripleKeyMap for the given keys. null
	 *         if this TripleKeyMap does not have an object stored with the
	 *         given keys.
	 */
	public V remove(K1 key1, K2 key2, K3 key3)
	{
		Map<K2, Map<K3, V>> localMap = map.get(key1);
		if (localMap == null)
		{
			return null;
		}
		Map<K3, V> subMap = localMap.get(key2);
		if (subMap == null)
		{
			return null;
		}
		V o = subMap.remove(key3);
		/*
		 * Clean up the primary maps if the secondary maps are empty. This is
		 * required to avoid a false report from get*KeySet. Generally, if an
		 * object is added with the keys KEY1 and KEY2, then subsequently
		 * removed (and no other objects were stored with those keys), then
		 * getKeySet() should never return KEY1 (and there is a corollary for
		 * KEY2 cleanup, though that is implicit and does not require special
		 * code)
		 */
		if (subMap.isEmpty())
		{
			localMap.remove(key2);
		}
		if (localMap.isEmpty())
		{
			map.remove(key1);
		}
		return o;
	}

	/**
	 * Returns a Set which contains the primary keys for this TripleKeyMap.
	 * Returns an empty Set if this TripleKeyMap is empty (has no primary keys)
	 * 
	 * Ownership of the returned Set is transferred to the Object that called
	 * this method. Modification of the returned Set will not modify this
	 * TripleKeyMap, and modification of this TripleKeyMap will not alter the
	 * returned Set.
	 * 
	 * @return A Set containing the primary keys for this TripleKeyMap.
	 */
	public Set<K1> getKeySet()
	{
		return new HashSet<K1>(map.keySet());
	}

	/**
	 * Returns a Set which contains the secondary keys for the given primary key
	 * within this TripleKeyMap. Returns an empty Set if there are no objects
	 * stored in the TripleKeyMap with the given primary key.
	 * 
	 * Ownership of the returned Set is transferred to the Object that called
	 * this method. Modification of the returned Set will not modify this
	 * TripleKeyMap, and modification of this TripleKeyMap will not alter the
	 * returned Set.
	 * 
	 * @param key1
	 *            The primary key used to identify the secondary Key Set in this
	 *            TripleKeyMap.
	 * @return A Set containing the secondary keys for the given primary key
	 *         within this TripleKeyMap.
	 */
	public Set<K2> getSecondaryKeySet(final K1 aPrimaryKey)
	{
		final Map<K2, Map<K3, V>> localMap = map.get(aPrimaryKey);
		if (localMap == null)
		{
			return Collections.emptySet();
		}
		return new HashSet<K2>(localMap.keySet());
	}

	/**
	 * Returns a Set which contains the tertiary keys for the given primary key
	 * within this TripleKeyMap. Returns an empty Set if there are no objects
	 * stored in the TripleKeyMap with the given primary key.
	 * 
	 * Ownership of the returned Set is transferred to the Object that called
	 * this method. Modification of the returned Set will not modify this
	 * TripleKeyMap, and modification of this TripleKeyMap will not alter the
	 * returned Set.
	 * 
	 * @param key1
	 *            The primary key used to identify the Tertiary Key Set in this
	 *            TripleKeyMap.
	 * @param key2
	 *            The secondary key used to identify the Tertiary Key Set in
	 *            this TripleKeyMap.
	 * @return A Set containing the Tertiary keys for the given primary and
	 *         secondary keys within this TripleKeyMap.
	 */
	public Set<K3> getTertiaryKeySet(K1 key1, K2 key2)
	{
		final Map<K2, Map<K3, V>> localMap = map.get(key1);
		if (localMap == null)
		{
			return Collections.emptySet();
		}
		Map<K3, V> subMap = localMap.get(key2);
		if (subMap == null)
		{
			return Collections.emptySet();
		}
		return new HashSet<K3>(subMap.keySet());
	}

	/**
	 * Clears this TripleKeyMap.
	 */
	public void clear()
	{
		map.clear();
	}

	/**
	 * Returns true if the TripleKeyMap is empty
	 * 
	 * @return true if the TripleKeyMap is empty; false otherwise
	 */
	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	/**
	 * Returns the number of primary keys in this TripleKeyMap
	 * 
	 * @return the number of primary keys in this TripleKeyMap
	 */
	public int firstKeyCount()
	{
		return map.size();
	}

	/**
	 * Clones this TripleKeyMap. The contents of the TripleKeyMap (the keys and
	 * values) are not cloned - this is not a truly deep clone. However, the
	 * internal structure of the TripleKeyMap is sufficiently cloned in order to
	 * protect the internal structure of the original or the clone from being
	 * modified by the other object.
	 * 
	 * @return A clone of this TripleKeyMap that contains the same keys and
	 *         values as the original TripleKeyMap.
	 */
	@Override
	public TripleKeyMap<K1, K2, K3, V> clone()
		throws CloneNotSupportedException
	{
		/*
		 * This cast will cause a Generic type safety warning. This is
		 * impossible to avoid, given that super.clone() will not return a
		 * TripleKeyMap with the proper Generic arguments. - Thomas Parker
		 * 1/26/07
		 */
		TripleKeyMap<K1, K2, K3, V> tkm =
				(TripleKeyMap<K1, K2, K3, V>) super.clone();
		/*
		 * This provides a semi-deep clone of the TripleKeyMap, in order to
		 * protect the internal structure of the TripleKeyMap from modification.
		 * Note the key and value objects are not cloned, so this is not truly a
		 * deep clone, but is deep enough to protect the internal structure.
		 */
		tkm.map = new HashMap<K1, Map<K2, Map<K3, V>>>();
		for (Entry<K1, Map<K2, Map<K3, V>>> me : map.entrySet())
		{
			HashMap<K2, Map<K3, V>> localMap = new HashMap<K2, Map<K3, V>>();
			tkm.map.put(me.getKey(), localMap);
			for (Entry<K2, Map<K3, V>> subME : me.getValue().entrySet())
			{
				localMap.put(subME.getKey(), new HashMap<K3, V>(subME
					.getValue()));
			}
		}
		return tkm;
	}

	/**
	 * Returns a Set of the values stored in this TripleKeyMap for the given
	 * primary and secondary keys.
	 * 
	 * Note: This Set is reference-semantic. The ownership of the Set is
	 * transferred to the calling Object; therefore, changes to the returned Set
	 * will NOT impact the TripleKeyMap.
	 * 
	 * @param key1
	 *            The primary key for which the values will be returned
	 * @param key2
	 *            The secondary key for which the values will be returned
	 * @return a Set of the values stored in this TripleKeyMap for the given
	 *         primary and secondary keys
	 */
	public Set<V> values(K1 key1, K2 key2)
	{
		final Map<K2, Map<K3, V>> localMap = map.get(key1);
		if (localMap == null)
		{
			return Collections.emptySet();
		}
		Map<K3, V> subMap = localMap.get(key2);
		if (subMap == null)
		{
			return Collections.emptySet();
		}
		return new HashSet<V>(subMap.values());
	}

}
