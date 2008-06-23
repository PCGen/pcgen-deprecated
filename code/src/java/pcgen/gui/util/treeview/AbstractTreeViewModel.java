/*
 * AbstractTreeViewModel.java
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
 * Created on Feb 29, 2008, 11:38:40 PM
 */
package pcgen.gui.util.treeview;

import java.util.Collection;
import java.util.Collections;
import javax.swing.event.EventListenerList;
import pcgen.gui.util.event.TreeViewModelEvent;
import pcgen.gui.util.event.TreeViewModelListener;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public abstract class AbstractTreeViewModel<E> implements TreeViewModel<E>
{

    protected AbstractTreeViewModel()
    {
        this(null);
    }

    protected AbstractTreeViewModel(Collection<E> data)
    {
        this.data = data;
    }

    protected final EventListenerList listenerList = new EventListenerList();
    private Collection<E> data;

    public final void addTreeViewModelListener(TreeViewModelListener<E> listener)
    {
        listenerList.add(TreeViewModelListener.class, listener);
    }

    public final void removeTreeViewModelListener(TreeViewModelListener<E> listener)
    {
        listenerList.remove(TreeViewModelListener.class, listener);
    }

    @SuppressWarnings("unchecked")
    protected final void fireDataChanged(Collection<E> oldData,
                                           Collection<E> newData)
    {
        TreeViewModelEvent<E> event = null;
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == TreeViewModelListener.class)
            {
                // Lazily create the event:
                if (event == null)
                {
                    event = new TreeViewModelEvent<E>(this, oldData, newData);
                }
                ((TreeViewModelListener<E>) listeners[i + 1]).dataChanged(event);
            }
        }
    }

    public final Collection<E> getData()
    {
        if (data == null)
        {
            return Collections.emptySet();
        }
        else
        {
            return data;
        }
    }

    protected final void setData(Collection<E> data)
    {
        Collection<E> oldData = this.data;
        this.data = data;
        fireDataChanged(oldData, data);
    }

}
