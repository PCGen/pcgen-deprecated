/*
 * FilteredTreeViewPanel.java
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Jun 22, 2008, 3:55:32 PM
 */
package pcgen.gui.tools;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import pcgen.gui.filter.Filter;
import pcgen.gui.filter.FilterPanel;
import pcgen.gui.filter.FilterPanelListener;
import pcgen.gui.util.GenericListModelWrapper;
import pcgen.gui.util.JTreeViewPane;
import pcgen.gui.util.SwingWorker;
import pcgen.gui.util.event.GenericListDataEvent;
import pcgen.gui.util.event.GenericListDataListener;
import pcgen.gui.util.treeview.TreeViewModel;
import pcgen.gui.util.treeview.TreeViewModelWrapper;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class FilteredTreeViewPanel extends JPanel
{

    private FilterPanel filterPanel;
    private JTreeViewPane treeViewPane;

    public FilteredTreeViewPanel()
    {
        setLayout(new BorderLayout());

        filterPanel = new FilterPanel();
        add(filterPanel, BorderLayout.PAGE_START);

        treeViewPane = new JTreeViewPane();
        add(treeViewPane, BorderLayout.CENTER);
    }

    /**
     * delegates to JTable.getSelectionModel()
     * @return the row selection model
     */
    public ListSelectionModel getSelectionModel()
    {
        return treeViewPane.getSelectionModel();
    }

    /**
     * delegates to JTreeViewPane.getSelectedData()
     * @param b
     */
    public List<Object> getSelectedData()
    {
        return treeViewPane.getSelectedData();
    }

    /**
     * delegates to JTablePane.getTransferHandler()
     * @param b
     */
    @Override
    public TransferHandler getTransferHandler()
    {
        return treeViewPane.getTransferHandler();
    }

    /**
     * delegates to JTablePane.setTransferHandler()
     * @param b
     */
    @Override
    public void setTransferHandler(TransferHandler newHandler)
    {
        treeViewPane.setTransferHandler(newHandler);
    }

    /**
     * delegates to JTablePane.getDragEnabled()
     * @param b
     */
    public boolean getDragEnabled()
    {
        return treeViewPane.getDragEnabled();
    }

    /**
     * delegates to JTablePane.setDragEnabled()
     * @param b
     */
    public void setDragEnabled(boolean b)
    {
        treeViewPane.setDragEnabled(b);
    }

    public <T> void setTreeViewModel(FilterableTreeViewModel<T> model)
    {
        filterPanel.setFilterClass(model.getFilterClass());

        TreeViewDisplay<T> displayModel = new TreeViewDisplay<T>(model);
        treeViewPane.setTreeViewModel(displayModel);
        filterPanel.setFilterPanelListener(displayModel);
    }

    private class TreeViewDisplay<E> extends TreeViewModelWrapper<E>
            implements FilterPanelListener, GenericListDataListener<E>
    {

        public TreeViewDisplay(TreeViewModel<E> model)
        {
            super(model);
            model.getDataModel().addGenericListDataListener(this);
        }

        public void intervalRemoved(GenericListDataEvent e)
        {
            dataModel.removeAll(e.getData());
        }

        @SuppressWarnings("unchecked")
        public void intervalAdded(GenericListDataEvent e)
        {
            List<E> sublist = dataModel.subList(dataModel.getSize(),
                                                dataModel.getSize());
            new FilterUpdater(sublist, (List<E>) e.getData(),
                              filterPanel.getFilter(),
                              treeViewPane.getQuickSearchMode()).start();
        }

        public void contentsChanged(GenericListDataEvent e)
        {
            applyFilter(filterPanel.getFilter(),
                        treeViewPane.getQuickSearchMode());
        }

        public void applyFilter(Filter filter, boolean quicksearch)
        {
            new FilterUpdater(dataModel,
                              new GenericListModelWrapper<E>(treeviewModel.getDataModel()),
                              filter,
                              quicksearch).start();
        }

        private class FilterUpdater extends SwingWorker<List<E>>
        {

            private Collection<E> baseData;
            private Collection<E> modelData;
            private boolean quicksearch;
            private Filter filter;

            public FilterUpdater(Collection<E> modelData,
                                  Collection<E> baseData, Filter filter,
                                  boolean quicksearch)
            {
                this.baseData = baseData;
                this.modelData = modelData;
                this.filter = filter;
                this.quicksearch = quicksearch;
            }

            @Override
            @SuppressWarnings("unchecked")
            public List<E> construct()
            {
                List<E> data = new ArrayList<E>();
                for (E element : baseData)
                {
                    if (filter.accept(element))
                    {
                        data.add(element);
                    }
                }
                return data;
            }

            @Override
            public void finished()
            {
                List<E> value = getValue();
                if (value.size() != baseData.size())
                {
                    modelData.clear();
                    treeViewPane.setQuickSearchMode(quicksearch);
                    modelData.addAll(value);
                }
                else
                {
                    treeViewPane.setQuickSearchMode(quicksearch);
                }
            }

        }
    }
}
