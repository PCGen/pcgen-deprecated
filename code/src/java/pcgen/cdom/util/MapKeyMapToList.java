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
package pcgen.cdom.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import pcgen.base.util.DoubleKeyMap;
import pcgen.cdom.enumeration.MapKey;

public class MapKeyMapToList
{

	private final DoubleKeyMap<MapKey<?, ?>, Object, List<Object>> dkm;

	public MapKeyMapToList()
	{
		dkm = new DoubleKeyMap<MapKey<?, ?>, Object, List<Object>>();
	}

	public MapKeyMapToList(MapKeyMapToList other)
	{
		dkm = new DoubleKeyMap<MapKey<?, ?>, Object, List<Object>>(other.dkm);
	}

	public void addAll(MapKeyMapToList other)
	{
		dkm.putAll(other.dkm);
	}

	public <SK> boolean containsKey(MapKey<SK, ?> key1, SK key2)
	{
		return dkm.containsKey(key1, key2);
	}

	public boolean containsKey(MapKey<?, ?> key1)
	{
		return dkm.containsKey(key1);
	}

	public <SK, SV> List<SV> getListFor(MapKey<SK, SV> key1, SK key2)
	{
		List<SV> list = (List<SV>) dkm.get(key1, key2);
		if (list == null)
		{
			return null;
		}
		return new ArrayList<SV>(list);
	}

	public <SK, SV> boolean addToListFor(MapKey<SK, SV> key1, SK key2, SV value)
	{
		List<Object> list = dkm.get(key1, key2);
		if (list == null)
		{
			list = new ArrayList<Object>();
			dkm.put(key1, key2, list);
		}
		return list.add(value);
	}

	public <SK, SV> List<SV> removeListFor(MapKey<SK, SV> key1, SK key2)
	{
		return (List<SV>) dkm.remove(key1, key2);
	}

	public <SK> Set<SK> getKeySet(MapKey<SK, ?> key1)
	{
		return (Set<SK>) dkm.getSecondaryKeySet(key1);
	}

}
