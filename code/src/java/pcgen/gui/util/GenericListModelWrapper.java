/*
 * GenericListModelWrapper.java
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
 * Created on Jul 17, 2008, 9:18:26 PM
 */
package pcgen.gui.util;

import java.util.AbstractList;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class GenericListModelWrapper<E> extends AbstractList<E>
{

    private GenericListModel<E> model;

    public GenericListModelWrapper(GenericListModel<E> model)
    {
        this.model = model;
    }

    @Override
    public E get(int index)
    {
        return model.getElementAt(index);
    }

    @Override
    public int size()
    {
        return model.getSize();
    }

}
