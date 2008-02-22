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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pcgen.gui.util.treetable.AbstractTreeTableModel;
import pcgen.gui.util.treetable.DefaultSortableTreeTableNode;
import pcgen.gui.util.treetable.TreeTableModel;
import pcgen.util.UnboundedArrayList;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public final class TreeViewTableModel<E> extends AbstractTreeTableModel
{

    private final Map<TreeView<E>, TreeViewNode> viewMap = new HashMap<TreeView<E>, TreeViewNode>();
    private final Map<E, List<?>> dataMap = new HashMap<E, List<?>>();
    private final DataView dataview;
    private final List<DataViewColumn> datacolumns;
    private TreeView<E> selectedView;

    public TreeViewTableModel(TreeViewModel<E> model,
                               Collection<E> data)
    {
        super(null);

        this.dataview = model.getDataView();
        this.datacolumns = dataview.getDataColumns();
        populateViewMap(model.getTreeViews());
        populateDataMap(data);
        setSelectedTreeView(model.getTreeViews().iterator().next());
    }

    public void setData(Collection<E> data)
    {
        populateDataMap(data);
        populateViewMap(viewMap.keySet());
        setSelectedTreeView(selectedView);
    }

    private void populateViewMap(Collection<? extends TreeView<E>> treeviews)
    {
        for (TreeView<E> view : treeviews)
        {
            viewMap.put(view, new TreeViewNode());
        }
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

    public TreeView<E> getSelectedTreeView()
    {
        return selectedView;
    }

    public void setSelectedTreeView(TreeView<E> view)
    {
        this.selectedView = view;
        TreeViewNode node = viewMap.get(view);
        if (node == null)
        {
            throw new IllegalArgumentException("Attempting to use an unknown TreeView");
        }
        if (node.isLeaf())
        {
            for (E element : dataMap.keySet())
            {
                for (TreeViewPath<E> path : view.getPaths(element))
                {
                    node.createPath(path);
                }
            }
        }
        setRoot(node);
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

    @Override
    public String getColumnName(int column)
    {
        switch (column)
        {
            case 0:
                return null;
            default:
                return getDataColumn(column).getName();
        }
    }

    private DataViewColumn getDataColumn(int column)
    {
        switch (column)
        {
            case 0:
                return null;
            default:
                return datacolumns.get(column - 1);
        }
    }

    private class TreeViewNode extends DefaultSortableTreeTableNode
    {

        private final int level;

        public TreeViewNode()
        {
            this(0);
        }

        private TreeViewNode(int level)
        {
            this.level = level;
        }

        @SuppressWarnings("unchecked")
        public void createPath(TreeViewPath<E> path)
        {
            if (path.getPathCount() > level)
            {
                Object key = path.getPathComponent(level);
                TreeViewNode node = null;
                for (int i = 0; i < childData.size(); i++)
                {
                    if (getChildData(i).get(0).equals(key))
                    {
                        node = (TreeViewNode) children.get(i);
                        break;
                    }
                }
                if (node == null)
                {
                    node = new TreeViewNode(level + 1);
                    ArrayList<Object> datalist = new UnboundedArrayList<Object>(1);
                    datalist.add(key);
                    List<?> data = dataMap.get(key);
                    if (key != null)
                    {
                        datalist.add(data);
                    }
                    datalist.trimToSize();
                    add(node, datalist);
                }
                node.createPath(path);
            }
        }

        @Override
        public int getLevel()
        {
            return level;
        }

    }
}
