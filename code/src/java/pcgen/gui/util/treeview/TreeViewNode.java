/*
 * TreeViewNode.java
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
 * Created on Feb 11, 2008, 4:39:36 PM
 */
package pcgen.gui.util.treeview;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public class TreeViewNode<E> implements TreeNode
{

    private Map<TreeViewPath, TreeViewNode<E>> pathMap;
    private TreeViewPathComparator<E> comparator;
    private TreeViewNode parent = null;
    private TreePath treepath = null;
    private final Object item;
    private int depth;

    TreeViewNode(TreeViewPathComparator<E> comparator)
    {
        this(0, comparator, null);
        treepath = new TreePath(this);
    }

    private TreeViewNode(int depth, TreeViewPathComparator comparator,
                          Object item)
    {
        this.pathMap = new TreeMap<TreeViewPath, TreeViewNode<E>>(comparator);
        this.comparator = comparator;
        this.depth = depth;
        this.item = item;
    }

    TreeViewPathComparator<E> getTreeViewPathComparator()
    {
        return comparator;
    }

    void setTreeViewPathComparator(TreeViewPathComparator<E> comparator)
    {
        resetMap(comparator);
    }

    private void resetMap(TreeViewPathComparator comparator)
    {
        Map<TreeViewPath, TreeViewNode<E>> map = new TreeMap<TreeViewPath, TreeViewNode<E>>(comparator);
        map.putAll(pathMap);
        if (!map.equals(pathMap))
        {
            pathMap = map;
        }
        for (TreeViewPath path : pathMap.keySet())
        {
            TreeViewNode child = pathMap.get(path);
            child.resetMap(comparator);
        }
    }

    void createChild(TreeViewPath<E> path)
    {
        if (path.getPathCount() > depth)
        {
            
            TreeViewPath key = path.getParentPath(depth);
            TreeViewNode<E> node = pathMap.get(key);
            if (node == null)
            {
                node = new TreeViewNode(depth + 1, comparator,
                                        key.getLastPathComponent());
                node.setParent(this);
                pathMap.put(key, node);
            }
            node.createChild(path);
        }
    }

    public Object getItem()
    {
        return item;
    }

    public TreeNode getChildAt(int childIndex)
    {
        Iterator<TreeViewNode<E>> it = pathMap.values().iterator();
        for (int i = 0; i < childIndex; i++)
        {
            if (it.hasNext())
            {
                it.next();
            }
            else
            {
                throw new IndexOutOfBoundsException("childIndex: " + childIndex);
            }
        }
        return it.next();
    }

    public int getChildCount()
    {
        return pathMap.size();
    }

    public TreePath getTreePath()
    {
        return treepath;
    }

    public TreeViewNode<E> getParent()
    {
        return parent;
    }

    private void setParent(TreeViewNode<E> parent)
    {
        this.parent = parent;
        this.treepath = parent.getTreePath().pathByAddingChild(this);
    }

    public int getIndex(TreeNode node)
    {
        Iterator<TreeViewNode<E>> it = pathMap.values().iterator();
        for (int i = 0; it.hasNext(); i++)
        {
            if (it.next().equals(node))
            {
                return i;
            }
        }
        return -1;
    }

    public boolean getAllowsChildren()
    {
        return true;
    }

    public boolean isLeaf()
    {
        return pathMap.isEmpty();
    }

    public Enumeration<TreeViewNode<E>> children()
    {
        return Collections.enumeration(pathMap.values());
    }

    @Override
    public String toString()
    {
        if (item == null)
        {
            return super.toString();
        }
        else
        {
            return item.toString();
        }
    }

}
