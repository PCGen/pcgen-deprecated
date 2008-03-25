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

import java.util.Collection;
import java.util.IdentityHashMap;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * InstanceHashSet is an implementation of the Set Interface that uses an
 * IdentityHashMap as the internal representation of the Set.
 */
public class IdentityHashSet<T> extends WrappedMapSet<T>
{
	private static final Class<IdentityHashMap> IDENTITY_HASH_MAP = IdentityHashMap.class;

	/**
	 * Construct a new, empty InstanceHashSet
	 */
	public IdentityHashSet()
	{
		super(IDENTITY_HASH_MAP);
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

}
