/*
 * SharedCollection.java
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
 * Created on Mar 6, 2008, 11:09:26 PM
 */
package pcgen.util;

import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public abstract class SharedCollection<E> implements Collection<E>
{

    private boolean modified = false;
    private Collection<E> collection;

    public SharedCollection(Collection<E> collection)
    {
        this.collection = collection;
    }

    public final int size()
    {
        return collection.size();
    }

    public final boolean isEmpty()
    {
        return collection.isEmpty();
    }

    public final boolean contains(Object o)
    {
        return collection.contains(o);
    }

    public Iterator<E> iterator()
    {
        return new SharedIterator();
    }

    public final Object[] toArray()
    {
        return collection.toArray();
    }

    public final <T> T[] toArray(T[] a)
    {
        return collection.toArray(a);
    }

    public final boolean add(E o)
    {
        checkModified();
        return collection.add(o);
    }

    public final boolean remove(Object o)
    {
        checkModified();
        return collection.remove(o);
    }

    public final boolean containsAll(Collection<?> c)
    {
        checkModified();
        return collection.containsAll(c);
    }

    public final boolean addAll(Collection<? extends E> c)
    {
        checkModified();
        return collection.addAll(c);
    }

    public final boolean removeAll(Collection<?> c)
    {
        checkModified();
        return collection.removeAll(c);
    }

    public final boolean retainAll(Collection<?> c)
    {
        checkModified();
        return collection.retainAll(c);
    }

    public final void clear()
    {
        checkModified();
        collection.clear();
    }

    protected abstract Collection<E> createCopy(Collection<E> collection);

    protected final void checkModified()
    {
        if (!modified)
        {
            modified = true;
            collection = createCopy(collection);
        }
    }

    protected class SharedIterator implements Iterator<E>
    {

        private Iterator<E> it = collection.iterator();
        private E element = null;

        public boolean hasNext()
        {
            return it.hasNext();
        }

        public E next()
        {
            return element = it.next();
        }

        public void remove()
        {
            SharedCollection.this.remove(element);
        }

    }
}
