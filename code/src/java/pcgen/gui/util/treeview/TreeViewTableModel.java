/*
 * TreeTableViewModel.java
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
 * Created on Feb 11, 2008, 9:04:19 PM
 */
package pcgen.gui.util.treeview;

import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pcgen.gui.util.AbstractTreeTableModel;
import pcgen.gui.util.TreeTableModel;
import pcgen.util.Comparators;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public final class TreeViewTableModel<E> extends AbstractTreeTableModel
{

    private final DataView dataview;
    private final EnumSet<? extends TreeView<E>> treeviews;
    private final List<DataViewColumn<E>> datacolumns;
    private final Map<E, List<?>> dataMap = new HashMap<E, List<?>>();
    private final Map<TreeView<E>, TreeViewNode<E>> viewMap = new HashMap<TreeView<E>, TreeViewNode<E>>();
    private Comparator<E> comparator = Comparators.toStringComparator();
    private TreeViewMode mode = TreeViewMode.ASCENDING;
    private TreeView<E> selectedView;

    public TreeViewTableModel(TreeViewModel<E> model,
            Collection<E> data)
    {
        this.treeviews = model.getTreeViews();
        this.selectedView = treeviews.iterator().next();
        this.dataview = model.getDataView();
        this.datacolumns = dataview.getDataColumns();
        setData(data);
        root = viewMap.get(selectedView);
    }

    public void setSelectedTreeView(TreeView<E> view)
    {
        this.selectedView = view;
        TreeViewNode<E> node = viewMap.get(view);
        if (node == null)
        {
            throw new IllegalArgumentException("Attempting to use an unknown TreeView");
        }
        root = node;
        TreeViewPathComparator<E> pathcomparator = node.getTreeViewPathComparator();
        if (!pathcomparator.getTreeViewMode().equals(mode) || !pathcomparator.getComparator().equals(comparator))
        {
            resetTreeViewPathComparator();
        }
        else
        {
            super.fireTreeStructureChanged(root, node.getTreePath());
        }
    }

    public void setData(Collection<E> data)
    {
        populateDataMap(data);
        populateViewMap(data);
    }

    public void setComparator(Comparator<E> comparator)
    {
        this.comparator = comparator;
        resetTreeViewPathComparator();
    }

    private void resetTreeViewPathComparator()
    {
        setTreeViewPathComparator(getNewTreeViewPathComparator());
    }

    private TreeViewPathComparator getNewTreeViewPathComparator()
    {
        return new TreeViewPathComparator(comparator, mode);
    }

    private void setTreeViewPathComparator(TreeViewPathComparator comparator)
    {
        TreeViewNode rootNode = viewMap.get(selectedView);
        rootNode.setTreeViewPathComparator(comparator);
        super.fireTreeStructureChanged(root, rootNode.getTreePath());
    }

    public void setTreeViewMode(TreeViewMode mode)
    {
        this.mode = mode;
        resetTreeViewPathComparator();
    }

    private void populateDataMap(Collection<E> data)
    {
        dataMap.entrySet().retainAll(data);
        for (E obj : data)
        {
            if (!dataMap.containsKey(obj))
            {
                dataMap.put(obj, dataview.getData(obj));
            }
        }
    }

    private void populateViewMap(Collection<E> data)
    {
        for (TreeView<E> view : treeviews)
        {
            TreeViewNode<E> node = new TreeViewNode<E>(getNewTreeViewPathComparator());
            for (E element : data)
            {
                for (TreeViewPath path : view.getPaths(element))
                {
                    node.createChild(path);
                }
            }
            viewMap.put(view, node);
        }
    }

    public int getColumnCount()
    {
        return datacolumns.size() + 1;
    }

    @Override
    public Class<?> getColumnClass(int column)
    {
        switch (column)
        {
            case 0:
                return TreeTableModel.class;
            default:
                return getDataColumn(column).getDataClass();
        }
    }

    public String getColumnName(int column)
    {
        switch (column)
        {
            case 0:
                return selectedView.getViewName();
            default:
                return getDataColumn(column).getName();
        }
    }

    public DataViewColumn getDataColumn(int column)
    {
        switch(column)
        {
             case 0:
                 return null;
            default:
                return datacolumns.get(column-1);
        }
    }
    
    public Object getValueAt(Object node, int column)
    {
        Object item = ((TreeViewNode<E>) node).getItem();
        List<?> list = dataMap.get(item);
        if (list == null)
        {
            return null;
        }
        return list.get(column - 1);
    }

    public Object getChild(Object parent, int index)
    {
        return ((TreeViewNode<E>) parent).getChildAt(index);
    }

    public int getChildCount(Object parent)
    {
        return ((TreeViewNode<E>) parent).getChildCount();
    }

}
