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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import pcgen.gui.util.treeview.DataView;
import pcgen.gui.util.treeview.TreeViewModel;
import pcgen.gui.util.treeview.TreeViewTableModel;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class JTreeViewSelectionPane extends JTreeViewPane
{

    public static enum SelectionType
    {

        RADIO,
        CHECKBOX
    }
    private SelectionType selectionType;
    private JTableEx rowheaderTable;

    public JTreeViewSelectionPane()
    {
        this(SelectionType.RADIO);
    }

    public JTreeViewSelectionPane(SelectionType selectionType)
    {
        setSelectionType(selectionType);
        this.rowheaderTable = new JTableEx();
        initComponents();
    }

    public JTreeViewSelectionPane(TreeViewModel<?> viewModel)
    {
        this(viewModel, SelectionType.RADIO);
    }

    public JTreeViewSelectionPane(TreeViewModel<?> viewModel, SelectionType selectionType)
    {
        this(selectionType);
        setTreeViewModel(viewModel);
    }

    private void initComponents()
    {
        JTable table = getTable();
        rowheaderTable.setAutoCreateColumnsFromModel(false);
        rowheaderTable.setModel(table.getModel());
        rowheaderTable.setSelectionModel(table.getSelectionModel());
        rowheaderTable.setRowHeight(table.getRowHeight());
        rowheaderTable.setIntercellSpacing(table.getIntercellSpacing());
        rowheaderTable.setShowGrid(false);
        rowheaderTable.setFocusable(false);
        
        TableColumn column;
        TableCellRenderer renderer;
        TableCellEditor editor;
        if (selectionType == SelectionType.RADIO)
        {
            renderer = new ToggleButtonRenderer(new JRadioButton());
            editor = new RadioButtonEditor();
        }
        else
        {
            renderer = new ToggleButtonRenderer(new JCheckBox());
            editor = rowheaderTable.getDefaultEditor(Boolean.class);
        }
        column = new TableColumn(-1, 20, renderer, editor);
        column.setHeaderValue(new Object());

        rowheaderTable.addColumn(column);
        rowheaderTable.setPreferredScrollableViewportSize(new Dimension(20, 400));

        setRowHeaderView(rowheaderTable);
    }

    @Override
    protected <T> TreeViewTableModel<T> createDefaultTreeViewTableModel(DataView<T> dataView)
    {
        return new TreeViewSelectionTableModel<T>(dataView);
    }

    public void setSelectionType(SelectionType selectionType)
    {
        this.selectionType = selectionType;
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
        public boolean isCellEditable(Object node, int column)
        {
            if (column < 0)
            {
                Object obj = super.getValueAt(node, 0);
                return dataMap.containsKey(obj);
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
                    if (selectionType == SelectionType.RADIO)
                    {
                        selectedSet.clear();
                    }
                    selectedSet.add(obj);
                }
                else
                {
                    selectedSet.remove(obj);
                }
                rowheaderTable.repaint(rowheaderTable.getVisibleRect());
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

    private static class ToggleButtonRenderer extends DefaultTableCellRenderer
    {

        private JToggleButton button;

        public ToggleButtonRenderer(JToggleButton button)
        {
            this.button = button;
            button.setHorizontalAlignment(CENTER);
            button.setBorderPainted(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                        boolean isSelected, boolean hasFocus,
                                                        int row,
                                                        int column)
        {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
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
}
