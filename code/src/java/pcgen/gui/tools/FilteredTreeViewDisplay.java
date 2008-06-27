/*
 * FilteredTreeViewDisplay.java
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
import pcgen.gui.UIContext;
import pcgen.gui.filter.Filter;
import pcgen.gui.filter.FilterPanel;
import pcgen.gui.filter.FilterPanelListener;
import pcgen.gui.util.JTreeViewPane;
import pcgen.gui.util.SwingWorker;
import pcgen.gui.util.event.TreeViewModelEvent;
import pcgen.gui.util.treeview.TreeViewModel;
import pcgen.gui.util.treeview.TreeViewModelWrapper;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class FilteredTreeViewDisplay extends JPanel
{

    private FilterPanel filterPanel;
    private JTreeViewPane treeViewPane;

    public FilteredTreeViewDisplay(UIContext context)
    {
        setLayout(new BorderLayout());

        filterPanel = new FilterPanel(context);
        add(filterPanel, BorderLayout.PAGE_START);

        treeViewPane = new JTreeViewPane();
        add(treeViewPane, BorderLayout.CENTER);
    }

    public JTreeViewPane getTreeViewPane()
    {
        return treeViewPane;
    }

    public <T> void setTreeViewModel(Class<T> filterClass,
                                      TreeViewModel<T> model)
    {
        filterPanel.setFilterClass(filterClass);

        TreeViewDisplay<T> displayModel = new TreeViewDisplay<T>(model);
        treeViewPane.setTreeViewModel(displayModel);
        filterPanel.setFilterPanelListener(displayModel);
    }

    private class TreeViewDisplay<E> extends TreeViewModelWrapper<E>
            implements FilterPanelListener
    {

        private Filter oldfilter = null;

        public TreeViewDisplay(TreeViewModel<E> model)
        {
            super(model);
        }

        @SuppressWarnings("unchecked")
        public void applyFilter(Filter filter, boolean quicksearch)
        {
            new FilterUpdater(getModel().getData(), filter, quicksearch).start();
        }

        @Override
        public void dataChanged(TreeViewModelEvent<E> event)
        {
            if (oldfilter != null)
            {
                new FilterUpdater(event.getNewData(), oldfilter,
                                  treeViewPane.getQuickSearchMode()).start();
            }
        }

        private class FilterUpdater extends SwingWorker<List<E>>
        {

            private Collection<E> modelData;
            private boolean quicksearch;
            private Filter filter;

            public FilterUpdater(Collection<E> modelData, Filter filter,
                                  boolean quicksearch)
            {
                this.modelData = modelData;
                this.filter = filter;
                this.quicksearch = quicksearch;
            }

            @Override
            @SuppressWarnings("unchecked")
            public List<E> construct()
            {
                List<E> data = new ArrayList<E>();
                for (E element : modelData)
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
                oldfilter = filter;
                treeViewPane.setQuickSearchMode(quicksearch);
                setData(getValue());
            }

        }
    }
}
