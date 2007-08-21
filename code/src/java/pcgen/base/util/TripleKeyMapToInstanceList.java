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
import java.util.List;
import java.util.Set;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * Represents a TripleKeyMap of objects to Lists. List management is done
 * internally to this class (while copies are accessible, the lists are kept
 * private to this class).
 * 
 * This class is reference-semantic. In appropriate cases (such as calling the
 * addToListFor method), TripleKeyMapToInstanceList will maintain a reference to
 * the given Object. TripleKeyMapToInstanceList will not modify any of the
 * Objects it is passed; however, it reserves the right to return references to
 * Objects it contains to other Objects.
 * 
 * However, when any method in which TripleKeyMapToInstanceList returns a
 * Collection, ownership of the Collection itself is transferred to the calling
 * Object, but the contents of the Collection (keys, values, etc.) are
 * references whose ownership should be respected.
 * 
 * @param <K1>
 *            The type of the primary keys in this TripleKeyMapToInstanceList
 * @param <K2>
 *            The type of the secondary keys in this TripleKeyMapToInstanceList
 * @param <K3>
 *            The type of the tertiary keys in this TripleKeyMapToInstanceList
 * @param <V>
 *            The type of the values in this TripleKeyMapToInstanceList
 */
public class TripleKeyMapToInstanceList<K1, K2, K3, V> // implements Cloneable
{

	/**
	 * The underlying map for the TripleKeyMapToInstanceList. This class
	 * protects its internal structure, so no method should ever return an
	 * object capable of modifying the maps. All modifications should be done
	 * through direct calls to the methods of TripleKeyMapToInstanceList.
	 */
	private final DoubleKeyMap<K1, K2, HashMapToInstanceList<K3, V>> dkmtl =
			new DoubleKeyMap<K1, K2, HashMapToInstanceList<K3, V>>();

	/**
	 * Constructs a new (empty) TripleKeyMapToInstanceList
	 */
	public TripleKeyMapToInstanceList()
	{
		super();
	}

	/**
	 * Adds the given value to the List for the given keys. The null value
	 * cannot be used as a key in a TripleKeyMapToInstanceList. This method will
	 * automatically initialize the list for the given key if there is not
	 * already a List for that key.
	 * 
	 * This method is reference-semantic and this TripleKeyMapToInstanceList
	 * will maintain a strong reference to both the key object and the value
	 * object given as arguments to this method.
	 * 
	 * @param key1
	 *            The primary key indicating which List the given object should
	 *            be added to.
	 * @param key2
	 *            The secondary key indicating which List the given object
	 *            should be added to.
	 * @param key3
	 *            The tertiary key indicating which List the given object should
	 *            be added to.
	 * @param value
	 *            The value to be added to the List for the given keys.
	 */
	public void addToListFor(K1 key1, K2 key2, K3 key3, V value)
	{
		HashMapToInstanceList<K3, V> localMap = dkmtl.get(key1, key2);
		if (localMap == null)
		{
			localMap = new HashMapToInstanceList<K3, V>();
			dkmtl.put(key1, key2, localMap);
		}
		localMap.addToListFor(key3, value);
	}

	/**
	 * Returns a copy of the List contained in this TripleKeyMapToInstanceList
	 * for the given keys. This method returns null if the given key is not in
	 * this TripleKeyMapToInstanceList.
	 * 
	 * This method is value-semantic in that no changes are made to the object
	 * passed into the method and ownership of the returned List is transferred
	 * to the class calling this method.
	 * 
	 * @param key1
	 *            The primary key for retrieving the given List
	 * @param key2
	 *            The secondary key for retrieving the given List
	 * @param key3
	 *            The tertiary key for retrieving the given List
	 * @return a copy of the List contained in this TripleKeyMapToInstanceList
	 *         for the given key; null if the given key is not a key in this
	 *         TripleKeyMapToInstanceList.
	 */
	public List<V> getListFor(K1 key1, K2 key2, K3 key3)
	{
		HashMapToInstanceList<K3, V> localMap = dkmtl.get(key1, key2);
		if (localMap == null)
		{
			return null;
		}
		return localMap.getListFor(key3);
	}

	/**
	 * Returns true if this TripleKeyMapToInstanceList contains a List for the
	 * given keys. This method returns false if the given keys are not in this
	 * TripleKeyMapToInstanceList.
	 * 
	 * This method is value-semantic in that no changes are made to the objects
	 * passed into the method.
	 * 
	 * @param key1
	 *            The primary key for testing presence of a List
	 * @param key2
	 *            The secondary key for testing presence of a List
	 * @param key3
	 *            The tertiary key for testing presence of a List
	 * @return true if this TripleKeyMapToInstanceList contains a List for the
	 *         given keys; false otherwise.
	 */
	public boolean containsListFor(K1 key1, K2 key2, K3 key3)
	{
		HashMapToInstanceList<K3, V> localMap = dkmtl.get(key1, key2);
		if (localMap == null)
		{
			return false;
		}
		return localMap.containsListFor(key3);
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
	 * @param key3
	 *            The tertiary key indicating the List to remove
	 * @return The List which this TripleKeyMapToInstanceList previous mapped
	 *         the given keys
	 */
	public List<V> removeListFor(K1 key1, K2 key2, K3 key3)
	{
		HashMapToInstanceList<K3, V> localMap = dkmtl.get(key1, key2);
		if (localMap == null)
		{
			return null;
		}
		List<V> o = localMap.removeListFor(key3);
		// cleanup!
		if (localMap.isEmpty())
		{
			dkmtl.remove(key1, key2);
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
	 * @param key3
	 *            The tertiary key indicating which List the given object should
	 *            be removed from
	 * @param object
	 *            The value to be removed from the List for the given keys
	 * @return true if the value was successfully removed from the list for the
	 *         given keys; false otherwise
	 */
	public boolean removeFromListFor(K1 key1, K2 key2, K3 key3, V value)
	{
		/*
		 * Note there is no requirement that a Key is added before this method
		 * is called
		 */
		HashMapToInstanceList<K3, V> localMap = dkmtl.get(key1, key2);
		if (localMap == null)
		{
			return false;
		}
		boolean b = localMap.removeFromListFor(key3, value);
		// cleanup!
		if (b && localMap.isEmpty())
		{
			dkmtl.remove(key1, key2);
		}
		return b;
	}

	/**
	 * Returns a Set which contains the primary keys for this
	 * TripleKeyMapToInstanceList. Returns an empty Set if this
	 * TripleKeyMapToInstanceList is empty (has no primary keys)
	 * 
	 * NOTE: This method returns all of the primary keys this
	 * TripleKeyMapToInstanceList contains. It DOES NOT determine whether the
	 * Lists defined for the keys are empty. Therefore, it is possible that this
	 * TripleKeyMapToInstanceList contains one or more keys, and all of the
	 * lists associated with those keys are empty, yet this method will return a
	 * non-zero length Set.
	 * 
	 * Ownership of the returned Set is transferred to the Object that called
	 * this method. Modification of the returned Set will not modify this
	 * TripleKeyMapToInstanceList, and modification of this
	 * TripleKeyMapToInstanceList will not alter the returned Set.
	 * 
	 * @return A Set containing the primary keys for this
	 *         TripleKeyMapToInstanceList.
	 */
	public Set<K1> getKeySet()
	{
		// No Need to 'clone' the Set, done by TripleKeyMap
		return dkmtl.getKeySet();
	}

	/**
	 * Returns a Set which contains the secondary keys for the given primary key
	 * within this TripleKeyMapToInstanceList. Returns an empty Set if there are
	 * no objects stored in the TripleKeyMapToInstanceList with the given
	 * primary key.
	 * 
	 * NOTE: This method returns all of the secondary keys this
	 * TripleKeyMapToInstanceList contains for the given primary key. It DOES
	 * NOT determine whether the Lists defined for the keys are empty.
	 * Therefore, it is possible that this TripleKeyMapToInstanceList contains
	 * one or more keys, and all of the lists associated with those keys are
	 * empty, yet this method will return a non-zero length Set.
	 * 
	 * Ownership of the returned Set is transferred to the Object that called
	 * this method. Modification of the returned Set will not modify this
	 * TripleKeyMapToInstanceList, and modification of this
	 * TripleKeyMapToInstanceList will not alter the returned Set.
	 * 
	 * @return A Set containing the secondary keys for the given primary key
	 *         within this TripleKeyMapToInstanceList.
	 */
	public Set<K2> getSecondaryKeySet(K1 aPrimaryKey)
	{
		return dkmtl.getSecondaryKeySet(aPrimaryKey);
	}

	/**
	 * Returns a Set which contains the tertiary keys for the given primary key
	 * within this TripleKeyMapToInstanceList. Returns an empty Set if there are
	 * no objects stored in the TripleKeyMapToInstanceList with the given
	 * primary key.
	 * 
	 * NOTE: This method returns all of the tertiary keys this
	 * TripleKeyMapToInstanceList contains for the given primary and secondary
	 * keys. It DOES NOT determine whether the Lists defined for the keys are
	 * empty. Therefore, it is possible that this TripleKeyMapToInstanceList
	 * contains one or more keys, and all of the lists associated with those
	 * keys are empty, yet this method will return a non-zero length Set.
	 * 
	 * Ownership of the returned Set is transferred to the Object that called
	 * this method. Modification of the returned Set will not modify this
	 * TripleKeyMapToInstanceList, and modification of this
	 * TripleKeyMapToInstanceList will not alter the returned Set.
	 * 
	 * @param key1
	 *            The primary key used to identify the Tertiary Key Set in this
	 *            TripleKeyMapToInstanceList.
	 * @param key2
	 *            The secondary key used to identify the Tertiary Key Set in
	 *            this TripleKeyMapToInstanceList.
	 * @return A Set containing the Tertiary keys for the given primary and
	 *         secondary keys within this TripleKeyMapToInstanceList.
	 */
	public Set<K3> getTertiaryKeySet(K1 key1, K2 key2)
	{
		HashMapToInstanceList<K3, V> localMap = dkmtl.get(key1, key2);
		if (localMap == null)
		{
			return Collections.emptySet();
		}
		return localMap.getKeySet();
	}

	/**
	 * Clears this TripleKeyMapToInstanceList.
	 */
	public void clear()
	{
		dkmtl.clear();
	}

	/**
	 * Returns true if the TripleKeyMapToInstanceList is empty
	 * 
	 * NOTE: This method checks whether this TripleKeyMapToInstanceList contains
	 * any Lists for any key. It DOES NOT test whether all Lists defined for all
	 * keys are empty. Therefore, it is possible that this
	 * TripleKeyMapToInstanceList contains one or more keys, and all of the
	 * lists associated with those keys are empty, yet this method will return
	 * false.
	 * 
	 * @return true if the TripleKeyMapToInstanceList is empty; false otherwise
	 */
	public boolean isEmpty()
	{
		return dkmtl.isEmpty();
	}

	/**
	 * Returns the number of primary key maps contained by this
	 * TripleKeyMapToInstanceList.
	 * 
	 * NOTE: This method counts the number of Lists this
	 * TripleKeyMapToInstanceList contains. It DOES NOT determine whether all
	 * Lists defined for all keys are empty. Therefore, it is possible that this
	 * TripleKeyMapToInstanceList contains one or more keys, and all of the
	 * lists associated with those keys are empty, yet this method will return a
	 * non-zero value.
	 * 
	 * @return The number of lists contained by this TripleKeyMapToInstanceList.
	 */
	public int firstKeyCount()
	{
		return dkmtl.primaryKeyCount();
	}

	/**
	 * Returns true if this TripleKeyMapToInstanceList contains a List for the
	 * given keys and that list contains the given object (this is an instance
	 * test against the specific object, not the value of the object). Note,
	 * this method returns false if the given keys are not in this
	 * TripleKeyMapToInstanceList.
	 * 
	 * This method is value-semantic in that no changes are made to the objects
	 * passed into the method.
	 * 
	 * @param key1
	 *            The primary key for retrieving the List to be checked
	 * @param key2
	 *            The secondary key for retrieving the List to be checked
	 * @param key3
	 *            The tertiary key for retrieving the List to be checked
	 * @param value
	 *            The value to find in the List for the given keys.
	 * @return true if this TripleKeyMapToInstanceList contains a List for the
	 *         given keys AND that list contains the given value; false
	 *         otherwise.
	 */
	public boolean containsInList(K1 key1, K2 key2, K3 key3, V value)
	{
		return containsListFor(key1, key2, key3)
			&& dkmtl.get(key1, key2).containsInList(key3, value);
	}

	/**
	 * Returns the number of objects in the List for the given keys. This method
	 * will throw a NullPointerException if this TripleKeyMapToInstanceList does
	 * not contain a List for the given key.
	 * 
	 * This method is value-semantic in that no changes are made to the object
	 * passed into the method.
	 * 
	 * @param key1
	 *            The primary key for retrieving the List to be checked
	 * @param key2
	 *            The secondary key for retrieving the List to be checked
	 * @param key3
	 *            The tertiary key for retrieving the List to be checked
	 * @return the number of objects in the List for the given keys
	 */
	public int sizeOfListFor(K1 key1, K2 key2, K3 key3)
	{
		HashMapToInstanceList<K3, V> localMap = dkmtl.get(key1, key2);
		return localMap == null ? 0 : localMap.sizeOfListFor(key3);
	}

	/**
	 * Returns a the object in the given position in the List contained in this
	 * TripleKeyMapToInstanceList for the given keys. This method returns null
	 * if the given key is not in this TripleKeyMapToInstanceList.
	 * 
	 * This method is value-semantic in that no changes are made to the objects
	 * passed into the method.
	 * 
	 * @param key1
	 *            The primary key for retrieving the value
	 * @param key2
	 *            The secondary key for retrieving the value
	 * @param key3
	 *            The tertoary key for retrieving the value
	 * @param i
	 *            the location in the List for the given keys of the value to be
	 *            returned
	 * @return the object in the given position in the List contained in this
	 *         TripleKeyMapToInstanceList for the given keys; null if the given
	 *         key is not a key in this TripleKeyMapToInstanceList.
	 */
	public V getItemFor(K1 key1, K2 key2, K3 key3, int i)
	{
		HashMapToInstanceList<K3, V> localMap = dkmtl.get(key1, key2);
		return localMap == null ? null : localMap.getElementInList(key3, i);
	}
}
