/*
 * AbstractSelectionDialog.java
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
 * Created on Aug 30, 2008, 9:54:31 PM
 */
package pcgen.gui.tools;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.util.Collections;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pcgen.gui.tools.ResourceManager.Icons;
import pcgen.gui.util.DefaultGenericListModel;
import pcgen.gui.util.GenericListModelWrapper;
import pcgen.gui.util.event.PopupMouseAdapter;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public abstract class AbstractSelectionDialog extends JDialog
{

    protected final JList availableList;
    protected final JList selectedList;
    protected final JPopupMenu availableListPopup;
    protected final JPopupMenu selectedListPopup;
    protected final Action newAction;
    protected final Action deleteAction;
    protected final Action addAction;
    protected final Action removeAction;
    protected final Action upAction;
    protected final Action downAction;
    protected DefaultGenericListModel<?> availableModel;
    protected DefaultGenericListModel<?> selectedModel;
    private GenericSelectionModel model;

    public AbstractSelectionDialog()
    {
        this.availableList = new JList();
        this.selectedList = new JList();
        this.availableListPopup = new JPopupMenu();
        this.selectedListPopup = new JPopupMenu();
        this.newAction = new NewAction();
        this.deleteAction = new DeleteAction();
        this.addAction = new AddAction();
        this.removeAction = new RemoveAction();
        this.upAction = new UpAction();
        this.downAction = new DownAction();
        initComponents();
    }

    private void initComponents()
    {
        getContentPane().setLayout(new GridBagLayout());
        addWindowListener(
                new WindowAdapter()
                {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent evt)
                    {
                        doClose(false);
                    }

                });
        ActionHandler handler = new ActionHandler();
        FlippingSplitPane subSplitPane = new FlippingSplitPane(JSplitPane.VERTICAL_SPLIT);
        ListPanel panel;
        {//Initialize availableList
            panel = new ListPanel(availableList, availableListPopup, handler,
                                  getAvailableListTitle());
            panel.add(newAction);
            panel.add(deleteAction);
        }
        subSplitPane.setTopComponent(panel);
        {//Initialize selectedList
            panel = new ListPanel(selectedList, selectedListPopup, handler,
                                  getSelectedListTitle());
            panel.add(addAction);
            panel.add(removeAction);
            panel.addSeparator();
            panel.add(upAction);
            panel.add(downAction);
        }
        subSplitPane.setBottomComponent(panel);
        FlippingSplitPane splitPane = new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                                            getLeftComponent(),
                                                            subSplitPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(7);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(splitPane, gridBagConstraints);
        JButton button;
        {
            button = new JButton("OK");
            button.addActionListener(
                    new ActionListener()
                    {

                        public void actionPerformed(ActionEvent e)
                        {
                            doClose(true);
                        }

                    });
        }
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new Insets(4, 4, 4, 4);
        getContentPane().add(button, gridBagConstraints);
        {
            button = new JButton("Cancel");
            button.addActionListener(
                    new ActionListener()
                    {

                        public void actionPerformed(ActionEvent e)
                        {
                            doClose(false);
                        }

                    });
        }
        gridBagConstraints.weightx = 0.0;
        getContentPane().add(button, gridBagConstraints);

        pack();
    }

    @SuppressWarnings("unchecked")
    protected void setModel(GenericSelectionModel<?> model)
    {
        this.model = model;
        availableModel = new DefaultGenericListModel(new GenericListModelWrapper(model.getAvailableList()));
        selectedModel = new DefaultGenericListModel(new GenericListModelWrapper(model.getSelectedList()));

        availableList.setModel(availableModel);
        selectedList.setModel(selectedModel);
    }

    protected abstract Component getLeftComponent();

    protected abstract String getAvailableListTitle();

    protected abstract String getSelectedListTitle();

    protected abstract String getNewActionToolTip();

    protected abstract String getDeleteActionToolTip();

    protected abstract String getAddActionToolTip();

    protected abstract String getRemoveActionToolTip();

    protected abstract Object createNewItem();

    @SuppressWarnings("unchecked")
    private void doClose(boolean save)
    {
        if (save)
        {
            model.setAvailableList(availableModel);
            model.setSelectedList(selectedModel);
        }
        setVisible(false);
        dispose();
    }

    private class NewAction extends AbstractAction
    {

        public NewAction()
        {
            putValue(NAME, ResourceManager.getText("new"));
            putValue(MNEMONIC_KEY, ResourceManager.getMnemonic("new"));
            putValue(SHORT_DESCRIPTION, getNewActionToolTip());
        }

        @SuppressWarnings("unchecked")
        public void actionPerformed(ActionEvent e)
        {
            DefaultGenericListModel model = availableModel;
            model.add(createNewItem());
        }

    }

    private class DeleteAction extends AbstractAction
    {

        public DeleteAction()
        {
            putValue(NAME, ResourceManager.getText("delete"));
            putValue(MNEMONIC_KEY, ResourceManager.getMnemonic("delete"));
            putValue(SHORT_DESCRIPTION, getDeleteActionToolTip());
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e)
        {
            Object value = availableList.getSelectedValue();
            availableModel.remove(value);
            selectedModel.remove(value);
        }

    }

    private class AddAction extends AbstractAction
    {

        public AddAction()
        {
            putValue(NAME, ResourceManager.getText("add"));
            putValue(MNEMONIC_KEY, ResourceManager.getMnemonic("add"));
            putValue(SHORT_DESCRIPTION, getAddActionToolTip());
            setEnabled(false);
        }

        @SuppressWarnings("unchecked")
        public void actionPerformed(ActionEvent e)
        {
            DefaultGenericListModel model = selectedModel;
            model.add(availableList.getSelectedValue());
        }

    }

    private class RemoveAction extends AbstractAction
    {

        public RemoveAction()
        {
            putValue(NAME, ResourceManager.getText("remove"));
            putValue(MNEMONIC_KEY, ResourceManager.getMnemonic("remove"));
            putValue(SHORT_DESCRIPTION, getRemoveActionToolTip());
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e)
        {
            int index = selectedList.getSelectedIndex();
            selectedModel.remove(index);
        }

    }

    private class UpAction extends AbstractAction
    {

        public UpAction()
        {
            putValue(SMALL_ICON, ResourceManager.getImageIcon(Icons.Up16));
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e)
        {
            int index = selectedList.getSelectedIndex();
            Collections.swap(selectedModel, index, index - 1);
            selectedList.setSelectedIndex(index - 1);
        }

    }

    private class DownAction extends AbstractAction
    {

        public DownAction()
        {
            putValue(SMALL_ICON, ResourceManager.getImageIcon(Icons.Down16));
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e)
        {
            int index = selectedList.getSelectedIndex();
            Collections.swap(selectedModel, index, index + 1);
            selectedList.setSelectedIndex(index + 1);
        }

    }

    private class ActionHandler extends PopupMouseAdapter implements ListSelectionListener
    {

        @Override
        public void showPopup(MouseEvent e)
        {
            if (e.getComponent() == availableList)
            {
                availableListPopup.show(availableList, e.getX(), e.getY());
            }
            else
            {
                selectedListPopup.show(selectedList, e.getX(), e.getY());
            }
        }

        public void valueChanged(ListSelectionEvent e)
        {
            JList list = (JList) e.getSource();
            Object value = list.getSelectedValue();
            boolean nonNull = value != null;
            if (list.getParent() == selectedList)
            {
                int index = e.getFirstIndex();
                upAction.setEnabled(nonNull && index > 0);
                downAction.setEnabled(nonNull &&
                                      index < selectedModel.getSize() - 1);
                if (nonNull)
                {
                    availableList.setSelectedValue(value, true);
                    //this checks if the selection is unique to the selectionModel
                    if (!availableModel.contains(value))
                    {
                        addAction.setEnabled(false);
                        removeAction.setEnabled(true);
                    }
                }
            }
            else if (nonNull)
            {
                selectedList.setSelectedValue(value, true);

                boolean unique = !selectedModel.contains(value);
                addAction.setEnabled(unique);
                removeAction.setEnabled(!unique);
            }
        }

    }

    private static class ListPanel extends JPanel
    {

        private final GridBagConstraints gridBagConstraints;
        private final JPopupMenu popupMenu;

        public ListPanel(JList list, JPopupMenu popupMenu,
                          ActionHandler handler, String title)
        {
            super(new GridBagLayout());
            this.gridBagConstraints = new GridBagConstraints();
            this.popupMenu = popupMenu;

            gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            add(new JLabel(title), gridBagConstraints);

            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.addListSelectionListener(handler);
            list.addMouseListener(handler);

            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridheight = GridBagConstraints.REMAINDER;
            gridBagConstraints2.fill = GridBagConstraints.BOTH;
            gridBagConstraints2.weightx = 1.0;
            gridBagConstraints2.weighty = 1.0;
            add(new JScrollPane(list), gridBagConstraints2);

            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = GridBagConstraints.NORTH;
        }

        public void add(Action action)
        {
            add(new JButton(action), gridBagConstraints);
            popupMenu.add(new JMenuItem(action));
        }

        public void addSeparator()
        {
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridwidth = GridBagConstraints.REMAINDER;
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.insets = new Insets(4, 0, 4, 0);
            add(new JSeparator(), gridBagConstraints2);
            popupMenu.addSeparator();
        }

    }
}
