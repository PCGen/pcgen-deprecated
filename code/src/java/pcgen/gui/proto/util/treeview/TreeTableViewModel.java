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
package pcgen.gui.proto.util.treeview;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pcgen.gui.proto.util.AbstractTreeTableModel;



/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public class TreeTableViewModel<E> extends AbstractTreeTableModel
{

    private final DataView dataview;
    private final EnumSet<? extends TreeView<E>> treeviews;
    private final List<String> headerNames;
    private final Map<E, List<?>> dataMap = new HashMap<E, List<?>>();
    private final Map<TreeView<E>, TreeViewNode<E>> viewMap = new HashMap<TreeView<E>, TreeViewNode<E>>();

    public TreeTableViewModel(EnumSet<? extends TreeView<E>> treeviews, DataView<E> dataview, Collection<E> data)
    {
	this.treeviews = treeviews;
	headerNames = dataview.getDataNames();
	this.dataview = dataview;
	setData(data);
    }

    public void setData(Collection<E> data)
    {
	populateDataMap(data);
	populateViewMap(data);
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
	for(TreeView<E> view : treeviews)
	{
	    //viewMap.put(view, new TreeViewNode<E> )
	}
    }

    public int getColumnCount()
    {
	return headerNames.size();
    }

    public String getColumnName(int column)
    {
	return headerNames.get(column);
    }

    public Object getValueAt(Object node, int column)
    {
	return ((TreeViewNode<E>) node).getItem();//INCORRECT TODO:FIX
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
