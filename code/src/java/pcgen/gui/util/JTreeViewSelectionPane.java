/*
 * JTreeViewSelectionPane.java
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
 * Created on Jul 11, 2008, 7:28:43 PM
 */
package pcgen.gui.util;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractCellEditor;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import pcgen.gui.util.treeview.DataView;
import pcgen.gui.util.treeview.TreeViewTableModel;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class JTreeViewSelectionPane extends JTreeViewPane
{

    public enum SelectionType
    {

        RADIO,
        CHECKBOX
    }
    private JTable rowheaderTable;
    private SelectionType selectionType;

    public JTreeViewSelectionPane()
    {

    }

    private class TreeViewSelectionTableModel<E> extends TreeViewTableModel<E>
    {

        private Set<E> selectedSet;

        public TreeViewSelectionTableModel(DataView<E> dataView)
        {
            super(dataView);
            this.selectedSet = new HashSet<E>();
        }

        @Override
        protected void populateDataMap(Collection<E> data)
        {
            super.populateDataMap(data);
            selectedSet.retainAll(data);
        }

        @Override
        public boolean isCellEditable(Object node, int column)
        {
            if (column < 0)
            {
                return true;
            }
            return super.isCellEditable(node, column);
        }

        @Override
        public Object getValueAt(Object node, int column)
        {

            if (column < 0)
            {
                Object obj = super.getValueAt(node, 0);
                if (dataMap.containsKey(obj))
                {
                    return Boolean.valueOf(selectedSet.contains(obj));
                }
                else
                {
                    return null;
                }
            }
            return super.getValueAt(node, column);
        }

        @Override
        public void setValueAt(Object aValue, Object node, int column)
        {
            if (column < 0)
            {
                @SuppressWarnings("unchecked")
                E obj = (E) super.getValueAt(node, 0);
                if ((Boolean) aValue)
                {
                    switch (selectionType)
                    {
                        case RADIO:
                            selectedSet.clear();
                            rowheaderTable.validate();
                        case CHECKBOX:
                            selectedSet.add(obj);
                    }
                }
                else
                {
                    selectedSet.remove(obj);
                }
                rowheaderTable.validate();
                return;
            }
            super.setValueAt(aValue, node, column);
        }

    }

    private static class RadioButtonEditor extends AbstractCellEditor implements ActionListener,
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

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
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

    private static class RadioButtonRenderer extends JRadioButton
            implements TableCellRenderer
    {

        private final DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();

        public RadioButtonRenderer()
        {
            setHorizontalAlignment(JRadioButton.CENTER);
            setBorderPainted(true);
        }

        public Component getTableCellRendererComponent(JTable table,
                                                        Object value,
                                                        boolean isSelected,
                                                        boolean hasFocus,
                                                        int row,
                                                        int column)
        {
            defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                                                          row, column);
            if (value == null)
            {
                return defaultRenderer;
            }
            setForeground(defaultRenderer.getForeground());
            setBackground(defaultRenderer.getBackground());
            setBorder(defaultRenderer.getBorder());

            setSelected(((Boolean) value).booleanValue());
            return this;
        }

    }
}
