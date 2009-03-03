/*
 * JCheckBoxTreeViewPane.java
 * Copyright 2009 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Feb 28, 2009, 6:15:01 PM
 */
package pcgen.gui.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import pcgen.gui.util.treeview.DataView;
import pcgen.gui.util.treeview.DataViewColumn;
import pcgen.gui.util.treeview.TreeView;
import pcgen.gui.util.treeview.TreeViewPath;
import pcgen.gui.util.treeview.TreeViewTableModel;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class JCheckBoxTreeViewPane extends JTreeViewPane
{

	@Override
	protected <T> TreeViewTableModel<T> createDefaultTreeViewTableModel(
			DataView<T> dataView)
	{
		return super.createDefaultTreeViewTableModel(dataView);
	}

	private class CheckBoxTreeViewTableModel<T> extends TreeViewTableModel<T>
	{

		private final Map<TreeView<? super T>, List<TreeViewPath<? super T>>> viewMap;
		private List<T> selectedData;

		public CheckBoxTreeViewTableModel(DataView<T> dataView)
		{
			super(dataView);
			this.viewMap = new HashMap<TreeView<? super T>, List<TreeViewPath<? super T>>>();
			this.selectedData = new ArrayList<T>();
		}

		public boolean isSelected(Object treeNode)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeNode;
			for (TreeViewPath<? super T> path : getSelectedPaths())
			{
				int level = node.getLevel();
				if (path.getPathCount() < level)
				{
					continue;
				}
				if (path.getPathComponent(level - 1).equals(node.getUserObject()))
				{
					return true;
				}
			}
			return false;
		}

		private List<TreeViewPath<? super T>> getSelectedPaths()
		{
			TreeView<? super T> treeView = getSelectedTreeView();
			if (!viewMap.containsKey(treeView))
			{
				List<TreeViewPath<? super T>> selectedPaths = new ArrayList<TreeViewPath<? super T>>();
				for (T data : selectedData)
				{
					selectedPaths.addAll(treeView.getPaths(data));
				}
				viewMap.put(treeView, selectedPaths);
			}
			return viewMap.get(treeView);
		}

		public void setSelected(DefaultMutableTreeNode node, boolean selected)
		{
			
		}

	}
}
