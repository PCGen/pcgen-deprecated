/*
 * ListTableModel.java
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
 * Created on Sep 11, 2008, 10:51:38 PM
 */
package pcgen.gui.util.table;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import pcgen.gui.util.DefaultGenericListModel;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class ListTableModel<E> extends DefaultGenericListModel<E>
        implements TableModel, ListDataListener
{

    private String title;

    public ListTableModel(String title)
    {
        this.title = title;
        addListDataListener(this);
    }

    public int getRowCount()
    {
        return getSize();
    }

    public int getColumnCount()
    {
        return 1;
    }

    public String getColumnName(int columnIndex)
    {
        return title;
    }

    public Class<?> getColumnClass(int columnIndex)
    {
        return Object.class;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return true;
    }

    public Object getValueAt(int rowIndex, int columnIndex)
    {
        return get(rowIndex);
    }

    @SuppressWarnings("unchecked")
    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
        setElementAt((E) aValue, rowIndex);
    }

    /**
     * Adds a listener to the list that's notified each time a change
     * to the data model occurs.
     *
     * @param	l		the TableModelListener
     */
    public void addTableModelListener(TableModelListener l)
    {
        listenerList.add(TableModelListener.class, l);
    }

    /**
     * Removes a listener from the list that's notified each time a
     * change to the data model occurs.
     *
     * @param	l		the TableModelListener
     */
    public void removeTableModelListener(TableModelListener l)
    {
        listenerList.remove(TableModelListener.class, l);
    }

    /**
     * Returns an array of all the table model listeners 
     * registered on this model.
     *
     * @return all of this model's <code>TableModelListener</code>s 
     *         or an empty
     *         array if no table model listeners are currently registered
     *
     * @see #addTableModelListener
     * @see #removeTableModelListener
     *
     * @since 1.4
     */
    public TableModelListener[] getTableModelListeners()
    {
        return (TableModelListener[]) listenerList.getListeners(
                TableModelListener.class);
    }

    /**
     * Forwards the given notification event to all
     * <code>TableModelListeners</code> that registered
     * themselves as listeners for this table model.
     *
     * @param e  the event to be forwarded
     *
     * @see #addTableModelListener
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableChanged(TableModelEvent e)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == TableModelListener.class)
            {
                ((TableModelListener) listeners[i + 1]).tableChanged(e);
            }
        }
    }

    public void intervalAdded(ListDataEvent e)
    {
        fireTableChanged(new TableModelEvent(this, e.getIndex0(), e.getIndex1(),
                                             TableModelEvent.ALL_COLUMNS,
                                             TableModelEvent.INSERT));
    }

    public void intervalRemoved(ListDataEvent e)
    {
        fireTableChanged(new TableModelEvent(this, e.getIndex0(), e.getIndex1(),
                                             TableModelEvent.ALL_COLUMNS,
                                             TableModelEvent.DELETE));
    }

    public void contentsChanged(ListDataEvent e)
    {
        fireTableChanged(new TableModelEvent(this, e.getIndex0(), e.getIndex1(),
                                             TableModelEvent.ALL_COLUMNS,
                                             TableModelEvent.UPDATE));
    }

}
