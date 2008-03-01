/*
 * JTableEx.java
 * Copyright 2001 (C) Jonas Karlsson <jujutsunerd@users.sourceforge.net>
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
 * Created on June 27, 2001, 20:36 PM
 */
package pcgen.gui.util;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import pcgen.gui.util.table.DefaultSortableTableModel;
import pcgen.gui.util.table.DynamicTableColumnModel;
import pcgen.gui.util.table.SortableTableModel;
import pcgen.util.Comparators;

/**
 *  <code>JTableEx</code> extends JTable to provide auto-tooltips.
 *
 * @author     Jonas Karlsson <jujutsunerd@users.sourceforge.net>
 * @version    $Revision: 1817 $
 */
public class JTableEx extends JTable
{

    private static final long serialVersionUID = 514835142307946415L;
    private final RowComparator rowComparator = new RowComparator();
    private List<SortingPriority> columnkeys;

    /**
     * Constructor
     */
    public JTableEx()
    {
        this(null, null, null);
    }

    /**
     * Constructor
     * @param tm
     */
    public JTableEx(SortableTableModel tm)
    {
        this(tm, null, null);
    }

    /**
     * Constructor
     * @param tm
     * @param tcm
     */
    public JTableEx(SortableTableModel tm, TableColumnModel tcm)
    {
        this(tm, tcm, null);
    }

    public JTableEx(SortableTableModel tm, TableColumnModel tcm,
                     ListSelectionModel lsm)
    {
        super(tm, tcm, lsm);

        setDefaultRenderer(BigDecimal.class, new AlignCellRenderer(
                           SwingConstants.RIGHT));
        setDefaultRenderer(Float.class, new AlignCellRenderer(
                           SwingConstants.RIGHT));
        setDefaultRenderer(Integer.class, new AlignCellRenderer(
                           SwingConstants.RIGHT));
        setSortingPriority(createDefaultSortingPriority());
        setTableHeader(new JTableSortingHeader(this));
    }

    protected List<? extends SortingPriority> createDefaultSortingPriority()
    {
        return Collections.emptyList();
    }

    @Override
    public void createDefaultColumnsFromModel()
    {
        TableModel m = getModel();
        if (m != null)
        {
            // Remove any current columns
            TableColumnModel cm = getColumnModel();
            if (cm instanceof DynamicTableColumnModel)
            {
                DynamicTableColumnModel dm = (DynamicTableColumnModel) cm;
                TableColumn[] columns = dm.getAvailableColumns().toArray(new TableColumn[0]);
                for (TableColumn column : columns)
                {
                    dm.removeColumn(column);
                }
            }
            while (cm.getColumnCount() > 0)
            {
                cm.removeColumn(cm.getColumn(0));
            }

            // Create new columns from the data model info
            for (int i = 0; i < m.getColumnCount(); i++)
            {
                TableColumn newColumn = new TableColumn(i);
                addColumn(newColumn);
            }
        }
    }

    @Override
    public void setModel(TableModel model)
    {
        if (!(model instanceof SortableTableModel))
        {
            model = new DefaultSortableTableModel(model);
        }
        super.setModel(model);
    }

    @Override
    public SortableTableModel getModel()
    {
        return (SortableTableModel) super.getModel();
    }

    /**
     * set horizontal alignment of column
     * and attach a new cell renderer
     * @param col
     * @param alignment
     **/
    public void setColAlign(int col, int alignment)
    {
        getColumnModel().getColumn(col).setCellRenderer(
                new AlignCellRenderer(alignment));
    }

    public void toggleSort(int column)
    {
        Vector<SortingPriority> list = new Vector<SortingPriority>(getSortingPriority());
        int index;
        for (index = list.size() - 1; index >= 0; index--)
        {
            if (list.get(index).getColumn() == column)
            {
                break;
            }
        }
        switch (index)
        {
            case 0:
                if (list.get(0).getMode() == SortMode.ASCENDING)
                {
                    list.set(0, new SortingPriority(column, SortMode.DESCENDING));
                    break;
                }
            default:
                list.remove(index);
            case -1:
                list.add(0, new SortingPriority(column, SortMode.ASCENDING));
        }
        if (list.size() > 2)
        {
            list.setSize(2);
        }
        setSortingPriority(list);
    }

    public void setSortingPriority(List<? extends SortingPriority> keys)
    {
        this.columnkeys = Collections.unmodifiableList(keys);
        sortModel();
    }

    public void sortModel()
    {
        getModel().sortModel(rowComparator);
    }

    public List<? extends SortingPriority> getSortingPriority()
    {
        return columnkeys;
    }

    private final class RowComparator implements Comparator<List<?>>
    {

        @SuppressWarnings("unchecked")
        public int compare(List<?> o1,
                            List<?> o2)
        {
            SortableModel model = getModel();
            for (SortingPriority priority : columnkeys)
            {
                if (priority.getMode() == SortMode.UNORDERED)
                {
                    continue;
                }
                int column = priority.getColumn();
                Comparator comparator = Comparators.getComparatorFor(model.getColumnClass(column));
                Object obj1 = null;
                Object obj2 = null;
                if (o1.size() > column)
                {
                    obj1 = o1.get(column);
                }
                if (o2.size() > column)
                {
                    obj2 = o2.get(column);
                }
                int ret;
                if (obj1 == null || obj2 == null)
                {
                    if (obj1 == obj2)
                    {
                        ret = 0;
                    }
                    else if (obj1 == null)
                    {
                        ret = -1;
                    }
                    else
                    {
                        ret = 1;
                    }
                }
                else
                {
                    ret = comparator.compare(obj1, obj2);
                }
                if (priority.getMode() == SortMode.DESCENDING)
                {
                    ret *= -1;
                }
                if (ret != 0)
                {
                    return ret;
                }
            }
            return 0;
        }

    }

    /**
     * Align the cell text in a column
     **/
    public static final class AlignCellRenderer extends DefaultTableCellRenderer
    {

        /**
         * align is one of:
         * SwingConstants.LEFT
         * SwingConstants.CENTER
         * SwingConstants.RIGHT
         **/
        private int align = SwingConstants.LEFT;

        /**
         * Align the cell renderer
         * @param anInt
         */
        public AlignCellRenderer(int anInt)
        {
            super();
            align = anInt;
            setHorizontalAlignment(align);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                                                        Object value,
                                                        boolean isSelected,
                                                        boolean hasFocus,
                                                        int row,
                                                        int column)
        {
            super.getTableCellRendererComponent(table, value, isSelected,
                                                hasFocus, row, column);
            setEnabled((table == null) || table.isEnabled());

            setHorizontalAlignment(align);

            return this;
        }

    }
}
