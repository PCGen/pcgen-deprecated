/*
 * AbstractGenericListDataListener.java
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
 * Created on Jul 15, 2008, 8:14:59 PM
 */
package pcgen.gui.util.event;

import pcgen.gui.util.GenericListModel;
import pcgen.gui.util.GenericListModelWrapper;

/**
 * This class is only effective if the subclass does not call <code>getData()</code>
 * in its implementation of the <code>intervalAdded()</code> method. 
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public abstract class AbstractGenericListDataListener<E> implements GenericListDataListener<E>
{

    protected GenericListModelWrapper<E> wrapper = null;

    public AbstractGenericListDataListener()
    {
    }

    public AbstractGenericListDataListener(GenericListModel<E> model)
    {
        setModel(model);
    }

    public void setModel(GenericListModel<E> model)
    {
        if (wrapper != null)
        {
            wrapper.getModel().removeGenericListDataListener(this);
        }
        wrapper = new GenericListModelWrapper<E>(model);
        model.addGenericListDataListener(this);
    }

    public void contentsChanged(GenericListDataEvent<E> e)
    {
        intervalRemoved(e);
        intervalAdded(new GenericListDataEvent<E>(e.getSource(),
                                                  wrapper.subList(e.getIndex0(),
                                                                  e.getIndex1() +
                                                                  1),
                                                  e.getValueIsAdjusting(),
                                                  e.getType(), e.getIndex0(),
                                                  e.getIndex1()));
    }

}
