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
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import pcgen.gui.util.event.TreeViewModelEvent;
import pcgen.gui.util.event.TreeViewModelListener;
import pcgen.gui.util.table.SortableTableModel;
import pcgen.gui.util.treeview.TreeView;
import pcgen.gui.util.treeview.TreeViewModel;
import pcgen.gui.util.treeview.TreeViewTableModel;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public class JTreeViewPane extends JTablePane
{

    private TreeViewModelListener listener;
    private TreeViewTableModel<?> treetableModel;
    private TreeViewModel<?> viewModel;
    private JPopupMenu treeviewMenu;

    public JTreeViewPane()
    {
        super(new JTreeTable());
        getTable().setTableHeader(new JTreeViewHeader());
    }

    public <T> JTreeViewPane(TreeViewModel<T> viewModel)
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
    //TODO: log something
    }

    @SuppressWarnings("unchecked")
    public <T> void setTreeViewModel(TreeViewModel<T> viewModel)
    {
        final TreeViewTableModel<T> model = new TreeViewTableModel<T>(viewModel.getDataView());
        this.treetableModel = model;
        if (this.viewModel != null)
        {
            this.viewModel.removeTreeViewModelListener(listener);
        }
        this.viewModel = viewModel;
        this.viewModel.addTreeViewModelListener(listener = new TreeViewModelListener<T>()
                                        {

                                            public void dataChanged(TreeViewModelEvent<T> event)
                                            {
                                                model.setData(event.getNewData());
                                            }

                                        });
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
            if (e.isPopupTrigger() && getTrackedColumn().getHeaderValue() ==
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

    @SuppressWarnings("unchecked")
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
            //make sure that the original dynamictableModel is not changed
            JTreeTable table = getTable();
            TableColumnModel old = table.getColumnModel();
            table.setColumnModel(new DefaultTableColumnModel());
            TableColumn viewColumn = old.getColumn(old.getColumnIndex(treetableModel.getSelectedTreeView().getViewName()));
            treetableModel.setSelectedTreeView(view);
            viewColumn.setHeaderValue(view.getViewName());
            table.setColumnModel(old);
            table.sortModel();
        }

    }
}
