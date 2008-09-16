/*
 * DefaultOrderedGenerator.java
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Sep 11, 2008, 1:51:46 PM
 */
package pcgen.gui.generator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class DefaultOrderedGenerator<E> extends AbstractGenerator<E>
{

    protected List<E> items;
    private Queue<E> queue = null;
    protected boolean randomOrder;

    public DefaultOrderedGenerator(String name, List<E> items,
                                    boolean randomOrder)
    {
        super(name);
        this.items = items;
        this.randomOrder = randomOrder;
        reset();
    }

    public E getNext()
    {
        if (items.isEmpty())
        {
            return null;
        }
        if (queue.isEmpty())
        {
            reset();
        }
        return queue.poll();
    }

    public boolean isRandomOrder()
    {
        return randomOrder;
    }

    @Override
    public List<E> getAll()
    {
        return items;
    }

    protected Queue<E> createQueue()
    {
        LinkedList<E> temp = new LinkedList<E>(items);
        if (randomOrder)
        {
            Collections.shuffle(temp);
        }
        return temp;
    }

    @Override
    public void reset()
    {
        queue = createQueue();
    }

}
