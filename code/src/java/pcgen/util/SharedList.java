/*
 * SharedList.java
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
 * Created on Mar 6, 2008, 11:38:17 PM
 */
package pcgen.util;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public abstract class SharedList<E> extends AbstractList<E>
{

    private boolean modified = false;
    private List<E> list;

    public SharedList(List<E> list)
    {
        this.list = list;
    }

    @Override
    public boolean add(E o)
    {
        checkModified();
        return list.add(o);
    }

    @Override
    public void add(int index, E o)
    {
        checkModified();
        list.add(index, o);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c)
    {
        checkModified();
        return list.addAll(index, c);
    }

    @Override
    public E remove(int index)
    {
        checkModified();
        return list.remove(index);
    }

    public E get(int index)
    {
        return list.get(index);
    }

    @Override
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
    public boolean contains(Object o)
    {
        return list.contains(o);
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
    public boolean remove(Object o)
    {
        checkModified();
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c)
    {
        checkModified();
        return list.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        checkModified();
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        checkModified();
        return list.retainAll(c);
    }

    @Override
    public void clear()
    {
        checkModified();
        list.clear();
    }

    @Override
    public E set(int index, E element)
    {
        checkModified();
        return list.set(index, element);
    }

    @Override
    public int indexOf(Object o)
    {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o)
    {
        return list.lastIndexOf(o);
    }

    protected abstract List<E> createCopy(List<E> collection);
    
    private void checkModified()
    {
        if (!modified)
        {
            modified = true;
            list = createCopy(list);
        }
    }

}
