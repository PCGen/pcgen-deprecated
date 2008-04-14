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

import java.lang.reflect.Constructor;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a utility class that create dynamic collections.
 * Dynamic collections are modifyable "wrappers" for collections that
 * are not meant to be modified. These dynamic collections will
 * reference the provided collection for all read-only operations.
 * The first time a write operation takes place, a copy of the underlying
 * collection is made and the write operation is carryed out on that copy.
 * Thus the original collection remains unmodified. Further read and write 
 * operations will take placd on the copied collection.
 * <br>
 * These dynamic wrappers are an efficient way to guarentee the safety of 
 * reference-semantic collections when those collections are meant to be passed
 * around and viewed by other classes.
 * <br>
 * The dynamic allocation behavior of a dynamic collection is thread safe.
 * However, this does not mean that read and write operations to the underlying
 * collection are thread safe. Dynamic collections are only as thread safe as
 * the underlying collection.
 * <br>
 * Unlike java.util.concurrent.CopyOnWriteArrayList, Dynamic Collections will
 * only make a single copy of the original collection. Even then, that copy is
 * only made when a write operation takes place. If a dynamic collection is never
 * modified, then a copy is never created. On the other hand, a CopyOnWriteArrayList
 * will make an initial copy of the collection upon creation and will make additional
 * copies each time a write operation takes place.
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

    /**
     * Creates a dynamic collection backed by the <code>data</code> parameter.
     * The class that is provided as a paramter must have a no-args
     * constructor, otherwise an IllegalArgumentException will be thrown.
     * @param copyClass the class of the <code>data</code> parameter
     * @param data the set that will be wrapped by a dynamic collection
     * @return a Dynamic collection backed by the <code>data</code> parameter
     */
    public static <T, C extends Collection<T>> Collection<T> createDynamicCollection(Class<? extends C> copyClass,
                                                                                       C data)
    {
        try
        {
            Constructor<? extends C> c = copyClass.getConstructor((Class<?>) null);
            return new DynamicCollection<T, C>(c, data);
        }
        catch (Exception ex)
        {
            throw new IllegalArgumentException("copyClass must have a public no-arg constructor",
                                               ex);
        }
    }

    /**
     * Creates a dynamic list backed by the <code>data</code> parameter.
     * The class that is provided as a paramter must have a no-args
     * constructor, otherwise an IllegalArgumentException will be thrown.
     * @param copyClass the class of the <code>data</code> parameter
     * @param data the set that will be wrapped by a dynamic list
     * @return a dynamic list backed by the <code>data</code> parameter
     */
    public static <T, C extends List<T>> List<T> createDynamicList(Class<? extends C> copyClass,
                                                                     C data)
    {
        try
        {
            Constructor<? extends C> c = copyClass.getConstructor((Class<?>) null);
            return new DynamicList<T, C>(c, data);
        }
        catch (Exception ex)
        {
            throw new IllegalArgumentException("copyClass must have a public no-arg constructor",
                                               ex);
        }
    }

    /**
     * Creates a dynamic set backed by the <code>data</code> parameter.
     * The class that is provided as a paramter must have a no-args
     * constructor, otherwise an IllegalArgumentException will be thrown.
     * @param copyClass the class of the <code>data</code> parameter
     * @param data the set that will be wrapped by a Dynamic Set
     * @return a Dynamic Set backed by the <code>data</code> parameter
     */
    public static <T, C extends Set<T>> Set<T> createDynamicSet(Class<? extends C> copyClass,
                                                                  C data)
    {
        try
        {
            Constructor<? extends C> c = copyClass.getConstructor((Class<?>) null);
            return new DynamicSet<T, C>(c, data);
        }
        catch (Exception ex)
        {
            throw new IllegalArgumentException("copyClass must have a public no-arg constructor",
                                               ex);
        }
    }

    private static class DynamicSupport<E, C extends Collection<E>>
    {

        private final Constructor<? extends C> constructor;
        private volatile boolean modified = false;
        private volatile C collection;

        public DynamicSupport(Constructor<? extends C> constructor,
                               C collection)
        {
            this.constructor = constructor;
            this.collection = collection;
        }

        public C getCollection()
        {
            return collection;
        }

        public void checkModified()
        {
            if (!modified)
            {
                synchronized (this)
                {
                    if (!modified)
                    {
                        modified = true;
                        C copy = createInstance(constructor.getDeclaringClass());
                        copy.addAll(collection);
                        collection = copy;
                    }
                }
            }
        }

    }

    private static class DynamicCollection<E, C extends Collection<E>>
            implements Collection<E>
    {

        private final DynamicSupport<E, C> support;

        public DynamicCollection(Constructor<? extends C> copyClass,
                                  C collection)
        {
            this.support = new DynamicSupport<E, C>(copyClass, collection);
        }

        public final int size()
        {
            return support.getCollection().size();
        }

        public final boolean isEmpty()
        {
            return support.getCollection().isEmpty();
        }

        public final boolean contains(Object o)
        {
            return support.getCollection().contains(o);
        }

        public Iterator<E> iterator()
        {
            return new DynamicCollectionIterator();
        }

        public final Object[] toArray()
        {
            return support.getCollection().toArray();
        }

        public final <T> T[] toArray(T[] a)
        {
            return support.getCollection().toArray(a);
        }

        public final boolean add(E o)
        {
            support.checkModified();
            return support.getCollection().add(o);
        }

        public final boolean remove(Object o)
        {
            support.checkModified();
            return support.getCollection().remove(o);
        }

        public final boolean containsAll(Collection<?> c)
        {
            support.checkModified();
            return support.getCollection().containsAll(c);
        }

        public final boolean addAll(Collection<? extends E> c)
        {
            support.checkModified();
            return support.getCollection().addAll(c);
        }

        public final boolean removeAll(Collection<?> c)
        {
            support.checkModified();
            return support.getCollection().removeAll(c);
        }

        public final boolean retainAll(Collection<?> c)
        {
            support.checkModified();
            return support.getCollection().retainAll(c);
        }

        public final void clear()
        {
            support.checkModified();
            support.getCollection().clear();
        }

        private class DynamicCollectionIterator implements Iterator<E>
        {

            private final Iterator<E> it = support.getCollection().iterator();
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

    private static class DynamicSet<E, C extends Set<E>> extends DynamicCollection<E, C>
            implements Set<E>
    {

        public DynamicSet(Constructor<? extends C> copyClass, C set)
        {
            super(copyClass, set);
        }

    }

    private static class DynamicList<E, C extends List<E>> extends AbstractList<E>
    {

        private final DynamicSupport<E, C> support;

        public DynamicList(Constructor<? extends C> copyClass, C list)
        {
            this.support = new DynamicSupport<E, C>(copyClass, list);
        }

        @Override
        public void add(int index, E element)
        {
            support.checkModified();
            support.getCollection().add(index, element);
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c)
        {
            support.checkModified();
            return support.getCollection().addAll(index, c);
        }

        @Override
        public E remove(int index)
        {
            support.checkModified();
            return support.getCollection().remove(index);
        }

        @Override
        public E set(int index, E element)
        {
            support.checkModified();
            return support.getCollection().set(index, element);
        }

        @Override
        public E get(int index)
        {
            return support.getCollection().get(index);
        }

        @Override
        public int size()
        {
            return support.getCollection().size();
        }

    }
}
