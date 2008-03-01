/*
 * JTreeTablePane.java
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
 * Created on Feb 29, 2008, 12:56:51 AM
 */
package pcgen.gui.util;

import pcgen.gui.util.table.SortableTableModel;
import pcgen.gui.util.treetable.TreeTableModel;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public class JTreeTablePane extends JTablePane
{

    public JTreeTablePane()
    {
        this(null);
    }

    public JTreeTablePane(TreeTableModel model)
    {
        super(new JTreeTable(model));
    }

    @Override
    protected JTreeTable getTable()
    {
        return (JTreeTable) super.getTable();
    }

    @Override
    public void setModel(SortableTableModel model)
    {
        //TODO: log something
    }

    public void setTreeTableModel(TreeTableModel model)
    {
        getTable().setTreeTableModel(model);
    }

}
