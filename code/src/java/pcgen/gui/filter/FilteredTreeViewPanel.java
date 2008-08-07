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
package pcgen.gui.filter;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.undo.StateEditable;
import pcgen.gui.facade.CharacterFacade;
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
public class FilteredTreeViewPanel extends JPanel implements StateEditable
{

    protected final FilterPanel filterPanel;
    protected final JTreeViewPane treeViewPane;

    public FilteredTreeViewPanel()
    {
        setLayout(new BorderLayout());

        filterPanel = new FilterPanel();
        add(filterPanel, BorderLayout.PAGE_START);

        treeViewPane = createDefaultTreeViewPane();
        add(treeViewPane, BorderLayout.CENTER);
    }

    protected JTreeViewPane createDefaultTreeViewPane()
    {
        return new JTreeViewPane();
    }

    protected JTreeViewPane getTreeViewPane()
    {
        return treeViewPane;
    }

    /**
     * delegates to JTable.getSelectionModel()
     * @return the row selection model
     */
    public ListSelectionModel getSelectionModel()
    {
        return getTreeViewPane().getSelectionModel();
    }

    /**
     * delegates to JTreeViewPane.getSelectedData()
     * @param b
     */
    public List<Object> getSelectedData()
    {
        return getTreeViewPane().getSelectedData();
    }

    /**
     * delegates to JTablePane.getTransferHandler()
     * @param b
     */
    @Override
    public TransferHandler getTransferHandler()
    {
        return getTreeViewPane().getTransferHandler();
    }

    /**
     * delegates to JTablePane.setTransferHandler()
     * @param b
     */
    @Override
    public void setTransferHandler(TransferHandler newHandler)
    {
        getTreeViewPane().setTransferHandler(newHandler);
    }

    /**
     * delegates to JTablePane.getDragEnabled()
     * @param b
     */
    public boolean getDragEnabled()
    {
        return getTreeViewPane().getDragEnabled();
    }

    /**
     * delegates to JTablePane.setDragEnabled()
     * @param b
     */
    public void setDragEnabled(boolean b)
    {
        getTreeViewPane().setDragEnabled(b);
    }

    public void setTreeCellRenderer(TreeCellRenderer renderer)
    {
        getTreeViewPane().setTreeCellRenderer(renderer);
    }

    public void setDefaultRenderer(Class<?> columnClass,
                                    TableCellRenderer renderer)
    {
        getTreeViewPane().setDefaultRenderer(columnClass, renderer);
    }

    public void setDefaultEditor(Class<?> columnClass, TableCellEditor editor)
    {
        getTreeViewPane().setDefaultEditor(columnClass, editor);
    }

    public <T> Hashtable<Object, Object> createState(CharacterFacade character,
                                                      FilterableTreeViewModel<T> model)
    {
        Hashtable<Object, Object> state = filterPanel.createState(model.getFilterClass());
        state.put("FilteredTreeViewModel",
                  new FilteredTreeViewModel<T>(character, model));
        return state;
    }

    public void storeState(Hashtable<Object, Object> state)
    {

    }

    public void restoreState(Hashtable<?, ?> state)
    {
        FilteredTreeViewModel<?> model = (FilteredTreeViewModel<?>) state.get("FilteredTreeViewModel");
        getTreeViewPane().setTreeViewModel(model);
        filterPanel.setFilterPanelListener(model);
        filterPanel.restoreState(state);
    }

    private class FilteredTreeViewModel<E> extends TreeViewModelWrapper<E>
            implements FilterPanelListener, GenericListDataListener<E>
    {

        private CharacterFacade character;

        public FilteredTreeViewModel(CharacterFacade character,
                                      TreeViewModel<E> model)
        {
            super(model);
            this.character = character;
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
                              getTreeViewPane().getQuickSearchMode()).start();
        }

        public void contentsChanged(GenericListDataEvent e)
        {
            applyFilter(filterPanel.getFilter(),
                        getTreeViewPane().getQuickSearchMode());
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
                    if (filter.accept(character, element))
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
                    getTreeViewPane().setQuickSearchMode(quicksearch);
                    modelData.addAll(value);
                }
                else
                {
                    getTreeViewPane().setQuickSearchMode(quicksearch);
                }
            }

        }
    }
}
