/*
 * CompactList.java
 *
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
 * Created on December 16, 2007, 2:12 AM
 */
package pcgen.util;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

/**
 *
 * @author Connor Petty
 */
public class CompactList<E> extends AbstractList<E> 
	implements RandomAccess, Serializable
{

    private List<E> list;
    
    /** Creates a new instance of CompactList */
    public CompactList()
    {
	list = Collections.emptyList();
    }
    
    public CompactList(E element)
    {
	list = Collections.singletonList(element);
    }
    
    public CompactList(E... elements)
    {
	list = new ArrayList<E>(elements.length);
	for(E element : elements)
	    list.add(element);
    }
    
    public CompactList(Collection<? extends E> c)
    {
	this();
	addAll(c);
    }
    public int size()
    {
	return list.size();
    }

    @Override
    public boolean isEmpty()
    {
	return list.isEmpty();
    }

    @Override
    public Object[] toArray()
    {
	return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a)
    {
	return list.toArray(a);
    }

    @Override
    public boolean addAll(Collection<? extends E> c)
    {
	return addAll(size(), c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c)
    {
	rangeCheck0(index);
	int size = size();
	int csize = c.size();
	if (csize == 0)
	{
	    return false;
	}
	else
	{
	    switch (size)
	    {
		case 0:
		    switch (csize)
		    {
			case 1:
			    list = Collections.singletonList(c.iterator().next());
			    break;
			default:
			    list = new ArrayList<E>(c);
		    }
		    break;
		case 1:
		    list = new ArrayList<E>(list);
		default:
		    list.addAll(index, c);
	    }
	    return true;
	}
    }

    @Override
    public void clear()
    {
	list = Collections.emptyList();
    }

    public E get(int index)
    {
	return list.get(index);
    }

    @Override
    public E set(int index, E element)
    {
	rangeCheck1(index);
	switch (size())
	{
	    case 1:
		E old = list.get(0);
		list = Collections.singletonList(element);
		return old;
	    default:
		return list.set(index, element);
	}
    }

    @Override
    public void add(int index, E element)
    {
	rangeCheck0(index);
	switch (size())
	{
	    case 0:
		list = Collections.singletonList(element);
		break;
	    case 1:
		List<E> newlist = new ArrayList<E>(2);
		newlist.add(list.get(0));
		list = newlist;
	    default:
		list.add(index, element);
	}
    }

    @Override
    public E remove(int index)
    {
	rangeCheck1(index);
	E old = list.get(index);
	switch (size())
	{
	    case 1:
		list = Collections.emptyList();
		break;
	    case 2:
		switch (index)
		{
		    case 0:
			list = Collections.singletonList(list.get(1));
			break;
		    case 1:
			list = Collections.singletonList(list.get(0));
			break;
		}
		break;
	    default:
		list.remove(index);
	}
	return old;
    }

    private void rangeCheck0(int index)
    {
	int size = size();
	if (index > size || index < 0)
	{
	    throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
	}
    }

    private void rangeCheck1(int index)
    {
	int size = size();
	if (index > size - 1 || index < 0)
	{
	    throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
	}
    }

}
