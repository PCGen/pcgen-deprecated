/*
 * HashListMap.java
 * Copyright 2008 (C) Connor Petty <mistercpp2000@gmail.com>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Created on Feb 3, 2008, 1:30:21 AM
 */
package pcgen.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public class HashListMap<K, V> extends AbstractListMap<K, V, CompactList<V>>
{

    private Map<K, CompactList<V>> map = new HashMap<K, CompactList<V>>();

    @Override
    public void add(K key, int index, V value)
    {
	CompactList<V> list = get(key);
	if (list == null)
	{
	    map.put(key, list = new CompactList<V>());
	}
	list.add(index, value);
    }

    @Override
    public boolean containsKey(Object key)
    {
	return map.containsKey(key);
    }

    @Override
    public CompactList<V> get(Object key)
    {
	Collection<V> c = map.get(key);
	return c == null ? null : new CompactList<V>(c);
    }

    @Override
    public CompactList<V> put(K key, CompactList<V> c)
    {
	if (c == null)
	{
	    return remove(key);
	}
	return map.put(key, new CompactList<V>(c));
    }

    @Override
    public void clear()
    {
	map.clear();
    }

    @Override
    public Set<K> keySet()
    {
	return new HashSet<K>(map.keySet());
    }

    @Override
    public Set<Entry<K, CompactList<V>>> entrySet()
    {
	return new HashSet<Entry<K, CompactList<V>>>(map.entrySet());
    }

    @Override
    public int size()
    {
	return map.size();
    }

    @Override
    public CompactList<V> remove(Object key)
    {
	return map.remove(key);
    }

    @Override
    public V remove(Object key, int index)
    {
	List<V> list = get(key);
	if (list != null)
	{
	    V value = list.remove(index);
	    if (list.isEmpty())
	    {
		map.remove(key);
	    }
	    return value;
	}

	return null;
    }

}
