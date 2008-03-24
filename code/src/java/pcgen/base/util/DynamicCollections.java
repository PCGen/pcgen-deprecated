/*
 * DynamicCollections.java
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
import java.util.ListIterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public final class DynamicCollections
{

    private DynamicCollections()
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
            Logger.getLogger(DynamicCollections.class.getName()).log(Level.SEVERE,
                                                                     null, ex);
        }
        return null;
    }

    public static <T, C extends Collection<T>> Collection<T> createDynamicCollection(Class<C> sharedClass,
                                                                                      C data)
    {
        return new DynamicCollection<T, C>(sharedClass, data);
    }

    public static <T, C extends List<T>> List<T> createDynamicList(Class<C> sharedClass,
                                                                    C data)
    {
        return new DynamicList<T, C>(sharedClass, data);
    }

    public static <T, C extends Set<T>> Set<T> createDynamicSet(Class<C> sharedClass,
                                                                 C data)
    {
        return new DynamicSet<T, C>(sharedClass, data);
    }

    private static class DynamicCollection<E, C extends Collection<E>> implements Collection<E>
    {

        protected final Class<C> copyClass;
        protected C collection;
        private boolean modified = false;

        public DynamicCollection(Class<C> copyClass, C collection)
        {
            this.copyClass = copyClass;
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
            return new DynamicCollectionIterator();
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

        protected final void checkModified()
        {
            if (!modified)
            {
                modified = true;
                C copy = createInstance(copyClass);
                copy.addAll(collection);
                collection = copy;
            }
        }

        private class DynamicCollectionIterator implements Iterator<E>
        {

            private final Iterator<E> it = collection.iterator();
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
                DynamicCollection.this.remove(element);
            }

        }
    }

    private static class DynamicSet<E, C extends Set<E>> extends DynamicCollection<E, C> implements Set<E>
    {

        public DynamicSet(Class<C> copyClass, C set)
        {
            super(copyClass, set);
        }

    }

    private static class DynamicList<E, C extends List<E>> extends DynamicCollection<E, C> implements List<E>
    {

        public DynamicList(Class<C> copyClass, C list)
        {
            super(copyClass, list);
        }

        public boolean addAll(int index, Collection<? extends E> c)
        {
            checkModified();
            return collection.addAll(index, c);
        }

        public E get(int index)
        {
            return collection.get(index);
        }

        public E set(int index, E element)
        {
            checkModified();
            return collection.set(index, element);
        }

        public void add(int index, E element)
        {
            checkModified();
            collection.add(index, element);
        }

        public E remove(int index)
        {
            checkModified();
            return collection.remove(index);
        }

        public int indexOf(Object o)
        {
            return collection.indexOf(o);
        }

        public int lastIndexOf(Object o)
        {
            return collection.lastIndexOf(o);
        }

        public ListIterator<E> listIterator()
        {
            return new DynamicListIterator();
        }

        public ListIterator<E> listIterator(int index)
        {
            return new DynamicListIterator(index);
        }

        public List<E> subList(int fromIndex, int toIndex)
        {
            return new DynamicSubList<E>(this, fromIndex, toIndex);
        }

        private class DynamicListIterator implements ListIterator<E>
        {

            private ListIterator<E> it;
            private int index = -1;

            public DynamicListIterator()
            {
                it = collection.listIterator();
            }

            public DynamicListIterator(int index)
            {
                it = collection.listIterator(index);
            }

            public boolean hasPrevious()
            {
                return it.hasPrevious();
            }

            public E previous()
            {
                index = previousIndex();
                return it.previous();
            }

            public int nextIndex()
            {
                return it.nextIndex();
            }

            public int previousIndex()
            {
                return it.previousIndex();
            }

            public void set(E o)
            {
                DynamicList.this.set(index, o);
                it = collection.listIterator(index);
            }

            public void add(E o)
            {
                DynamicList.this.add(index, o);
                it = collection.listIterator(index);
                index = -1;
            }

            public boolean hasNext()
            {
                return it.hasNext();
            }

            public E next()
            {
                index = nextIndex();
                return it.next();
            }

            public void remove()
            {
                DynamicList.this.remove(index);
                it = collection.listIterator(index);
                index = -1;
            }

        }

        private static class DynamicSubList<E> extends AbstractList<E>
        {

            private final DynamicList<E, ? extends List<E>> list;
            private final int offset;
            private int size;

            public DynamicSubList(DynamicList<E, ? extends List<E>> list, int fromIndex, int toIndex)
            {
                this.list = list;
                this.offset = fromIndex;
                this.size = toIndex - fromIndex;
            }

            public int size()
            {
                return size;
            }

            public E get(int index)
            {
                return list.get(index + offset);
            }

            @Override
            public E set(int index, E element)
            {
                if (index < 0 || index >= size)
                {
                    throw new IndexOutOfBoundsException("Index: " + index +
                                                        ",Size: " + size);
                }
                return list.set(index + offset, element);
            }

            @Override
            public void add(int index, E element)
            {
                if (index < 0 || index > size)
                {
                    throw new IndexOutOfBoundsException("Index: " + index +
                                                        ",Size: " + size);
                }
                list.add(index + offset, element);
                size++;
            }

            @Override
            public E remove(int index)
            {
                if (index < 0 || index >= size)
                {
                    throw new IndexOutOfBoundsException("Index: " + index +
                                                        ",Size: " + size);
                }
                E element = list.remove(index + offset);
                size--;
                return element;
            }

        }
    }
}
