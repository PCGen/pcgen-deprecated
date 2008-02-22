/*
 * ModelSorter.java
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
 * Created on Feb 20, 2008, 8:21:42 PM
 */
package pcgen.gui.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.event.EventListenerList;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public class ModelSorter
{

    private final EventListenerList listenerList = new EventListenerList();
    private final RowComparator rowComparator = new RowComparator();
    private List<? extends SortingPriority> columnkeys = null;
    private Map<Integer, Comparator<?>> comparatorMap = null;
    private SortableModel model;

    public void addTableSorterListener(ModelSorterListener listener)
    {
        listenerList.add(ModelSorterListener.class, listener);
    }

    protected void fireTableSorterEvent(ModelSorterEvent event)
    {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == ModelSorterListener.class)
            {
                ((ModelSorterListener) listeners[i + 1]).tableSorted(event);
            }
        }
    }

    public void removeTableSorterListener(ModelSorterListener listener)
    {
        listenerList.remove(ModelSorterListener.class, listener);
    }

    public Comparator<?> getComparator(int column)
    {
        if (comparatorMap != null)
        {
            return comparatorMap.get(column);
        }
        return null;
    }

    public void setComparator(Comparator<?> comparator, int column)
    {
        if (comparatorMap == null)
        {
            comparatorMap = new HashMap<Integer, Comparator<?>>();
        }
        comparatorMap.put(column, comparator);
    }

    public void toggleSort(int column)
    {
        List<SortingPriority> list = new ArrayList<SortingPriority>(getSortingPriority());
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
                list.set(0, new SortingPriority(column, SortMode.DESCENDING));
                break;
            default:
                list.remove(index);
            case -1:
                list.add(0, new SortingPriority(column, SortMode.ASCENDING));
        }
        setSortingPriority(list);
    }

    public List<? extends SortingPriority> getSortingPriority()
    {
        return columnkeys;
    }

    public void setSortingPriority(List<? extends SortingPriority> keys)
    {
        this.columnkeys = new ArrayList(keys);
        sort();
    }

    public SortableModel getModel()
    {
        return model;
    }

    public void setModel(SortableModel model)
    {
        this.model = model;
    }

    public void sort()
    {
        model.sortModel(rowComparator);
        fireTableSorterEvent(new ModelSorterEvent());//TODO: make this more usefull
    }

    private final class RowComparator implements Comparator<List<?>>
    {

        public int compare(List<?> o1,
                            List<?> o2)
        {
            for (SortingPriority priority : columnkeys)
            {

                if (priority.getMode() == SortMode.UNORDERED)
                {
                    continue;
                }
                int column = priority.getColumn();
                Comparator comparator = getComparator(column);
                int ret = comparator.compare(o1.get(column), o2.get(column));
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

    public static class SortingPriority
    {

        private int column;
        private SortMode mode;

        public SortingPriority(int column, SortMode mode)
        {
            this.column = column;
            this.mode = mode;
        }

        public int getColumn()
        {
            return column;
        }

        public SortMode getMode()
        {
            return mode;
        }

    }
}
