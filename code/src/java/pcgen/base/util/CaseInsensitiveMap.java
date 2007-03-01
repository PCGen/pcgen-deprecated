/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.HashMap;

import pcgen.base.lang.CaseInsensitiveString;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * A DefaultMap is a HashMap that has uses a CaseInsensitiveString as the Key
 * 
 * @param <V>
 *            The Type of the Values stored in this CaseInsensitiveMap
 */
public class CaseInsensitiveMap<V> extends HashMap<CaseInsensitiveString, V>
{

	private Object resolveObject(Object arg0)
	{
		return arg0 instanceof String
			? new CaseInsensitiveString((String) arg0) : arg0;
	}

	@Override
	public boolean containsKey(Object arg0)
	{
		return super.containsKey(resolveObject(arg0));
	}

	@Override
	public V get(Object arg0)
	{
		return super.get(resolveObject(arg0));
	}

	public V put(String arg0, V arg1)
	{
		return super.put(new CaseInsensitiveString(arg0), arg1);
	}

	@Override
	public V remove(Object arg0)
	{
		return super.remove(resolveObject(arg0));
	}
}
