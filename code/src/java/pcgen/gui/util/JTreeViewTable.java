/*
 * JTreeViewTable.java
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
 * Created on Feb 14, 2008, 10:18:26 PM
 */
package pcgen.gui.util;

import java.util.Collection;
import pcgen.gui.util.treeview.TreeViewModel;
import pcgen.gui.util.treeview.TreeViewTableModel;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public class JTreeViewTable extends JTreeTable
{

    public <T> JTreeViewTable(TreeViewModel<T> model, Collection<T> collection)
    {
        this(new TreeViewTableModel(model, collection));
    }

    public JTreeViewTable(TreeViewTableModel model)
    {
        super(model);
    }

    public TreeViewTableModel getTreeViewTableModel()
    {
        return (TreeViewTableModel) super.getTreeTableModel();
    }

    public void setTreeViewTableModel(TreeViewTableModel model)
    {
        super.setTreeTableModel(model);
    }

}
