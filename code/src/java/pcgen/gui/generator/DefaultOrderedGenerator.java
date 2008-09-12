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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class DefaultOrderedGenerator<E> extends AbstractGenerator<E>
{

    protected List<E> items;
    private List<E> temp = null;
    protected boolean randomOrder;
    private int index;

    public DefaultOrderedGenerator(String name, List<E> items, boolean randomOrder)
    {
        super(name);
        this.items = items;
        this.randomOrder = randomOrder;
        if (randomOrder)
        {
            temp = new ArrayList<E>(items);
        }
        reset();
    }

    public E getNext()
    {
        List<E> list = randomOrder ? temp : items;
        if (list.isEmpty())
        {
            return null;
        }
        if (index == list.size())
        {
            reset();
        }
        return list.get(index++);
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

    @Override
    public void reset()
    {
        index = 0;
        if (randomOrder)
        {
            if (temp == null)
            {
                temp = new ArrayList<E>(items);
            }
            Collections.shuffle(temp);
        }
    }

}
