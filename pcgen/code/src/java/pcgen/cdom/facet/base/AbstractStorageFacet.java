/*
 * Copyright (c) 2012 Tom Parker <thpr@users.sourceforge.net>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.facet.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import pcgen.base.test.InequalityTester;
import pcgen.base.util.DoubleKeyMap;
import pcgen.cdom.enumeration.CharID;
import pcgen.util.Logging;

/**
 * An AbstractStorageFacet is a facet which stores contents in the overall CDOM
 * cache. All classes (facets) that want to store information in the cache must
 * extend this class.
 * 
 * @author Tom Parker (thpr [at] yahoo.com)
 */
public abstract class AbstractStorageFacet
{
	
	public final Class<?> thisClass = getClass();

	/**
	 * Copies the contents of the AbstractStorageFacet from one Player Character
	 * to another Player Character, based on the given CharIDs representing
	 * those Player Characters.
	 * 
	 * This is a method each AbstractStorageFacet must implement in order to do
	 * 2 things: First, it must avoid exposing the mutable storage information
	 * stored in the cache to other classes. Second, this ensures that every
	 * AbstractStorageFacet has implemented a copy function so that any deep
	 * copies (if Lists need to be cloned, etc.) is done appropriately.
	 * 
	 * Note also the copy is a one-time event and no references should be
	 * maintained between the Player Characters represented by the given CharIDs
	 * (meaning once this copy takes place, any change to the
	 * AbstractStorageFacet of one Player Character will only impact the Player
	 * Character where the AbstractStorageFacet was changed).
	 * 
	 * @param source
	 *            The CharID representing the Player Character from which the
	 *            information should be copied
	 * @param copy
	 *            The CharID representing the Player Character to which the
	 *            information should be copied
	 */
	public abstract void copyContents(CharID source, CharID copy);

	/**
	 * The actual cache that stores the CDOM information, as stored by the
	 * identifying CharID of a PlayerCharacter and the class of the facet
	 * storing the information
	 */
	private static final DoubleKeyMap<CharID, Class<?>, Object> CACHE =
			new DoubleKeyMap<CharID, Class<?>, Object>(WeakHashMap.class, HashMap.class);

	/*
	 * Note: the use of CACHE.getReadOnlyMapFor(K1) in peekAtCache makes calling
	 * CACHE.removeAll(k1) or CACHE.clear() [not used in this class at the
	 * moment] a rather dangerous activity that is prone to later frustration in
	 * debugging. It is advised that if such a call is every considered that
	 * detailed consideration is made of the consequences so that debugging
	 * information is not destroyed in the process. - thpr Dec 15, 2012.
	 */
	
	/**
	 * Removes the information from the cache for a given Player Character and
	 * facet (as identified by the Class)
	 * 
	 * @param id
	 *            The CharID for which information from the cache should be
	 *            removed
	 * @return The information which was removed from the Cache for the Player
	 *         Character identified by the given CharID and the facet identified
	 *         by the given Class.
	 */
	public Object removeCache(CharID id)
	{
		if (id == null)
		{
			throw new IllegalArgumentException(
				"CharID cannot be null in removeCache");
		}
		return CACHE.remove(id, thisClass);
	}

	/**
	 * Sets the information from the cache for a given Player Character and
	 * facet (as identified by the Class)
	 * 
	 * @param id
	 *            The CharID for which information from the cache should be
	 *            removed
	 * @param o
	 *            The object to be stored in the cache.
	 * @return The previous information which was removed from the Cache for the
	 *         Player Character identified by the given CharID and the facet
	 *         identified by the given Class.
	 */
	public Object setCache(CharID id, Object o)
	{
		if (id == null)
		{
			throw new IllegalArgumentException(
				"CharID cannot be null in setCache");
		}
		return CACHE.put(id, thisClass, o);
	}

	/**
	 * Retrieves the information from the cache for a given Player Character and
	 * facet (as identified by the Class)
	 * 
	 * @param id
	 *            The CharID for which information from the cache should be
	 *            removed
	 * @return The information in the Cache for the Player Character identified
	 *         by the given CharID and the facet identified by the given Class.
	 */
	public Object getCache(CharID id)
	{
		if (id == null)
		{
			throw new IllegalArgumentException(
				"CharID cannot be null in getCache");
		}
		return CACHE.get(id, thisClass);
	}

	/**
	 * Tests whether the contents of the cache are equal for two Player
	 * Characters, as identified by the CharID objects. The given
	 * InequalityTester is used to compare the cache contents.
	 * 
	 * @param id1
	 *            The CharID of the first PlayerCharacter that is to be compared
	 * @param id2
	 *            The CharID of the second PlayerCharacter that is to be
	 *            compared
	 * @param t
	 *            The InequalityTester used to establish equality between
	 *            contents of the cache.
	 * @return true if the contents of the cache are equal (as identified by the
	 *         given InequalityTester) for the Player Characters identified by
	 *         the given CharIDs; false otherwise
	 */
	public static boolean areEqualCache(CharID id1, CharID id2,
		InequalityTester t)
	{
		if (id1 == null)
		{
			throw new IllegalArgumentException(
				"CharID #1 cannot be null in areEqualCache");
		}
		if (id2 == null)
		{
			throw new IllegalArgumentException(
				"CharID #2 cannot be null in areEqualCache");
		}
		Set<Class<?>> set1 = CACHE.getSecondaryKeySet(id1);
		Set<Class<?>> set2 = CACHE.getSecondaryKeySet(id2);
		if (!set1.equals(set2))
		{
			List<Class<?>> l1 = new ArrayList<Class<?>>(set1);
			l1.removeAll(set2);
			List<Class<?>> l2 = new ArrayList<Class<?>>(set2);
			l2.removeAll(set1);
			Logging.errorPrint("Inequal: " + l1 + " " + l2);
			return false;
		}
		for (Class<?> cl : set1)
		{
			Object obj1 = CACHE.get(id1, cl);
			Object obj2 = CACHE.get(id2, cl);
			String equal = t.testEquality(obj1, obj2, cl + "/");
			if (equal != null)
			{
				Logging.errorPrint(equal);
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns a read-only view into the cache for a given CharID.
	 * 
	 * Since the returned Map is read-only, the value here is in that it is a
	 * direct reference to the contents of cache for a given CharID, and is
	 * therefore reference-semantic (the contents of the returned map will
	 * change as the contents of the cache are changed). Ownership of the
	 * returned Map is transferred to the caller, although since it is
	 * read-only, that is perhaps only relevant for determining the garbage
	 * collection time of the decorator that makes the returned Map an
	 * unmodifiable view into this DoubleKeyMap.
	 * 
	 * Note that while this is a read-only map, there is no guarantee that this
	 * returned map is thread-safe. Use in threaded situations with caution.
	 * 
	 * @param id
	 *            The CharID for which a read-only view of the cache should be
	 *            returned.
	 * @return A read-only view of the cache for the given CharID
	 */
	public static Map<Class<?>, Object> peekAtCache(CharID id)
	{
		if (id == null)
		{
			throw new IllegalArgumentException(
				"CharID cannot be null in peekAtCache");
		}
		return CACHE.getReadOnlyMapFor(id);
	}
}
