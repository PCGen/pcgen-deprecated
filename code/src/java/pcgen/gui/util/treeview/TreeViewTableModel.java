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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.JTree;
import pcgen.gui.util.treetable.AbstractTreeTableModel;
import pcgen.gui.util.treetable.SortableTreeTableModel;
import pcgen.gui.util.treetable.SortableTreeTableNode;
import pcgen.gui.util.treetable.TreeTableNode;
import pcgen.util.CollectionMaps;
import pcgen.util.ListMap;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public class TreeViewTableModel<E> extends AbstractTreeTableModel
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

    public final void setData(Collection<E> data)
    {
        populateDataMap(data);
        viewMap.clear();
        setSelectedTreeView(selectedView);
    }

    private void populateDataMap(Collection<E> data)
    {
        dataMap.keySet().retainAll(data);
        for (E obj : data)
        {
            if (!dataMap.containsKey(obj))
            {
                dataMap.put(obj, dataview.getData(obj));
            }
        }
    }

    public final TreeView<E> getSelectedTreeView()
    {
        return selectedView;
    }

    public final void setSelectedTreeView(TreeView<E> view)
    {
        if (view != null)
        {
            this.selectedView = view;
            TreeViewNode node = viewMap.get(view);
            if (node == null)
            {
                Vector<TreeViewPath<E>> paths = new Vector<TreeViewPath<E>>();
                for (E element : dataMap.keySet())
                {
                    for (TreeViewPath<E> path : view.getPaths(element))
                    {
                        paths.add(path);
                    }
                }
                node = new TreeViewNode(paths);
                viewMap.put(view, node);
            }
            setRoot(node);
        }
    }

    public final int getColumnCount()
    {
        return datacolumns.size() + 1;
    }

    @Override
    public final Class<?> getColumnClass(int column)
    {
        return getDataColumn(column).getDataClass();
    }

    @Override
    public final String getColumnName(int column)
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

    public final void sortModel(Comparator<List<?>> comparator)
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

        public Visibility getVisibility()
        {
            return Visibility.ALWAYS_VISIBLE;
        }

    }

    private final class TreeViewNode extends JTree.DynamicUtilTreeNode
            implements SortableTreeTableNode
    {

        private final int level;

        public TreeViewNode(Vector<TreeViewPath<E>> paths)
        {
            this(0, null, paths);
        }

        private TreeViewNode(int level, Object name,
                              Vector<TreeViewPath<E>> paths)
        {
            super(name, paths);
            this.level = level;
        }

        @Override
        public int getLevel()
        {
            return level;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void loadChildren()
        {
            loadedChildren = true;
            if (childValue != null)
            {
                ListMap<Object, TreeViewPath<E>, Vector<TreeViewPath<E>>> vectorMap = CollectionMaps.createListMap(HashMap.class,
                                                                                                                   Vector.class);
                Vector<TreeViewPath<E>> vector = (Vector<TreeViewPath<E>>) childValue;
                for (TreeViewPath<E> path : vector)
                {
                    if (path.getPathCount() > level)
                    {
                        Object key = path.getPathComponent(level);
                        vectorMap.add(key, path);
                    }
                }
                for (Object key : vectorMap.keySet())
                {
                    vector = vectorMap.get(key);
                    TreeViewNode child;
                    if (vector.size() == 1 &&
                            vector.firstElement().getPathCount() <= level + 1)
                    {
                        child = new TreeViewNode(level + 1, key, null);
                    }
                    else
                    {
                        child = new TreeViewNode(level + 1, key, vector);
                    }
                    add(child);
                }
                childValue = null;
            }
        }

        @SuppressWarnings("unchecked")
        public void sortChildren(Comparator<TreeTableNode> comparator)
        {
            if (!loadedChildren)
            {
                loadChildren();
            }
            if (children != null)
            {
                Collections.sort(children, comparator);
                for (Object obj : children)
                {
                    TreeViewNode child = (TreeViewNode) obj;
                    if (child.loadedChildren)
                    {
                        child.sortChildren(comparator);
                    }
                }
            }
        }

        public List<Object> getValues()
        {
            Vector<Object> list = new Vector<Object>(getColumnCount());
            list.add(userObject);
            List<?> data = dataMap.get(userObject);
            if (data != null)
            {
                list.addAll(data);
            }
            list.setSize(getColumnCount());
            return list;
        }

        public Object getValueAt(int column)
        {
            if (column == 0)
            {
                return userObject;
            }
            List<?> data = dataMap.get(userObject);
            if (data != null && data.size() >= column)
            {
                return data.get(column - 1);
            }
            return null;
        }

        public void setValueAt(Object value, int column)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
}
