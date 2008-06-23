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
import java.util.List;
import javax.swing.JPanel;
import pcgen.gui.core.UIContext;
import pcgen.gui.filter.Filter;
import pcgen.gui.filter.FilterPanel;
import pcgen.gui.filter.FilterPanelListener;
import pcgen.gui.util.JTreeViewPane;
import pcgen.gui.util.treeview.AbstractTreeViewModel;
import pcgen.gui.util.treeview.DataView;
import pcgen.gui.util.treeview.TreeView;
import pcgen.gui.util.treeview.TreeViewModel;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class FilteredTreeViewDisplay extends JPanel
{

    private final UIContext context;
    private FilterPanel filterPanel;
    private JTreeViewPane treeViewPane;

    public FilteredTreeViewDisplay(UIContext context)
    {
        this.context = context;
        initComponents();
    }

    private void initComponents()
    {
        setLayout(new BorderLayout());

        filterPanel = new FilterPanel(context);
        add(filterPanel, BorderLayout.PAGE_START);

        treeViewPane = new JTreeViewPane();
        add(treeViewPane, BorderLayout.CENTER);
    }

    public <T> void setTreeViewModel(Class<T> filterClass,
                                      TreeViewModel<T> model)
    {
        filterPanel.setFilterClass(filterClass);
        
        TreeViewDisplay<T> displayModel = new TreeViewDisplay<T>(model);
        treeViewPane.setTreeViewModel(displayModel);
        filterPanel.setFilterPanelListener(displayModel);
    }

    private class TreeViewDisplay<E> extends AbstractTreeViewModel<E>
            implements FilterPanelListener
    {

        private TreeViewModel<E> model;

        public TreeViewDisplay(TreeViewModel<E> model)
        {
            super(model.getData());
            this.model = model;
        }

        public List<? extends TreeView<E>> getTreeViews()
        {
            return model.getTreeViews();
        }

        public int getDefaultTreeViewIndex()
        {
            return model.getDefaultTreeViewIndex();
        }

        public DataView<E> getDataView()
        {
            return model.getDataView();
        }

        public int getQuickSearchTreeViewIndex()
        {
            return model.getQuickSearchTreeViewIndex();
        }

        public void applyFilter(Filter filter, boolean quicksearch)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
}
