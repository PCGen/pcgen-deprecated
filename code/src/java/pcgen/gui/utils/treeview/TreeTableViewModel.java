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
package pcgen.gui.utils.treeview;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import pcgen.gui.utils.AbstractTreeTableModel;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public class TreeTableViewModel extends AbstractTreeTableModel
{

    public TreeTableViewModel()
    {
	super(null);
    }
    public int getColumnCount()
    {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getColumnName(int column)
    {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object getValueAt(Object node, int column)
    {
	throw new UnsupportedOperationException("Not supported yet.");
    }

//    public Object getChild(Object parent, int index)
//    {
//	return ((TreeViewNode) parent).getChildAt(index);
//    }
//
//    public int getChildCount(Object parent)
//    {
//	return ((TreeViewNode) parent).getChildCount();
//    }

    private class TreeViewNode<E> implements TreeNode
    {

	private Map<TreeViewPath, TreeViewNode<E>> pathMap;
	private TreeViewPathComparator<E> comparator;
	private TreeViewNode parent = null;
	private final Object item;
	private int depth;

	public TreeViewNode(TreeViewPathComparator<E> comparator)
	{
	    this(0, comparator, null);
	}

	private TreeViewNode(int depth, TreeViewPathComparator comparator, Object item)
	{
	    this.pathMap = new TreeMap<TreeViewPath, TreeViewNode<E>>(comparator);
	    this.comparator = comparator;
	    this.depth = depth;
	    this.item = item;
	}

	public void setTreeViewPathComparator(TreeViewPathComparator<E> comparator)
	{

	}

	public boolean remap(TreeViewPathComparator comparator)
	{
	    Map<TreeViewPath, TreeViewNode<E>> map = new TreeMap<TreeViewPath, TreeViewNode<E>>(comparator);
	    map.putAll(pathMap);
	    boolean changed = !map.equals(pathMap);
	    for(TreeViewPath path : pathMap.keySet())
	    {
		TreeViewNode child = pathMap.get(path);
		if(child.remap(comparator) && !changed)
		    TreeTableViewModel.this.fireTreeStructureChanged(child, child.getPath());
	    }
	    if(!map.equals(pathMap)){
		pathMap = map;
		return true;
	    }
	    return false;
	}

	public void createChild(TreeViewPath<E> path)
	{
	    if (path.getPathCount() < depth)
	    {
		TreeViewPath key = path.getParentPath(depth);
		TreeViewNode<E> node = pathMap.get(key);
		if (node == null)
		{
		    node = new TreeViewNode(depth + 1, comparator, key.getLastPathComponent());
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
	
	private TreePath getPath()
	{
	    Object[] path = new Object[depth+1];
	    return getPath(path);
	}
	
	private TreePath getPath(Object[] path)
	{
	    path[depth] = this;
	    if(parent == null)
		return new TreePath(path);
	    else
		return parent.getPath(path);
	}
	
	public TreeViewNode<E> getParent()
	{
	    return parent;
	}

	private void setParent(TreeViewNode<E> parent)
	{
	    this.parent = parent;
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

    }
}
