/*
 * TreeViewSelectionPane.java
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
 * Created on Jun 23, 2008, 11:52:27 AM
 */
package pcgen.gui.tools;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.TransferHandler;
import pcgen.gui.core.UIContext;
import pcgen.gui.util.event.TreeViewModelEvent;
import pcgen.gui.util.event.TreeViewModelListener;
import pcgen.gui.util.treeview.TreeViewModel;
import pcgen.gui.util.treeview.TreeViewModelWrapper;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class TreeViewSelectionPane extends JSplitPane
{

    private FilteredTreeViewDisplay availableView;
    private FilteredTreeViewDisplay selectedView;
    private ElementTreeViewModel<?> availableTreeView;
    private ElementTreeViewModel<?> selectedTreeView;
    private TreeViewModel<?> masterTreeView;
    private TreeViewModelListener<?> masterTreeViewListener;
    //private Collection<?> availableElements;
    public TreeViewSelectionPane(UIContext context)
    {
        super(JSplitPane.VERTICAL_SPLIT, true);
        availableView = new FilteredTreeViewDisplay(context);
        selectedView = new FilteredTreeViewDisplay(context);

        setTopComponent(selectedView);
        setBottomComponent(availableView);
    }

    public <T> void setSelectionModel(Class<T> elementClass,
                                       TreeViewModel<T> masterModel,
                                       Collection<T> selectedData)
    {
        setTreeViewModels(masterModel, selectedData);
        
    }

    private <T> void setTreeViewModels(TreeViewModel<T> masterModel,
                                        final Collection<T> selectedData)
    {
        List<T> availableElements = new ArrayList<T>(masterModel.getData());
        availableElements.removeAll(selectedData);

        final ElementTreeViewModel<T> availableModel = new ElementTreeViewModel<T>(masterModel,
                                                                                    availableElements);
        final ElementTreeViewModel<T> selectedModel = new ElementTreeViewModel<T>(masterModel,
                                                                                   selectedData);
        if (masterTreeView != null)
        {
            masterTreeView.removeTreeViewModelListener(masterTreeViewListener);
        }
        masterTreeView = masterModel;
        
        TreeViewModelListener<T> listener = new TreeViewModelListener<T>()
        {

            public void dataChanged(TreeViewModelEvent<T> event)
            {
                setModelData(availableModel, selectedModel,
                             event.getNewData(), selectedModel.getData());
            }

        };
        masterTreeViewListener = listener;
        masterModel.addTreeViewModelListener(listener);
        
        availableTreeView = availableModel;
        selectedTreeView = selectedModel;
    }

    private <T> void setModelData(ElementTreeViewModel<T> availableModel,
                                   ElementTreeViewModel<T> selectedModel,
                                   Collection<T> allData,
                                   Collection<T> selectedData)
    {
        Collection<T> availableElements = new ArrayList<T>(allData);
        Collection<T> selectedElements = new ArrayList<T>(selectedData);
        selectedElements.retainAll(availableElements);
        availableElements.removeAll(selectedElements);

        availableModel.setData(availableElements);
        selectedModel.setData(availableElements);
    }

    private class ElementTreeViewModel<E> extends TreeViewModelWrapper<E>
    {

        public ElementTreeViewModel(TreeViewModel<E> model, Collection<E> data)
        {
            super(model);
            setData(data);
        }

        @Override
        public void setModel(TreeViewModel<E> model)
        {
            this.model = model;
        }

        @Override
        public void setData(Collection<E> data)
        {
            super.setData(data);
        }

    }

    private class ElementTransferHandler extends TransferHandler
    {

        private Class<?> elementClass;

        public ElementTransferHandler(Class<?> elementClass)
        {
            this.elementClass = elementClass;
        }

        @Override
        public int getSourceActions(JComponent c)
        {
            return MOVE;
        }

        @Override
        protected Transferable createTransferable(JComponent c)
        {
            FilteredTreeViewDisplay view = (FilteredTreeViewDisplay) c;
            List<?> data = view.getTreeViewPane().getSelectedData();
            Iterator<?> it = data.iterator();
            while (it.hasNext())
            {
                if (!elementClass.isInstance(it.next()))
                {
                    it.remove();
                }
            }
            return new ElementSelection(elementClass, data);
        }

        @Override
        protected void exportDone(JComponent source, Transferable data,
                                   int action)
        {

        }

    }

    private static class ElementSelection implements Transferable
    {

        private final DataFlavor dataFlavor;
        private final List<?> data;

        public ElementSelection(Class<?> elementClass, List<?> data)
        {
            String type = DataFlavor.javaJVMLocalObjectMimeType + ";class=" +
                    elementClass.getName();
            this.dataFlavor = new DataFlavor(type, null);
            this.data = data;
        }

        public DataFlavor[] getTransferDataFlavors()
        {
            return new DataFlavor[]{dataFlavor};
        }

        public boolean isDataFlavorSupported(DataFlavor flavor)
        {
            return dataFlavor.match(flavor);
        }

        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
        {
            if (!isDataFlavorSupported(flavor))
            {
                throw new UnsupportedFlavorException(flavor);
            }
            return data;
        }

        public List<?> getData()
        {
            return data;
        }

    }
}
