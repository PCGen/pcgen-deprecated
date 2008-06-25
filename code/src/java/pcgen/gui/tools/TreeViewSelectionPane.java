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
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import pcgen.gui.core.UIContext;
import pcgen.gui.util.JTreeTable;
import pcgen.gui.util.JTreeViewPane;
import pcgen.gui.util.MultiLineTextIcon;
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
    private TreeViewModel<?> masterTreeView;
    private TreeViewModelListener<?> masterTreeViewListener;

    public TreeViewSelectionPane(UIContext context)
    {
        super(JSplitPane.VERTICAL_SPLIT, true);
        availableView = new FilteredTreeViewDisplay(context);
        selectedView = new FilteredTreeViewDisplay(context);

        availableView.getTreeViewPane().setDragEnabled(true);
        selectedView.getTreeViewPane().setDragEnabled(true);

        setTopComponent(selectedView);
        setBottomComponent(availableView);
    }

    public <T> void setSelectionModel(Class<T> elementClass,
                                       TreeViewModel<T> masterModel,
                                       Collection<T> selectedData)
    {
        Collection<T> availableElements = masterModel.getData();
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

        availableView.setTreeViewModel(elementClass, availableModel);
        selectedView.setTreeViewModel(elementClass, selectedModel);

        JTreeViewPane availablePane = availableView.getTreeViewPane();
        JTreeViewPane selectedPane = selectedView.getTreeViewPane();

        ElementTransferHandler<T> handler = new ElementTransferHandler<T>(elementClass,
                                                                          availablePane,
                                                                          selectedPane);
        availablePane.setTransferHandler(handler);
        selectedPane.setTransferHandler(handler);
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

    private static class ElementTransferHandler<E> extends TransferHandler
    {

        private final DataFlavor dataFlavor;
        private Class<E> elementClass;
        private JTreeViewPane pane1;
        private JTreeViewPane pane2;

        public ElementTransferHandler(Class<E> elementClass,
                                       JTreeViewPane pane1,
                                       JTreeViewPane pane2)
        {
            this.elementClass = elementClass;
            String type = DataFlavor.javaJVMLocalObjectMimeType + ";class=" +
                    elementClass.getName();
            this.dataFlavor = new DataFlavor(type, null);
            this.pane1 = pane1;
            this.pane2 = pane2;
        }

        public List<Object> getSelectedData(JTreeTable table)
        {
            TreePath[] paths = table.getTree().getSelectionPaths();
            List<Object> data = new ArrayList<Object>();
            for (TreePath path : paths)
            {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                data.add(node.getUserObject());
            }
            return data;
        }

        @Override
        public int getSourceActions(JComponent c)
        {
            return MOVE;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected Transferable createTransferable(JComponent c)
        {
            List<Object> data = getSelectedData((JTreeTable) c);
            Iterator<Object> it = data.iterator();
            while (it.hasNext())
            {
                if (!elementClass.isInstance(it.next()))
                {
                    it.remove();
                }
            }
            return new ElementSelection(c, (List<E>) data);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void exportDone(JComponent source, Transferable data,
                                   int action)
        {
            JTreeViewPane pane;
            if (pane1.isAncestorOf(source))
            {
                pane = pane1;
            }
            else
            {
                pane = pane2;
            }
            ElementSelection selection = (ElementSelection) data;
            ElementTreeViewModel<E> model = (ElementTreeViewModel<E>) pane.getTreeViewModel();
            Collection<E> elements = model.getData();
            elements.removeAll(selection.getData());
            model.setData(elements);
        }

        @Override
        public boolean canImport(JComponent comp, DataFlavor[] transferFlavors)
        {
            return dataFlavor == transferFlavors[0];
        }

        @Override
        public boolean importData(JComponent comp, Transferable t)
        {
            ElementSelection selection = (ElementSelection) t;
            if (comp == selection.getSource())
            {
                return false;
            }

            JTreeViewPane pane;
            if (pane1.isAncestorOf(comp))
            {
                pane = pane1;
            }
            else
            {
                pane = pane2;
            }
            @SuppressWarnings("unchecked")
            ElementTreeViewModel<E> model = (ElementTreeViewModel<E>) pane.getTreeViewModel();
            Collection<E> elements = model.getData();
            elements.addAll(selection.getData());
            model.setData(elements);

            return true;
        }

        @Override
        public Icon getVisualRepresentation(Transferable t)
        {
            ElementSelection selection = (ElementSelection) t;
            return new MultiLineTextIcon(selection.getSource(),
                                         selection.getData());
        }

        private class ElementSelection implements Transferable
        {

            private final List<E> data;
            private JComponent source;

            public ElementSelection(JComponent source, List<E> data)
            {
                this.source = source;
                this.data = data;
            }

            public DataFlavor[] getTransferDataFlavors()
            {
                return new DataFlavor[]{dataFlavor};
            }

            public boolean isDataFlavorSupported(DataFlavor flavor)
            {
                return dataFlavor == flavor;
            }

            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
            {
                if (!isDataFlavorSupported(flavor))
                {
                    throw new UnsupportedFlavorException(flavor);
                }
                return data;
            }

            public JComponent getSource()
            {
                return source;
            }

            public List<E> getData()
            {
                return data;
            }

        }
    }
}
