/*
 * TableCellUtils.java
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
 * Created on Aug 10, 2008, 3:37:34 PM
 */
package pcgen.gui.util.table;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public final class TableCellUtils
{

    private TableCellUtils()
    {

    }

    public static class RadioButtonEditor extends AbstractCellEditor
            implements ActionListener,
                       TableCellEditor
    {

        private JRadioButton button;

        public RadioButtonEditor()
        {
            this.button = new JRadioButton();
            button.setHorizontalAlignment(JRadioButton.CENTER);
            button.addActionListener(this);
        }

        public Object getCellEditorValue()
        {
            return Boolean.valueOf(button.isSelected());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                      boolean isSelected,
                                                      int row, int column)
        {
            boolean selected = false;
            if (value instanceof Boolean)
            {
                selected = ((Boolean) value).booleanValue();
            }
            else if (value instanceof String)
            {
                selected = value.equals("true");
            }
            button.setSelected(selected);
            return button;
        }

        public void actionPerformed(ActionEvent e)
        {
            stopCellEditing();
        }

    }

    public static class SpinnerEditor extends AbstractCellEditor
            implements TableCellEditor, ChangeListener
    {

        protected final JSpinner spinner;

        public SpinnerEditor()
        {
            this(new JSpinner());
        }

        public SpinnerEditor(SpinnerModel model)
        {
            this(new JSpinner(model));
        }

        public SpinnerEditor(JSpinner spinner)
        {
            this.spinner = spinner;
            spinner.addChangeListener(this);
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
            spinner.setValue(value);
            return spinner;
        }

        public void stateChanged(ChangeEvent e)
        {
            stopCellEditing();
        }

    }

    public static class ToggleButtonRenderer extends DefaultTableCellRenderer
    {

        private JToggleButton button;

        public ToggleButtonRenderer(JToggleButton button)
        {
            this.button = button;
            button.setHorizontalAlignment(CENTER);
            button.setBorderPainted(true);
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
                                                hasFocus, row,
                                                column);
            if (value == null)
            {
                return this;
            }
            button.setForeground(getForeground());
            button.setBackground(getBackground());
            button.setBorder(getBorder());

            button.setSelected(((Boolean) value).booleanValue());
            return button;
        }

    }

    public static class SpinnerRenderer extends DefaultTableCellRenderer
    {

        private final JSpinner spinner;

        public SpinnerRenderer()
        {
            this(new JSpinner());
        }

        public SpinnerRenderer(JSpinner spinner)
        {
            this.spinner = spinner;
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
            if (value == null)
            {
                return this;
            }
            spinner.setBackground(getBackground());
            spinner.setForeground(getForeground());
            spinner.setValue(value);
            return spinner;
        }

    }
}
