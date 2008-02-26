/*
 * DefaultDynamicTableColumnModel.java
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
 * Created on Feb 25, 2008, 2:01:31 PM
 */
package pcgen.gui.util.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public class DefaultDynamicTableColumnModel extends DefaultTableColumnModel
        implements DynamicTableColumnModel
{

    private final List<TableColumn> availableColumns;

    public DefaultDynamicTableColumnModel(TableColumnModel model, int offset)
    {
        ArrayList<TableColumn> allColumns = Collections.list(model.getColumns());
        if (offset < allColumns.size())
        {
            this.availableColumns = Collections.unmodifiableList(allColumns.subList(offset,
                                                                                    allColumns.size()));
            for (int x = 0; x < offset; x++)
            {
                this.addColumn(allColumns.get(x));
            }
        }
        else
            this.availableColumns = Collections.emptyList();
    }

    public DefaultDynamicTableColumnModel(TableColumnModel model, int offset,
                                           int[] visibleColumns)
    {
        this(model, offset);
        for (int column : visibleColumns)
        {
            this.addColumn(availableColumns.get(column - offset));
        }
    }

    public List<TableColumn> getAvailableColumns()
    {
        return availableColumns;
    }

    public boolean isVisible(TableColumn column)
    {
        return tableColumns.contains(column);
    }

    public void toggleVisible(TableColumn column)
    {
        if (availableColumns.contains(column))
        {
            if (isVisible(column))
            {
                removeColumn(column);
            }
            else
            {
                addColumn(column);
            }
        }
    }

}
