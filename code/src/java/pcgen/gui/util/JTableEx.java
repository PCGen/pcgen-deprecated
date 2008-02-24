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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import pcgen.gui.util.table.DefaultSortableTableModel;
import pcgen.gui.util.table.SortableTableModel;

/**
 *  <code>JTableEx</code> extends JTable to provide auto-tooltips.
 *
 * @author     Jonas Karlsson <jujutsunerd@users.sourceforge.net>
 * @version    $Revision: 1817 $
 */
public class JTableEx extends JTable
{

    private static final long serialVersionUID = 514835142307946415L;
    protected ModelSorter sorter;

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
    public JTableEx(TableModel tm)
    {
        this(tm, null, null);
    }

    /**
     * Constructor
     * @param tm
     * @param tcm
     */
    public JTableEx(TableModel tm, TableColumnModel tcm)
    {
        this(tm, tcm, null);
    }

    private JTableEx(TableModel tm, TableColumnModel tcm,
                      ListSelectionModel lsm)
    {
        super(tm, tcm, lsm);

        setDefaultRenderer(BigDecimal.class, new AlignCellRenderer(
                           SwingConstants.RIGHT));
        setDefaultRenderer(Float.class, new AlignCellRenderer(
                           SwingConstants.RIGHT));
        setDefaultRenderer(Integer.class, new AlignCellRenderer(
                           SwingConstants.RIGHT));
        setModelSorter(new ModelSorter());
        setTableHeader(new JTableSortingHeader(this));
    }

    @Override
    public void setModel(TableModel model)
    {
        if (!(model instanceof SortableTableModel))
        {
            model = new DefaultSortableTableModel(model);
        }
        super.setModel(model);
        if (sorter != null)
        {
            sorter.setModel(getModel());
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
