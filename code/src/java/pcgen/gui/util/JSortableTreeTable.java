/*
 * JSortableTreeTable.java
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
 * Created on Feb 22, 2008, 4:10:15 PM
 */
package pcgen.gui.util;

import java.util.Comparator;
import java.util.List;
import javax.swing.JTree;
import pcgen.gui.util.table.SortableTableModel;
import pcgen.gui.util.treetable.DefaultSortableTreeTableModel;
import pcgen.gui.util.treetable.SortableTreeTableModel;
import pcgen.gui.util.treetable.TreeTableModel;
import pcgen.util.Comparators;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public class JSortableTreeTable extends JTreeTable
{

    private ModelSorter sorter;

    public JSortableTreeTable()
    {
        this(null);
    }

    public JSortableTreeTable(SortableTreeTableModel model)
    {
        super(model);
    }

    @Override
    protected TreeTableModelAdapter createDefaultTreeTableModelAdapter(TreeTableModel treeTableModel,
                                                                        JTree tree)
    {
        return new SortableTreeTableModelAdapter((SortableTreeTableModel) treeTableModel,
                                                 tree);
    }

    public ModelSorter getModelSorter()
    {
        return sorter;
    }

    @Override
    public void setTreeTableModel(TreeTableModel model)
    {
        if (model instanceof SortableTreeTableModel)
        {
            super.setTreeTableModel(model);
        }
        else
        {
            super.setTreeTableModel(new DefaultSortableTreeTableModel(model));
        }
    }

    public void setModelSorter(ModelSorter sorter)
    {
        ModelSorter old = this.sorter;
        if (old != null)
        {
            old.setModel(null);
        }
        if (sorter != null)
        {
            sorter.setModel((SortableTreeTableModelAdapter) getModel());
        }
        this.sorter = sorter;
    //TODO: do something with old
    }

    private static final class SortableTreeTableModelAdapter extends TreeTableModelAdapter
            implements SortableTableModel
    {

        SortableTreeTableModelAdapter(SortableTreeTableModel model, JTree tree)
        {
            super(model, tree);
        }

        public void sortModel(Comparator<List<?>> comparator)
        {
            if (treeTableModel != null)
            {
                ((SortableTreeTableModel) treeTableModel).sortModel(comparator);
            }
        }

    }
}
