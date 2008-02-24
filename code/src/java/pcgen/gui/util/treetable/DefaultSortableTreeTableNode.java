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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import pcgen.util.UnboundedArrayList;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public class DefaultSortableTreeTableNode extends DefaultMutableTreeNode
        implements SortableTreeTableNode
{

    protected Vector<List<Object>> childData = null;

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
                    data = new UnboundedArrayList<Object>(data);
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
                childData = new Vector<List<Object>>();
            }
            if (newChild.getParent() == this)
            {
                insert(newChild, childData.size() - 1);
            }
            else
            {
                insert(newChild, childData.size());
            }
            childData.add(null);
        }
        else
        {
            super.add(newChild);
        }
    }

    public void add(DefaultSortableTreeTableNode node, List<Object> data)
    {
        add(node);
        childData.setElementAt(data, childData.size() - 1);
    }

    @Override
    public void remove(int index)
    {
        if (childData != null && index < childData.size())
        {
            childData.removeElementAt(index);
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

    public List<Object> getValues()
    {
        DefaultSortableTreeTableNode parentNode = getSortableParent();
        if (parentNode != null)
        {
            return parentNode.getChildData(parentNode.getIndex(this));
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
        if (data != null)
        {
            return data.get(column);
        }
        return null;
    }

    public void setValueAt(Object value, int column)
    {
        List<Object> data = getValues();
        if (data != null)
        {
            data.set(column, value);
        }
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
        int index = 0;
        for (int x = 0; x < childData.size(); x++)
        {
            DefaultSortableTreeTableNode child = (DefaultSortableTreeTableNode) children.get(x);
            if (!child.isLeaf())
            {
                child.sortChildren(comparator);
                if (index != x)
                {
                    Collections.swap(children, index, x);
                    Collections.swap(childData, index, x);
                }
                index++;
            }
        }
        List sublist = children.subList(0, index);
        Collections.sort(sublist, new NodeComparator(comparator));
        sublist = childData.subList(0, index);
        Collections.sort(sublist, comparator);
        sublist = childData.subList(index, childData.size());
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
