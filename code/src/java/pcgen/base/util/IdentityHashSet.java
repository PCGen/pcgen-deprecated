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
 * 
 * Created on June 30, 2007.
 */
package pcgen.base.util;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * InstanceHashSet is an implementation of the Set Interface that uses an
 * IdentityHashMap as the internal representation of the Set.
 */
public class IdentityHashSet<T> extends AbstractSet<T> implements Set<T>
{

	/**
	 * An Object used to test for the presence of an object in the Set. This is
	 * basically a tradeoff between a tiny amount of memory (enough to store an
	 * Object) and speed in calculating the return value of add and remove.
	 */
	private static final Object PRESENCE = new Object();

	/**
	 * The underlying map for this IdentityHashSet
	 */
	private final IdentityHashMap<T, Object> map =
			new IdentityHashMap<T, Object>();

	/**
	 * Construct a new, empty InstanceHashSet
	 */
	public IdentityHashSet()
	{
		// Nothing to do here
	}

	/**
	 * Construct a new InstanceHashSet that contains the objects in the given
	 * Collection
	 * 
	 * @param c
	 *            The Collection used to initialize the contents of this
	 *            InstanceHashSet
	 */
	public IdentityHashSet(Collection<? extends T> c)
	{
		this();
		addAll(c);
	}

	/**
	 * Returns the number of Objects in this Set
	 */
	@Override
	public int size()
	{
		return map.size();
	}

	/**
	 * Returns an Iterator over the Set
	 */
	@Override
	public Iterator<T> iterator()
	{
		return map.keySet().iterator();
	}

	/**
	 * Adds the given Object to this set if it was not already part of the Set.
	 * returns true if the Object was added to the Set; false otherwise.
	 * 
	 * @param obj
	 *            The Object to be added to this Set.
	 * @return true if the Object was added to the Set; false otherwise
	 */
	@Override
	public boolean add(T obj)
	{
		return map.put(obj, PRESENCE) == null;
	}

	/**
	 * Clears this Set (removes all Objects from the Set)
	 */
	@Override
	public void clear()
	{
		map.clear();
	}

	/**
	 * Returns true if the given object is present in this Set.
	 * 
	 * @param obj
	 *            The Object to be tested
	 * @return true if the Object is present in this Set; false otherwise
	 */
	@Override
	public boolean contains(Object obj)
	{
		return map.containsKey(obj);
	}

	/**
	 * Returns true if this Set is Empty.
	 * 
	 * @return true if the this Set is Empty; false otherwise
	 */
	@Override
	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	/**
	 * Removes the given Object from this Set. Returns true if the object was
	 * removed, false otherwise.
	 * 
	 * @param obj
	 *            The Object to be removed from this Set.
	 * @return true if the Object was removed from the Set; false otherwise
	 */
	@Override
	public boolean remove(Object obj)
	{
		return map.remove(obj) == PRESENCE;
	}

}
