/*
 * StatTable.java
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
 * Created on Jun 15, 2008, 10:42:01 PM
 */
package pcgen.gui.proto;

import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class StatTable extends JTable
{

    private static final String[] columns = {"Stat",
                                                "Score",
                                                "Race Adj",
                                                "Other Adj",
                                                "Total",
                                                "Mod"
    };

    public StatTable()
    {
        TableColumnModel model = new DefaultTableColumnModel();
        model.addColumn(createStatColumn(0, "Stat", null, null));
        model.addColumn(createStatColumn(1, "Score", new SpinnerRenderer(),
                                         new SpinnerEditor()));
        model.addColumn(createStatColumn(2, "Race Adj", null, null));
        model.addColumn(createStatColumn(3, "Other Adj", null, null));
        model.addColumn(createStatColumn(4, "Total", null, null));
        model.addColumn(createStatColumn(5, "Mod", null, null));

        setColumnModel(model);
        setAutoCreateColumnsFromModel(false);

        setModel(new DefaultTableModel(6, 6));
    }

    private TableColumn createStatColumn(int index, String title,
                                          TableCellRenderer renderer,
                                          TableCellEditor editor)
    {
        TableColumn column = new TableColumn(index, 75, renderer, editor);
        column.setHeaderValue(title);
        column.sizeWidthToFit();
        return column;
    }

    private static class SpinnerEditor extends AbstractCellEditor implements TableCellEditor
    {

        private JSpinner spinner;

        public SpinnerEditor()
        {
            SpinnerNumberModel model = new SpinnerNumberModel();
            model.setMinimum(0);
            this.spinner = new JSpinner(model);
        }

        public Object getCellEditorValue()
        {
            return spinner.getValue();
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                      boolean isSelected,
                                                      int row,
                                                      int column)
        {
            if (value == null)
            {
                spinner.setValue(0);
            }
            else
            {
                spinner.setValue(value);
            }
            return spinner;
        }

    }

    private static class SpinnerRenderer extends JSpinner implements TableCellRenderer
    {

        public Component getTableCellRendererComponent(JTable table,
                                                        Object value,
                                                        boolean isSelected,
                                                        boolean hasFocus,
                                                        int row,
                                                        int column)
        {
            if (value == null)
            {
                setValue(0);
            }
            else
            {
                setValue(value);
            }
            return this;
        }

    }
}
