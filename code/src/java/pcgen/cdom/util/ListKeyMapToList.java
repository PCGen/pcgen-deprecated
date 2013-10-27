/*
 * Copyright 2005 (C) Tom Parker <thpr@sourceforge.net>
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
 * Created on June 18, 2005.
 * 
 * Current Ver: $Revision: 1060 $ Last Editor: $Author: boomer70 $ Last Edited:
 * $Date: 2006-06-08 23:25:16 -0400 (Thu, 08 Jun 2006) $
 */
package pcgen.cdom.util;

import java.util.List;
import java.util.Set;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.enumeration.ListKey;

/**
 * @author Tom Parker <thpr@sourceforge.net>
 * 
 * This encapsulates a MapToList in a typesafe way (prior to java 1.5 having the
 * ability to do that with typed Collections)
 */
public class ListKeyMapToList
{

	private final HashMapToList map = new HashMapToList();

	/** Constructor */
	public ListKeyMapToList()
	{
		// Do Nothing
	}

	/**
	 * Add all lists to the map
	 * 
	 * @param lcs
	 */
	public void addAllLists(ListKeyMapToList lcs)
	{
		map.addAllLists(lcs.map);
	}

	/**
	 * @param key
	 * @param list
	 */
	public <T> void addAllToListFor(ListKey<T> key, List<T> list)
	{
		map.addAllToListFor(key, list);
	}

	/**
	 * Add value to a list
	 * 
	 * @param key
	 * @param value
	 */
	public <T> void addToListFor(ListKey<T> key, T value)
	{
		map.addToListFor(key, value);
	}

	/**
	 * Returns true if list contains a value for a key
	 * 
	 * @param key
	 * @return true if list contains a value for a key
	 */
	public boolean containsListFor(ListKey key)
	{
		return map.containsListFor(key);
	}

	/**
	 * Get a list for a key
	 * 
	 * @param key
	 * @return list
	 */
	public <T> List<T> getListFor(ListKey<T> key)
	{
		return map.getListFor(key);
	}

	/**
	 * Get an element in the list
	 * 
	 * @param key
	 * @param i
	 * @return element in list
	 */
	public <T> T getElementInList(ListKey<T> key, int i)
	{
		return (T) map.getElementInList(key, i);
	}

	/**
	 * Remove an item from a list
	 * 
	 * @param key
	 * @param value
	 * @return true, removal ok
	 */
	public <T> boolean removeFromListFor(ListKey<T> key, T value)
	{
		return map.removeFromListFor(key, value);
	}

	/**
	 * Remove a list from a map
	 * 
	 * @param key
	 * @return removed list
	 */
	public <T> List<T> removeListFor(ListKey<T> key)
	{
		return map.removeListFor(key);
	}

	/**
	 * Get size of a list
	 * 
	 * @param key
	 * @return size
	 */
	public int sizeOfListFor(ListKey key)
	{
		return map.sizeOfListFor(key);
	}

	/**
	 * True if value is in a list
	 * 
	 * @param key
	 * @param value
	 * @return True if value is in a list
	 */
	public <T> boolean containsInList(ListKey<T> key, T value)
	{
		return map.containsInList(key, value);
	}

	public Set<ListKey<?>> getKeySet()
	{
		return map.getKeySet();
	}

	@Override
	public int hashCode()
	{
		return map.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof ListKeyMapToList
			&& map.equals(((ListKeyMapToList) o).map);
	}
}
