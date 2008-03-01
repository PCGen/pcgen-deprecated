/*
 * DefaultSortableTreeTableNode.java
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
 * Created on Feb 21, 2008, 3:11:09 PM
 */
package pcgen.gui.util.treetable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public class DefaultSortableTreeTableNode extends DefaultMutableTreeNode
        implements SortableTreeTableNode
{

    protected ArrayList<List<Object>> childData = null;
    private int index = -1;

    public DefaultSortableTreeTableNode()
    {
        super();
    }

    public DefaultSortableTreeTableNode(TreeNode node)
    {
        for (int x = 0; x < node.getChildCount(); x++)
        {
            TreeNode treeNode = node.getChildAt(x);
            DefaultSortableTreeTableNode child = new DefaultSortableTreeTableNode(treeNode);
            if (treeNode instanceof TreeTableNode)
            {
                TreeTableNode treeTableNode = (TreeTableNode) treeNode;
                List<Object> data = treeTableNode.getValues();
                if (data != null)
                {
                    data = new ArrayList<Object>(data);
                }
                add(child, data);
            }
            else
            {
                add(child);
            }
        }
    }

    @Override
    public void add(MutableTreeNode newChild)
    {
        if (newChild instanceof DefaultSortableTreeTableNode)
        {
            if (childData == null)
            {
                childData = new ArrayList<List<Object>>();
            }
            if (newChild.getParent() == this)
            {
                insert(newChild, childData.size() - 1);
            }
            else
            {
                insert(newChild, childData.size());
            }
        }
        else
        {
            super.add(newChild);
        }
    }

    public void add(DefaultSortableTreeTableNode node, List<Object> data)
    {
        add(node);
        childData.set(childData.size() - 1, data);
    }

    @Override
    public void insert(MutableTreeNode child, int i)
    {
        if (child instanceof DefaultSortableTreeTableNode)
        {
            ((DefaultSortableTreeTableNode) child).setIndex(i);
            if (childData == null)
            {
                childData = new ArrayList<List<Object>>();
            }
            childData.add(i, null);
        }
        super.insert(child, i);
    }

    @Override
    public void remove(int index)
    {
        if (childData != null && index < childData.size())
        {
            childData.remove(index);
        }
        super.remove(index);
    }

    protected List<Object> getChildData(int childindex)
    {
        if (childData != null)
        {
            return childData.get(childindex);
        }
        return null;
    }

    private int getIndex()
    {
        return index;
    }

    private void setIndex(int index)
    {
        this.index = index;
    }

    public List<Object> getValues()
    {
        DefaultSortableTreeTableNode parentNode = getSortableParent();
        if (parentNode != null)
        {
            return parentNode.getChildData(getIndex());
        }
        return null;
    }

    public DefaultSortableTreeTableNode getSortableParent()
    {
        if (parent != null && parent instanceof DefaultSortableTreeTableNode)
        {
            return (DefaultSortableTreeTableNode) parent;
        }
        return null;
    }

    public Object getValueAt(int column)
    {
        List<Object> data = getValues();
        if (data != null && data.size() > column)
        {
            return data.get(column);
        }
        return null;
    }

    public void setValueAt(Object value, int column)
    {
        List<Object> data = getValues();
        if (data == null)
        {
            data = new ArrayList<Object>();
            getSortableParent().add(this, data);
        }
        while (data.size() <= column)
        {
            data.add(null);
        }
        data.set(column, value);
    }

    @Override
    public String toString()
    {
        Object name = getValueAt(0);
        if (name != null)
        {
            return name.toString();
        }
        return super.toString();
    }

    @SuppressWarnings("unchecked")
    public void sortChildren(Comparator<List<?>> comparator)
    {

        int i = 0;
        for (int x = 0; x < childData.size(); x++)
        {
            DefaultSortableTreeTableNode child = (DefaultSortableTreeTableNode) children.get(x);
            // this makes sure that non-leaf nodes are the first nodes in the child array
            if (!child.isLeaf())
            {
                child.sortChildren(comparator);
                if (i != x)
                {
                    Collections.swap(children, i, x);
                    Collections.swap(childData, i, x);
                    child.setIndex(i);
                    child = (DefaultSortableTreeTableNode) children.get(x);
                    child.setIndex(x);
                }
                i++;
            }
        }
        List sublist = children.subList(0, i);
        Collections.sort(sublist, new NodeComparator(comparator));
        sublist = childData.subList(0, i);
        Collections.sort(sublist, comparator);
        sublist = childData.subList(i, childData.size());
        Collections.sort(sublist, comparator);

    }

    private static class NodeComparator implements Comparator<DefaultSortableTreeTableNode>
    {

        private Comparator<List<?>> comparator;

        public NodeComparator(Comparator<List<?>> comparator)
        {
            this.comparator = comparator;
        }

        public int compare(DefaultSortableTreeTableNode o1,
                            DefaultSortableTreeTableNode o2)
        {
            return comparator.compare(o1.getValues(), o2.getValues());
        }

    }
}
