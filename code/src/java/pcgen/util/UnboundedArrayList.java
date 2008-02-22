/*
 * UnboundedArrayList.java
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
 * Created on Feb 21, 2008, 12:45:18 AM
 */
package pcgen.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public class UnboundedArrayList<E> extends ArrayList<E>
{

    public UnboundedArrayList()
    {
        super();
    }

    public UnboundedArrayList(int capacity)
    {
        super(capacity);
    }

    public UnboundedArrayList(Collection<? extends E> collection)
    {
        super(collection);
    }

    @Override
    public void add(int index, E element)
    {
        try
        {
            super.add(index, element);
        }
        catch (IndexOutOfBoundsException e)
        {
            if (index < 0)
            {
                throw e;
            }
            while (size() < index)
            {
                add(null);
            }
            add(element);
        }
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> collection)
    {
        try
        {
            return super.addAll(index, collection);
        }
        catch (IndexOutOfBoundsException e)
        {
            if (index < 0)
            {
                throw e;
            }
            while (size() < index)
            {
                add(null);
            }
            return addAll(index, collection);
        }
    }

    @Override
    public E get(int index)
    {
        try
        {
            return super.get(index);
        }
        catch (IndexOutOfBoundsException e)
        {
            if (index < 0)
            {
                throw e;
            }
            return null;
        }
    }

    @Override
    public E set(int index, E element)
    {
        try
        {
            return super.set(index, element);
        }
        catch (IndexOutOfBoundsException e)
        {
            if (index < 0)
            {
                throw e;
            }
            while (size() < index)
            {
                add(null);
            }
            add(element);
            return null;
        }
    }

    @Override
    public E remove(int index)
    {
        try
        {
            return super.remove(index);
        }
        catch (IndexOutOfBoundsException e)
        {
            if (index < 0)
            {
                throw e;
            }
            return null;
        }
    }

}
