/*
 * TreeViewModelWrapper.java
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
 * Created on Jun 23, 2008, 3:53:14 PM
 */
package pcgen.gui.util.treeview;

import java.util.List;
import pcgen.gui.util.event.TreeViewModelEvent;
import pcgen.gui.util.event.TreeViewModelListener;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class TreeViewModelWrapper<E> extends AbstractTreeViewModel<E>
        implements TreeViewModelListener<E>
{

    private TreeViewModel<E> model;

    public TreeViewModelWrapper(TreeViewModel<E> model)
    {
        setModel(model);
    }

    public TreeViewModel<E> getModel()
    {
        return model;
    }

    public void setModel(TreeViewModel<E> model)
    {
        if (this.model != null)
        {
            this.model.removeTreeViewModelListener(this);
        }
        this.model = model;
        model.addTreeViewModelListener(this);
        setData(model.getData());
    }

    public List<? extends TreeView<E>> getTreeViews()
    {
        return model.getTreeViews();
    }

    public int getDefaultTreeViewIndex()
    {
        return model.getDefaultTreeViewIndex();
    }

    public DataView<E> getDataView()
    {
        return model.getDataView();
    }

    public void dataChanged(TreeViewModelEvent<E> event)
    {
        setData(event.getNewData());
    }

}
