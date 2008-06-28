/*
 * JTablePane.java
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
 * Created on Feb 27, 2008, 10:44:14 PM
 */
package pcgen.gui.util;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.TableColumn;
import pcgen.gui.util.event.DynamicTableColumnModelListener;
import pcgen.gui.util.table.DefaultDynamicTableColumnModel;
import pcgen.gui.util.table.DynamicTableColumnModel;
import pcgen.gui.util.table.SortableTableModel;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public class JTablePane extends JScrollPane
{

    private final JButton cornerButton = new JButton(new CornerAction());
    private final ModelListener listener = new ModelListener();
    private JPopupMenu menu = new JPopupMenu();
    private DynamicTableColumnModel columnModel;
    private JTableEx table;

    /**
     * Constructor
     */
    public JTablePane()
    {
        this((SortableTableModel) null);
    }

    /**
     * Constructor
     * @param tm
     */
    public JTablePane(SortableTableModel tm)
    {
        this(new JTableEx(tm));
    }

    protected JTablePane(JTableEx table)
    {
        super(VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setTable(table);
        setColumnModel(createDefaultDynamicTableColumnModel());
    }

    protected DynamicTableColumnModel createDefaultDynamicTableColumnModel()
    {
        return new DefaultDynamicTableColumnModel(table.getColumnModel(), 1);
    }

    public void setModel(SortableTableModel model)
    {
        table.setModel(model);
    }

    public void setColumnModel(DynamicTableColumnModel columnModel)
    {
        if (this.columnModel != null)
        {
            this.columnModel.removeDynamicTableColumnModelListener(listener);
        }
        this.columnModel = columnModel;
        columnModel.addDynamicTableColumnModelListener(listener);
        table.setColumnModel(columnModel);
        List<TableColumn> columns = columnModel.getAvailableColumns();
        if (!columns.isEmpty())
        {
            menu = new JPopupMenu();
            for (TableColumn column : columns)
            {
                JCheckBoxMenuItem item = new JCheckBoxMenuItem();
                item.setSelected(columnModel.isVisible(column));
                item.setAction(new MenuAction(column));
                menu.add(item);
            }
            setCorner(JScrollPane.UPPER_RIGHT_CORNER, cornerButton);
        }
        else
        {
            setCorner(JScrollPane.UPPER_RIGHT_CORNER, null);
        }
    }

    private void setTable(JTableEx table)
    {
        this.table = table;
        setViewportView(table);
    }

    protected JTableEx getTable()
    {
        return table;
    }

    public void setDragEnabled(boolean b)
    {
        table.setDragEnabled(b);
    }

    @Override
    public void setTransferHandler(TransferHandler newHandler)
    {
        table.setTransferHandler(newHandler);
    }

    @Override
    public TransferHandler getTransferHandler()
    {
        return table.getTransferHandler();
    }

    public ListSelectionModel getSelectionModel()
    {
        return table.getSelectionModel();
    }

    private class CornerAction extends AbstractAction
    {

        public CornerAction()
        {
            super("...");
        }

        public void actionPerformed(ActionEvent e)
        {
            menu.show(table, table.getWidth() - menu.getWidth(), 0);
        }

    }

    private class MenuAction extends AbstractAction
    {

        private TableColumn column;

        public MenuAction(TableColumn column)
        {
            super(column.getHeaderValue().toString());
            this.column = column;
        }

        public void actionPerformed(ActionEvent e)
        {
            columnModel.toggleVisible(column);
        }

    }

    private class ModelListener implements DynamicTableColumnModelListener
    {

        public void availableColumnAdded(TableColumnModelEvent event)
        {
            int index = event.getToIndex();
            TableColumn column = columnModel.getAvailableColumns().get(index);
            JCheckBoxMenuItem item = new JCheckBoxMenuItem();
            item.setSelected(columnModel.isVisible(column));
            item.setAction(new MenuAction(column));
            menu.insert(item, index);
            if (getCorner(JScrollPane.UPPER_RIGHT_CORNER) == null)
            {
                setCorner(JScrollPane.UPPER_RIGHT_CORNER, cornerButton);
            }
        }

        public void availableColumnRemove(TableColumnModelEvent event)
        {
            menu.remove(event.getFromIndex());
            if (menu.getComponentCount() == 0)
            {
                setCorner(JScrollPane.UPPER_RIGHT_CORNER, null);
            }
        }

    }
}
