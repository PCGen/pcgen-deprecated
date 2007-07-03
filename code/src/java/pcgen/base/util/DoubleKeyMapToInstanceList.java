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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
 * Note: For purposes of containing and removing an object from a List, this
 * Class performs instance tests (meaning the use of == not .equals()).
 * 
 * @param <K1>
 *            The type of the primary keys in this DoubleKeyMap
 * @param <K2>
 *            The type of the secondary keys in this DoubleKeyMap
 * @param <V>
 *            The type of the values in this DoubleKeyMap
 */
public class DoubleKeyMapToInstanceList<K1, K2, V> implements Cloneable
{

	/**
	 * The actual map containing the map of objects to Lists
	 */
	private Map<K1, HashMapToInstanceList<K2, V>> mtmtl =
			new HashMap<K1, HashMapToInstanceList<K2, V>>();

	/**
	 * Constructs a new DoubleKeyMapToInstanceList
	 */
	public DoubleKeyMapToInstanceList()
	{
		super();
	}

	/**
	 * Adds the given value to the List for the given keys. The null value
	 * cannot be used as a key in a DoubleKeyMapToInstanceList. This method will
	 * automatically initialize the list for the given key if there is not
	 * already a List for that key.
	 * 
	 * This method is reference-semantic and this DoubleKeyMapToInstanceList
	 * will maintain a strong reference to both the key object and the value
	 * object given as arguments to this method.
	 * 
	 * @param key1
	 *            The primary key indicating which List the given object should
	 *            be added to.
	 * @param key2
	 *            The secondary key indicating which List the given object
	 *            should be added to.
	 * @param value
	 *            The value to be added to the List for the given keys.
	 */
	public void addToListFor(K1 key1, K2 key2, V value)
	{
		HashMapToInstanceList<K2, V> localMap = mtmtl.get(key1);
		if (localMap == null)
		{
			localMap = new HashMapToInstanceList<K2, V>();
			mtmtl.put(key1, localMap);
		}
		localMap.addToListFor(key2, value);
	}

	/**
	 * Returns a copy of the List contained in this DoubleKeyMapToInstanceList
	 * for the given keys. This method returns null if the given key is not in
	 * this DoubleKeyMapToInstanceList.
	 * 
	 * This method is value-semantic in that no changes are made to the object
	 * passed into the method and ownership of the returned List is transferred
	 * to the class calling this method.
	 * 
	 * @param key1
	 *            The primary key for retrieving the given List
	 * @param key2
	 *            The secondary key for retrieving the given List
	 * @return a copy of the List contained in this DoubleKeyMapToInstanceList
	 *         for the given key; null if the given key is not a key in this
	 *         DoubleKeyMapToInstanceList.
	 */
	public List<V> getListFor(K1 key1, K2 key2)
	{
		HashMapToInstanceList<K2, V> localMap = mtmtl.get(key1);
		if (localMap == null)
		{
			return null;
		}
		return localMap.getListFor(key2);
	}

	/**
	 * Returns true if this DoubleKeyMapToInstanceList contains a List for the
	 * given keys. This method returns false if the given keys are not in this
	 * DoubleKeyMapToInstanceList.
	 * 
	 * This method is value-semantic in that no changes are made to the objects
	 * passed into the method.
	 * 
	 * @param key1
	 *            The primary key for testing presence of a List
	 * @param key2
	 *            The secondary key for testing presence of a List
	 * @return true if this DoubleKeyMapToInstanceList contains a List for the
	 *         given keys; false otherwise.
	 */
	public boolean containsListFor(K1 key1, K2 key2)
	{
		HashMapToInstanceList<K2, V> localMap = mtmtl.get(key1);
		if (localMap == null)
		{
			return false;
		}
		return localMap.containsListFor(key2);
	}

	/**
	 * Removes the List for the given keys. Note there is no requirement that
	 * the list for the given keys be empty before this method is called.
	 * 
	 * Obviously, ownership of the returned List is transferred to the object
	 * calling this method.
	 * 
	 * @param key1
	 *            The primary key indicating the List to remove
	 * @param key2
	 *            The secondary key indicating the List to remove
	 * @return The List which this DoubleKeyMapToInstanceList previous mapped
	 *         the given keys
	 */
	public List<V> removeListFor(K1 key1, K2 key2)
	{
		HashMapToInstanceList<K2, V> localMap = mtmtl.get(key1);
		if (localMap == null)
		{
			return null;
		}
		List<V> o = localMap.removeListFor(key2);
		// cleanup!
		if (localMap.isEmpty())
		{
			mtmtl.remove(key1);
		}
		return o;
	}

	/**
	 * Removes the given object from the list for the given keys (this is an
	 * instance test against the specific object, not the value of the object).
	 * Returns true if the value was successfully removed from the list for the
	 * given key. Returns false if there is not a list for the given keys or if
	 * the list for the given keys did not contain the given value object.
	 * 
	 * @param key1
	 *            The primary key indicating which List the given object should
	 *            be removed from
	 * @param key2
	 *            The secondary key indicating which List the given object
	 *            should be removed from
	 * @param object
	 *            The value to be removed from the List for the given keys
	 * @return true if the value was successfully removed from the list for the
	 *         given keys; false otherwise
	 */
	public boolean removeFromListFor(K1 key1, K2 key2, V object)
	{
		/*
		 * Note there is no requirement that a Key is added before this method
		 * is called
		 */
		HashMapToInstanceList<K2, V> localMap = mtmtl.get(key1);
		if (localMap == null)
		{
			return false;
		}
		boolean b = localMap.removeFromListFor(key2, object);
		// cleanup!
		if (b && localMap.isEmpty())
		{
			mtmtl.remove(key1);
		}
		return b;
	}

	/**
	 * Returns a Set indicating the primary Keys of this
	 * DoubleKeyMapToInstanceList. Ownership of the Set is transferred to the
	 * calling Object, no association is kept between the Set and this
	 * MapToList. (Thus, removal of a key from the returned Set will not remove
	 * that key from this DoubleKeyMapToInstanceList)
	 * 
	 * NOTE: This method returns all of the keys this DoubleKeyMapToInstanceList
	 * contains. It DOES NOT determine whether the Lists defined for the keys
	 * are empty. Therefore, it is possible that this DoubleKeyMapToInstanceList
	 * contains one or more keys, and all of the lists associated with those
	 * keys are empty, yet this method will return a non-zero length Set.
	 * 
	 * @return a Set containing the primary keys in this
	 *         DoubleKeyMapToInstanceList
	 */
	public Set<K1> getKeySet()
	{
		// Need to 'clone' the Set, since Map returns a set that is still
		// associated with the Map
		return new HashSet<K1>(mtmtl.keySet());
	}

	/**
	 * Returns a Set of the secondary keys for the given primary key in this
	 * DoubleKeyMapToInstanceList
	 * 
	 * Note: This Set is reference-semantic. The ownership of the Set is
	 * transferred to the calling Object; therefore, changes to the returned Set
	 * will NOT impact the DoubleKeyMapToInstanceList.
	 * 
	 * @param aPrimaryKey
	 *            The primary key to retrieve keys for.
	 * 
	 * @return A <tt>Set</tt> of secondary key objects for the given primary
	 *         key.
	 */
	public Set<K2> getSecondaryKeySet(final K1 aPrimaryKey)
	{
		HashMapToInstanceList<K2, V> localMap = mtmtl.get(aPrimaryKey);
		if (localMap == null)
		{
			return Collections.emptySet();
		}
		return localMap.getKeySet();
	}

	/**
	 * Clears this DoubleKeyMapToInstanceList
	 */
	public void clear()
	{
		mtmtl.clear();
	}

	/**
	 * Returns true if this DoubleKeyMapToInstanceList contains no Lists.
	 * 
	 * NOTE: This method checks whether this DoubleKeyMapToInstanceList contains
	 * any Lists for any key. It DOES NOT test whether all Lists defined for all
	 * keys are empty. Therefore, it is possible that this
	 * DoubleKeyMapToInstanceList contains one or more keys, and all of the
	 * lists associated with those keys are empty, yet this method will return
	 * false.
	 * 
	 * @return true if this DoubleKeyMapToInstanceList contains no Lists; false
	 *         otherwise
	 */
	public boolean isEmpty()
	{
		return mtmtl.isEmpty();
	}

	/**
	 * Returns the number of primary key maps contained by this
	 * DoubleKeyMapToInstanceList.
	 * 
	 * NOTE: This method counts the number of Lists this
	 * DoubleKeyMapToInstanceList contains. It DOES NOT determine whether all
	 * Lists defined for all keys are empty. Therefore, it is possible that this
	 * DoubleKeyMapToInstanceList contains one or more keys, and all of the
	 * lists associated with those keys are empty, yet this method will return a
	 * non-zero value.
	 * 
	 * @return The number of lists contained by this DoubleKeyMapToInstanceList.
	 */
	public int firstKeyCount()
	{
		return mtmtl.size();
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		DoubleKeyMapToInstanceList<K1, K2, V> dkm =
				(DoubleKeyMapToInstanceList<K1, K2, V>) super.clone();
		dkm.mtmtl = new HashMap<K1, HashMapToInstanceList<K2, V>>();
		for (Iterator<K1> it = mtmtl.keySet().iterator(); it.hasNext();)
		{
			K1 key = it.next();
			HashMapToInstanceList<K2, V> m = mtmtl.get(key);
			HashMapToInstanceList<K2, V> hmtl =
					new HashMapToInstanceList<K2, V>();
			hmtl.addAllLists(m);
			dkm.mtmtl.put(key, hmtl);
		}
		return dkm;
	}

	/**
	 * Returns true if this DoubleKeyMapToInstanceList contains a List for the
	 * given keys and that list contains the given object (this is an instance
	 * test against the specific object, not the value of the object). Note,
	 * this method returns false if the given keys are not in this
	 * DoubleKeyMapToInstanceList.
	 * 
	 * This method is value-semantic in that no changes are made to the objects
	 * passed into the method.
	 * 
	 * @param key1
	 *            The primary key for retrieving the List to be checked
	 * @param key2
	 *            The secondary key for retrieving the List to be checked
	 * @param value
	 *            The value to find in the List for the given keys.
	 * @return true if this DoubleKeyMapToInstanceList contains a List for the
	 *         given keys AND that list contains the given value; false
	 *         otherwise.
	 */
	public boolean containsInList(K1 key1, K2 key2, V value)
	{
		return containsListFor(key1, key2)
			&& mtmtl.get(key1).containsInList(key2, value);
	}

	/**
	 * Returns the number of objects in the List for the given keys. This method
	 * will throw a NullPointerException if this DoubleKeyMapToInstanceList does
	 * not contain a List for the given key.
	 * 
	 * This method is value-semantic in that no changes are made to the object
	 * passed into the method.
	 * 
	 * @param key1
	 *            The primary key for retrieving the List to be checked
	 * @param key2
	 *            The secondary key for retrieving the List to be checked
	 * @return the number of objects in the List for the given keys
	 */
	public int sizeOfListFor(K1 key1, K2 key2)
	{
		HashMapToInstanceList<K2, V> localMap = mtmtl.get(key1);
		return localMap == null ? 0 : localMap.sizeOfListFor(key2);
	}

	/**
	 * Returns a the object in the given position in the List contained in this
	 * DoubleKeyMapToInstanceList for the given keys. This method returns null
	 * if the given key is not in this DoubleKeyMapToInstanceList.
	 * 
	 * This method is value-semantic in that no changes are made to the objects
	 * passed into the method.
	 * 
	 * @param key1
	 *            The primary key for retrieving the value
	 * @param key2
	 *            The secondary key for retrieving the value
	 * @param i
	 *            the location in the List for the given keys of the value to be
	 *            returned
	 * @return the object in the given position in the List contained in this
	 *         DoubleKeyMapToInstanceList for the given keys; null if the given
	 *         key is not a key in this DoubleKeyMapToInstanceList.
	 */
	public V getItemFor(K1 key1, K2 key2, int i)
	{
		HashMapToInstanceList<K2, V> localMap = mtmtl.get(key1);
		return localMap == null ? null : localMap.getElementInList(key2, i);
	}
}
