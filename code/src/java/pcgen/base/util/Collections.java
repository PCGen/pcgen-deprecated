/*
 * Collections.java
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
 * Created on Mar 19, 2008, 3:12:06 PM
 */
package pcgen.base.util;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public final class Collections
{

    private Collections()
    {
    }

    private static <C> C createInstance(Class<C> c)
    {
        try
        {
            return c.newInstance();
        }
        catch (Exception ex)
        {
            Logger.getLogger(Collections.class.getName()).log(Level.SEVERE,
                                                                    null, ex);
        }
        return null;
    }

    public static <T, C extends Collection<T>> Collection<T> createSharedCollection(Class<C> sharedClass,
                                                                                      C data)
    {
        return new BasicSharedCollection<T, C>(sharedClass, data);
    }

    public static <T, C extends List<T>> List<T> createSharedList(Class<C> sharedClass,
                                                                    C data)
    {
        return new BasicSharedList<T, C>(sharedClass, data);
    }

    public static <T, C extends Set<T>> Set<T> createSharedSet(Class<C> sharedClass,
                                                                 C data)
    {
        return new BasicSharedSet<T, C>(sharedClass, data);
    }

    private static class BasicSharedSet<T, C extends Set<T>> extends BasicSharedCollection<T, C>
            implements Set<T>
    {

        public BasicSharedSet(Class<C> sharedClass, C collection)
        {
            super(sharedClass, collection);
        }

    }

    private static class BasicSharedList<T, C extends List<T>> extends SharedList<T>
    {

        private final Class<C> sharedClass;

        public BasicSharedList(Class<C> sharedClass, C collection)
        {
            super(collection);
            this.sharedClass = sharedClass;
        }

        @Override
        protected List<T> createCopy(List<T> collection)
        {
            C copy = createInstance(sharedClass);
            copy.addAll(collection);
            return copy;
        }

    }

    private static class BasicSharedCollection<T, C extends Collection<T>>
            extends SharedCollection<T>
    {

        private final Class<C> sharedClass;

        public BasicSharedCollection(Class<C> sharedClass, C collection)
        {
            super(collection);
            this.sharedClass = sharedClass;
        }

        @Override
        protected Collection<T> createCopy(Collection<T> collection)
        {
            C copy = createInstance(sharedClass);
            copy.addAll(collection);
            return copy;
        }

    }

    private static abstract class SharedCollection<E> implements Collection<E>
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

    private static abstract class SharedList<E> extends AbstractList<E>
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
}
