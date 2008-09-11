/*
 * DefaultMutableGenerator.java
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
 * Created on Aug 26, 2008, 5:11:13 PM
 */
package pcgen.gui.generator;

import java.util.Collections;
import java.util.List;
import java.util.Vector;
import pcgen.base.util.RandomUtil;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class DefaultMutableGenerator<E> extends AbstractGenerator<E> implements MutableGenerator<E>
{

    private final Vector<E> vector = new Vector<E>();

    public DefaultMutableGenerator(String name)
    {
        super(name);
    }

    public E getNext()
    {
        if (vector.isEmpty())
        {
            return null;
        }
        return vector.get(RandomUtil.getRandomInt(vector.size()));
    }

    @Override
    public List<E> getAll()
    {
        return Collections.unmodifiableList(vector);
    }

    public void add(E element)
    {
        vector.add(element);
    }

    public void remove(E element)
    {
        vector.remove(element);
    }

}
