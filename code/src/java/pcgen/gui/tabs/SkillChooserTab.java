/*
 * SkillChooserTab.java
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
 * Created on Jul 10, 2008, 8:03:21 PM
 */
package pcgen.gui.tabs;

import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class SkillChooserTab
{

    private static class TableCellSpinnerEditor extends AbstractCellEditor implements TableCellEditor
    {

        private JSpinner spinner;

        public TableCellSpinnerEditor()
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

    private static class TableCellSpinnerRenderer extends JSpinner implements TableCellRenderer
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
