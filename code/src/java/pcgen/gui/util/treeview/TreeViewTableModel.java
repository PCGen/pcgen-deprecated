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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pcgen.gui.util.treetable.AbstractTreeTableModel;
import pcgen.gui.util.treetable.DefaultSortableTreeTableNode;
import pcgen.gui.util.treetable.SortableTreeTableModel;
import pcgen.gui.util.treetable.TreeTableNode;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public final class TreeViewTableModel<E> extends AbstractTreeTableModel
        implements SortableTreeTableModel
{

    private final Map<TreeView<E>, TreeViewNode> viewMap = new HashMap<TreeView<E>, TreeViewNode>();
    private final Map<E, List<?>> dataMap = new HashMap<E, List<?>>();
    private final NameViewColumn namecolumn = new NameViewColumn();
    private final DataView<E> dataview;
    private final List<? extends DataViewColumn> datacolumns;
    private TreeView<E> selectedView = null;

    public TreeViewTableModel(DataView<E> dataView)
    {
        this.dataview = dataView;
        this.datacolumns = dataview.getDataColumns();
    }

    public void setData(Collection<E> data)
    {
        populateDataMap(data);
        viewMap.clear();
        setSelectedTreeView(selectedView);
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
        if (view != null)
        {
            this.selectedView = view;
            TreeViewNode node = viewMap.get(view);
            if (node == null)
            {
                node = new TreeViewNode();
                for (E element : dataMap.keySet())
                {
                    for (TreeViewPath<E> path : view.getPaths(element))
                    {
                        node.createPath(path);
                    }
                }
                viewMap.put(view, node);
            }
            setRoot(node);
        }
    }

    public int getColumnCount()
    {
        return datacolumns.size() + 1;
    }

    @Override
    public Class<?> getColumnClass(int column)
    {
        return getDataColumn(column).getDataClass();
    }

    @Override
    public String getColumnName(int column)
    {
        return getDataColumn(column).getName();
    }

    private DataViewColumn getDataColumn(int column)
    {
        switch (column)
        {
            case 0:
                return namecolumn;
            default:
                return datacolumns.get(column - 1);
        }
    }

    public void sortModel(Comparator<List<?>> comparator)
    {
        viewMap.get(selectedView).sortChildren(new TreeNodeComparator(comparator));
        reload();
    }

    private final class NameViewColumn implements DataViewColumn
    {

        public String getName()
        {
            return selectedView.getViewName();
        }

        public Class<?> getDataClass()
        {
            return TreeTableNode.class;
        }

    }

    private final class TreeViewNode extends DefaultSortableTreeTableNode
    {

        private final int level;

        public TreeViewNode()
        {
            super();
            this.level = 0;
        }

        private TreeViewNode(int level, List<Object> data)
        {
            super(data);
            this.level = level;
        }

        public void createPath(TreeViewPath<E> path)
        {
            if (path.getPathCount() > level)
            {
                Object key = path.getPathComponent(level);
                TreeViewNode node = null;
                if (children != null)
                {
                    for (int i = 0; i < children.size(); i++)
                    {
                        TreeViewNode child = (TreeViewNode) children.get(i);
                        if (child.getValueAt(0).equals(key))
                        {
                            node = child;
                            break;
                        }
                    }
                }
                if (node == null)
                {
                    ArrayList<Object> datalist = new ArrayList<Object>(1);
                    datalist.add(key);
                    List<?> data = dataMap.get(key);
                    if (data != null)
                    {
                        datalist.addAll(data);
                    }
                    datalist.trimToSize();
                    node = new TreeViewNode(level + 1, datalist);
                    add(node);
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
