/*
 * @(#)TreeTableModelAdapter.java    1.2 98/10/27
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package pcgen.gui.util;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;

/**
 * This is a wrapper class takes a TreeTableModel and implements
 * the table model interface. The implementation is trivial, with
 * all of the event dispatching support provided by the superclass:
 * the AbstractTableModel.
 *
 * @version 1.2 10/27/98
 *
 * @author Philip Milne
 * @author Scott Violet
 */
final class TreeTableModelAdapter extends AbstractTableModel
{

    private JTree tree;
    private TreeTableModel treeTableModel;
    private TreeModelListener modelListener;

    /**
     * Constructor
     * @param treeTableModel
     * @param tree
     */
    public TreeTableModelAdapter(TreeTableModel treeTableModel, JTree tree)
    {
	this.tree = tree;
	this.treeTableModel = treeTableModel;

	tree.addTreeExpansionListener(new TreeExpansionListener()
			      {
				  // Don't use fireTableRowsInserted() here;
				  // the selection model would get updated twice.
				  public void treeExpanded(TreeExpansionEvent event)
				  {
				      fireTableDataChanged();
				  }

				  public void treeCollapsed(TreeExpansionEvent event)
				  {
				      fireTableDataChanged();
				  }

			      });

	/**
	 * Install a TreeModelListener that can update the table when
	 * tree changes. We use delayedFireTableDataChanged as we can
	 * not be guaranteed the tree will have finished processing
	 * the event before us.
	 **/
	modelListener = new TreeModelListener()
	{

	    public void treeNodesChanged(TreeModelEvent e)
	    {
		fireTableDataChanged();
	    }

	    public void treeNodesInserted(TreeModelEvent e)
	    {
		fireTableDataChanged();
	    }

	    public void treeNodesRemoved(TreeModelEvent e)
	    {
		fireTableDataChanged();
	    }

	    public void treeStructureChanged(TreeModelEvent e)
	    {
		fireTableStructureChanged();
	    }

	};
	if (treeTableModel != null)
	{
	    treeTableModel.addTreeModelListener(modelListener);
	}
    }

    public void setTreeTableModel(TreeTableModel model)
    {
	if (treeTableModel != null)
	{
	    treeTableModel.removeTreeModelListener(modelListener);
	}
	treeTableModel = model;
	if (treeTableModel != null)
	{
	    treeTableModel.addTreeModelListener(modelListener);
	}
    }

    @Override
    public boolean isCellEditable(int row, int column)
    {
	if (treeTableModel == null)
	{
	    return false;
	}
	return treeTableModel.isCellEditable(nodeForRow(row), column);
    }

    public Class<?> getColumnClass(int column)
    {
	if (treeTableModel == null)
	{
	    return Object.class;
	}
	return treeTableModel.getColumnClass(column);
    }

    // Wrappers, implementing TableModel interface.
    public int getColumnCount()
    {
	if (treeTableModel == null)
	{
	    return 0;
	}
	return treeTableModel.getColumnCount();
    }

    @Override
    public String getColumnName(int column)
    {
	if (treeTableModel == null)
	{
	    return null;
	}
	return treeTableModel.getColumnName(column);
    }

    public int getRowCount()
    {
	return tree.getRowCount();
    }

    @Override
    public void setValueAt(Object value, int row, int column)
    {
	if (treeTableModel == null)
	{
	    return;
	}
	treeTableModel.setValueAt(value, nodeForRow(row), column);
    }

    public Object getValueAt(int row, int column)
    {
	if (treeTableModel == null)
	{
	    return null;
	}
	return treeTableModel.getValueAt(nodeForRow(row), column);
    }

    private Object nodeForRow(int row)
    {
	TreePath treePath = tree.getPathForRow(row);
	if (treePath != null)
	{
	    return treePath.getLastPathComponent();
	}
	return null;
    }

}
