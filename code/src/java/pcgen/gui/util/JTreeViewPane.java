/*
 * JTreeViewPane.java
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
 * Created on Feb 29, 2008, 1:32:23 AM
 */
package pcgen.gui.util;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import pcgen.gui.util.event.TreeViewModelEvent;
import pcgen.gui.util.event.TreeViewModelListener;
import pcgen.gui.util.table.DefaultDynamicTableColumnModel;
import pcgen.gui.util.table.DynamicTableColumnModel;
import pcgen.gui.util.table.SortableTableModel;
import pcgen.gui.util.treeview.*;
import pcgen.gui.util.treeview.DataViewColumn.Visibility;
import pcgen.util.CollectionMaps;
import pcgen.util.ListMap;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public class JTreeViewPane extends JTablePane
{

    private static final TreeView searchView = new TreeView()
    {

        public String getViewName()
        {
            return "Search";
        }

        @SuppressWarnings("unchecked")
        public List getPaths(Object pobj)
        {
            return Collections.singletonList(new TreeViewPath(pobj));
        }

    };
    private TreeViewModelListener modelListener;
    private TreeViewTableModel<?> treetableModel;
    private TreeViewModel<?> viewModel;
    private JPopupMenu treeviewMenu;
    private TreeView tempView;
    private boolean searchMode = false;

    public JTreeViewPane()
    {
        super(new JTreeTable());
        JTreeTable table = getTable();
        table.setTableHeader(new JTreeViewHeader());
        table.setAutoCreateColumnsFromModel(false);
    }

    public JTreeViewPane(TreeViewModel<?> viewModel)
    {
        this();
        setTreeViewModel(viewModel);
    }

    @Override
    protected JTreeTable getTable()
    {
        return (JTreeTable) super.getTable();
    }

    @Override
    public void setModel(SortableTableModel model)
    {
        throw new UnsupportedOperationException();
    }

    public TreeViewModel<?> getTreeViewModel()
    {
        return viewModel;
    }

    public <T> void setTreeViewModel(TreeViewModel<T> viewModel)
    {
        DataView<T> dataView = viewModel.getDataView();
        final TreeViewTableModel<T> model = new TreeViewTableModel<T>(dataView);
        this.treetableModel = model;
        if (this.viewModel != null)
        {
            this.viewModel.removeTreeViewModelListener(modelListener);
        }

        TreeViewModelListener<T> listener = new TreeViewModelListener<T>()
        {

            public void dataChanged(TreeViewModelEvent<T> event)
            {
                model.setData(event.getNewData());
            }

        };
        this.viewModel = viewModel;
        this.modelListener = listener;
        viewModel.addTreeViewModelListener(listener);

        treeviewMenu = new JPopupMenu();
        ButtonGroup group = new ButtonGroup();
        List<? extends TreeView<T>> views = viewModel.getTreeViews();
        TreeView<T> startingView = views.get(viewModel.getDefaultTreeViewIndex());
        for (TreeView<?> treeview : views)
        {
            JMenuItem item = new JRadioButtonMenuItem(new ChangeViewAction(treeview));
            item.setSelected(treeview == startingView);
            group.add(item);
            treeviewMenu.add(item);
        }
        model.setData(viewModel.getData());
        model.setSelectedTreeView(startingView);
        getTable().setTreeTableModel(model);
        setColumnModel(createTableColumnModel(startingView, dataView));
    }

    private DynamicTableColumnModel createTableColumnModel(TreeView<?> startingView,
                                                            DataView<?> dataView)
    {
        @SuppressWarnings("unchecked")
        ListMap<Visibility, DataViewColumn, List<DataViewColumn>> listMap =
                CollectionMaps.createListMap(HashMap.class, ArrayList.class);
        for ( DataViewColumn column : dataView.getDataColumns())
        {
            listMap.add(column.getVisibility(), column);
        }

        List<DataViewColumn> columns = listMap.get(Visibility.ALWAYS_VISIBLE);
        if (columns == null)
        {
            columns = Collections.emptyList();
        }
        DynamicTableColumnModel model = new DefaultDynamicTableColumnModel(columns.size() +
                                                                           1);
        TableColumn tableColumn = new TableColumn();
        tableColumn.setHeaderValue(startingView.getViewName());
        model.addColumn(tableColumn);

        int index = 1;
        for ( DataViewColumn column : columns)
        {
            tableColumn = new TableColumn(index++);
            tableColumn.setHeaderValue(column.getName());
            model.addColumn(tableColumn);
        }

        columns = listMap.get(Visibility.INITIALLY_VISIBLE);
        if (columns != null)
        {
            for ( DataViewColumn column : columns)
            {
                tableColumn = new TableColumn(index++);
                tableColumn.setHeaderValue(column.getName());
                model.addColumn(tableColumn);
                model.setVisible(tableColumn, true);
            }
        }
        columns = listMap.get(Visibility.INITIALLY_INVISIBLE);
        if (columns != null)
        {
            for ( DataViewColumn column : columns)
            {
                tableColumn = new TableColumn(index++);
                tableColumn.setHeaderValue(column.getName());
                model.addColumn(tableColumn);
            }
        }
        return model;
    }

    public boolean getQuickSearchMode()
    {
        return searchMode;
    }

    @SuppressWarnings("unchecked")
    public void setQuickSearchMode(boolean searchMode)
    {
        if (this.searchMode != searchMode)
        {
            this.searchMode = searchMode;
            if (searchMode)
            {
                tempView = treetableModel.getSelectedTreeView();
                setTreeView(searchView);
            }
            else
            {
                setTreeView(tempView);
            }
        }
    }

    public List<Object> getSelectedData()
    {
        TreePath[] paths = getTable().getTree().getSelectionPaths();
        List<Object> data = new ArrayList<Object>(paths.length);
        for (TreePath path : paths)
        {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            data.add(node.getUserObject());
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    private void setTreeView(TreeView view)
    {
        //make sure that the original dynamictableModel is not changed
        JTreeTable table = getTable();
        TableColumn viewColumn = table.getColumn(treetableModel.getSelectedTreeView().getViewName());
        treetableModel.setSelectedTreeView(view);
        viewColumn.setHeaderValue(view.getViewName());
        table.sortModel();
    }

    private class JTreeViewHeader extends JTableSortingHeader
    {

        public JTreeViewHeader()
        {
            super(JTreeViewPane.this.getTable());
        }

        @Override
        public void mouseClicked(MouseEvent e)
        {
            if (!treeviewMenu.isVisible())
            {
                super.mouseClicked(e);
            }
        }

        @Override
        public void mousePressed(MouseEvent e)
        {
            super.mousePressed(e);
            maybeShowPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {

            super.mouseReleased(e);
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e)
        {
            if (!searchMode && e.isPopupTrigger() && getTrackedColumn().getHeaderValue() ==
                    treetableModel.getSelectedTreeView().getViewName())
            {
                TableColumnModel columnmodel = getColumnModel();
                Rectangle rect = getHeaderRect(columnmodel.getColumnIndexAtX(e.getX()));
                treeviewMenu.setPopupSize(rect.width,
                                          treeviewMenu.getPreferredSize().height);
                treeviewMenu.show(getTable(),
                                  rect.x, rect.y);
            }
        }

    }

    private class ChangeViewAction extends AbstractAction
    {

        private TreeView view;

        public ChangeViewAction(TreeView view)
        {
            super(view.getViewName());
            this.view = view;
        }

        public void actionPerformed(ActionEvent e)
        {
            setTreeView(view);
        }

    }
}
