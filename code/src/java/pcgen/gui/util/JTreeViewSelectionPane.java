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

import java.awt.Dimension;
import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import pcgen.gui.util.table.TableCellUtilities.RadioButtonEditor;
import pcgen.gui.util.table.TableCellUtilities.ToggleButtonRenderer;
import pcgen.gui.util.treeview.DataView;
import pcgen.gui.util.treeview.TreeViewModel;
import pcgen.gui.util.treeview.TreeViewTableModel;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class JTreeViewSelectionPane extends JTreeViewPane implements ItemSelectable
{

    private static final long serialVersionUID = -4024848218899529700L;
    private static final Object COLUMN_ID = new Object();

    public static enum SelectionType
    {

        RADIO,
        CHECKBOX
    }
    private SelectionType selectionType;
    private JTableEx rowheaderTable;
    private boolean editable = true;

    public JTreeViewSelectionPane()
    {
        this(SelectionType.RADIO);
    }

    public JTreeViewSelectionPane(SelectionType selectionType)
    {
        this.rowheaderTable = new JTableEx();
        initComponents();
        setSelectionType(selectionType);
    }

    public JTreeViewSelectionPane(TreeViewModel<?> viewModel)
    {
        this(viewModel, SelectionType.RADIO);
    }

    public JTreeViewSelectionPane(TreeViewModel<?> viewModel,
                                   SelectionType selectionType)
    {
        this(selectionType);
        setTreeViewModel(viewModel);
    }

    private void initComponents()
    {
        JTable table = getTable();
        rowheaderTable.setAutoCreateColumnsFromModel(false);
        // force the tables to share models
        rowheaderTable.setModel(table.getModel());
        rowheaderTable.setSelectionModel(table.getSelectionModel());
        rowheaderTable.setRowHeight(table.getRowHeight());
        rowheaderTable.setIntercellSpacing(table.getIntercellSpacing());
        rowheaderTable.setShowGrid(false);
        rowheaderTable.setFocusable(false);

        TableColumn column = new TableColumn(-1);
        column.setHeaderValue(COLUMN_ID);
        rowheaderTable.addColumn(column);
        rowheaderTable.setPreferredScrollableViewportSize(new Dimension(20, 0));

        setRowHeaderView(rowheaderTable);
    }

    @Override
    protected <T> TreeViewTableModel<T> createDefaultTreeViewTableModel(DataView<T> dataView)
    {
        return new TreeViewSelectionTableModel<T>(dataView);
    }

    public void setSelectionType(SelectionType selectionType)
    {
        if (this.selectionType == null || this.selectionType != selectionType)
        {
            this.selectionType = selectionType;

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
            TableColumn column = rowheaderTable.getColumn(COLUMN_ID);
            column.setCellRenderer(renderer);
            column.setCellEditor(editor);
        }
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.
     * @param e  the event of interest
     *  
     * @see EventListenerList
     */
    protected void fireItemStateChanged(ItemEvent e)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == ItemListener.class)
            {
                // Lazily create the event:
                // if (changeEvent == null)
                // changeEvent = new ChangeEvent(this);
                ((ItemListener) listeners[i + 1]).itemStateChanged(e);
            }
        }
    }

    public Object[] getSelectedObjects()
    {
        TreeViewSelectionTableModel<?> model = (TreeViewSelectionTableModel<?>) getTable().getTreeTableModel();
        if (model != null)
        {
            return model.selectedSet.toArray();
        }
        return new Object[0];
    }

    public <T> void setSelectedObjects(Collection<T> objs)
    {
        @SuppressWarnings("unchecked")
        TreeViewSelectionTableModel<T> model = (TreeViewSelectionTableModel<T>) getTable().getTreeTableModel();
        if (model != null)
        {

            model.selectedSet.clear();
            if (selectionType == SelectionType.RADIO && objs.size() > 1)
            {
                model.selectedSet.add(objs.iterator().next());
            }
            else
            {
                model.selectedSet.addAll(objs);
            }
        }
    }

    public void setEditable(boolean editable)
    {
        this.editable = editable;
    }

    public void addItemListener(ItemListener l)
    {
        listenerList.remove(ItemListener.class, l);
    }

    public void removeItemListener(ItemListener l)
    {
        listenerList.add(ItemListener.class, l);
    }

    private class TreeViewSelectionTableModel<E> extends TreeViewTableModel<E>
    {

        Set<E> selectedSet;

        public TreeViewSelectionTableModel(DataView<E> dataView)
        {
            super(dataView);
            this.selectedSet = new HashSet<E>();
        }

        @Override
        public boolean isCellEditable(Object node, int column)
        {
            if (editable && column < 0)
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
                    if (selectionType == SelectionType.RADIO &&
                            !selectedSet.isEmpty())
                    {
                        Object item = selectedSet.iterator().next();
                        selectedSet.clear();
                        fireItemStateChanged(new ItemEvent(JTreeViewSelectionPane.this,
                                                           ItemEvent.ITEM_STATE_CHANGED,
                                                           item,
                                                           ItemEvent.DESELECTED));
                    }
                    selectedSet.add(obj);
                    fireItemStateChanged(new ItemEvent(JTreeViewSelectionPane.this,
                                                       ItemEvent.ITEM_STATE_CHANGED,
                                                       obj,
                                                       ItemEvent.SELECTED));
                }
                else
                {
                    selectedSet.remove(obj);
                    fireItemStateChanged(new ItemEvent(JTreeViewSelectionPane.this,
                                                       ItemEvent.ITEM_STATE_CHANGED,
                                                       obj,
                                                       ItemEvent.DESELECTED));
                }
                rowheaderTable.repaint(rowheaderTable.getVisibleRect());
                return;
            }
            super.setValueAt(aValue, node, column);
        }

    }
}
