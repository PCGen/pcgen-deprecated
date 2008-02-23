/*
 * JSortableTable.java
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
 * Created on Feb 22, 2008, 3:42:02 PM
 */
package pcgen.gui.util;

import javax.swing.JTable;
import javax.swing.table.TableModel;
import pcgen.gui.util.table.DefaultSortableTableModel;
import pcgen.gui.util.table.SortableTableModel;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public class JSortableTable extends JTable
{

    private ModelSorter sorter;

    public JSortableTable()
    {
        super();
    }

    public JSortableTable(TableModel model)
    {
        super(model);
    }

    @Override
    public void setModel(TableModel model)
    {
        if (model instanceof SortableTableModel)
        {
            super.setModel(model);
        }
        else
        {
            super.setModel(new DefaultSortableTableModel(model));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public SortableTableModel getModel()
    {
        return (SortableTableModel) super.getModel();
    }

    public ModelSorter getModelSorter()
    {
        return sorter;
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
            sorter.setModel(getModel());
        }
        this.sorter = sorter;
    //TODO: do something with old
    }

}
