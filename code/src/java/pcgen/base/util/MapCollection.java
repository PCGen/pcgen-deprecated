/*
 * Copyright 2006 (C) Tom Parker <thpr@sourceforge.net>
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
 * Created on Oct 31, 2006
 */
package pcgen.base.util;

import java.util.AbstractCollection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * MapCollection acts as a Facade on a Map to make it act as a Collection. This
 * is useful if you wish to iterate over both the keys and values of a Map.
 * 
 * Note: MapCollection acts as a Facade - that means the underlying Map is NOT
 * copied. Changes to the underlying Map are reflected in the MapCollection.
 * This also means that changes to the underlying Map may cause an Iterator of
 * the MapCollection to fail due to a ConcurrentModificationException.
 * 
 * **WARNING** This Class is known to NOT FAIL FAST. DO NOT rely on fail fast
 * behavior of the Iterator of MapCollection to detect changes to the underlying
 * Map.
 */
public class MapCollection extends AbstractCollection<Object>
{

	/**
	 * The Map over which this MapCollection is acting as a Facade.
	 */
	private final Map<?, ?> map;

	/**
	 * Creates a new MapCollection facade to the given Map.
	 * 
	 * @param m
	 *            The Map this MapCollection converts to a Collection
	 */
	public MapCollection(Map<?, ?> m)
	{
		super();
		if (m == null)
		{
			throw new IllegalArgumentException(
				"Cannot provide null to MapCollection");
		}
		map = new HashMap<Object, Object>(m);
	}

	/**
	 * Clear is an unsupported operation on a MapCollection
	 */
	@Override
	public void clear()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns true if the underlying Map contains the given Object as either a
	 * Key or a Value.
	 * 
	 * @param arg0
	 *            The object to be tested to see if it is contained in the
	 *            underlying map
	 * @return true if the underlying Map contains the given Object as either a
	 *         Key or a Value; false otherwise
	 */
	@Override
	public boolean contains(Object arg0)
	{
		return map.containsKey(arg0) || map.containsValue(arg0);
	}

	/**
	 * Returns true if the underlying Map is empty; false otherwise.
	 * 
	 * @return true if the underlying Map is empty; false otherwise
	 */
	@Override
	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	/**
	 * Returns an Iterator over the Keys and Values of the underlying Map. The
	 * Iterator will repeatedly return a Key followed by the value associated
	 * with that Key in the Map.
	 */
	@Override
	public Iterator<Object> iterator()
	{
		return new MapCollectionIterator(map);
	}

	/**
	 * Remove is not a supported operation for a MapCollection
	 */
	@Override
	public boolean remove(Object arg0)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the size of this MapCollection. This size is twice the size of
	 * the underlying Map
	 */
	@Override
	public int size()
	{
		return map.size() << 1;
	}

	/**
	 * The Iterator used to iterate over the underlying Map
	 * 
	 * This Class is known to NOT FAIL FAST. DO NOT rely on fail fast behavior
	 * of the Iterator of MapCollection to detect changes to the underlying Map.
	 */
	private static class MapCollectionIterator implements Iterator<Object>
	{
		/**
		 * The current entry which this MapCollectionIterator is iterating over
		 */
		private Entry<?, ?> workingEntry;

		/**
		 * Indicates if the key for the workingEntry has been returned by this
		 * Iterator
		 */
		private boolean returnedKey = false;

		@SuppressWarnings("unchecked")
		private final Iterator hashIterator;

		/**
		 * Constructs a new MapCollectionIterator to iterate over the given Map
		 * 
		 * @param m
		 *            The Map this MapCollectionIterator will iterate over
		 */
		MapCollectionIterator(Map<?, ?> m)
		{
			hashIterator = m.entrySet().iterator();
		}

		/**
		 * Indicates if this Iterator has an additional value
		 * 
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext()
		{
			return returnedKey || hashIterator.hasNext();
		}

		/**
		 * Not supported by MapCollectionIterator
		 * 
		 * @see java.util.Iterator#remove()
		 * 
		 * @throws UnsupportedOperationException
		 *             if called
		 */
		public void remove()
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * Returns the next key or value from the underlying Map.
		 * 
		 * @see java.util.Iterator#next()
		 */
		public Object next()
		{
			if (returnedKey)
			{
				returnedKey = false;
				return workingEntry.getValue();
			}
			else
			{
				workingEntry = (Entry<?, ?>) hashIterator.next();
				returnedKey = true;
				return workingEntry.getKey();
			}
		}
	}
}
