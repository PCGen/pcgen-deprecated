/*
 * TreeViewModelEvent.java
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
 * Created on Feb 29, 2008, 10:54:37 PM
 */
package pcgen.gui.util.event;

import java.util.Collection;
import java.util.EventObject;
import pcgen.gui.util.treeview.TreeViewModel;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public class TreeViewModelEvent<E> extends EventObject
{

    private Collection<E> oldData;
    private Collection<E> newData;

    public TreeViewModelEvent(TreeViewModel<E> source, Collection<E> oldData,
                               Collection<E> newData)
    {
        super(source);
        this.oldData = oldData;
        this.newData = newData;
    }

    public Collection<E> getOldData()
    {
        return oldData;
    }

    public Collection<E> getNewData()
    {
        return newData;
    }

}
