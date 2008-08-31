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
import java.awt.event.WindowAdapter;
import java.util.Collections;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pcgen.gui.tools.ResourceManager.Icons;
import pcgen.gui.util.DefaultGenericListModel;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public abstract class AbstractSelectionDialog extends JDialog
{

    protected final GenericListPanel availableList;
    protected final GenericListPanel selectedList;
    protected final Action newAction;
    protected final Action deleteAction;
    protected final Action addAction;
    protected final Action removeAction;
    protected final Action upAction;
    protected final Action downAction;
    protected DefaultGenericListModel<?> availableModel;
    protected DefaultGenericListModel<?> selectedModel;

    public AbstractSelectionDialog()
    {
        availableList = new GenericListPanel();
        selectedList = new GenericListPanel();
        newAction = new NewAction();
        deleteAction = new DeleteAction();
        addAction = new AddAction();
        removeAction = new RemoveAction();
        upAction = new UpAction();
        downAction = new DownAction();
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
        ListSelectionListener listener = new SelectionHandler();
        FlippingSplitPane subSplitPane = new FlippingSplitPane(JSplitPane.VERTICAL_SPLIT);
        {//Initialize availableList
            availableList.setTitle(getAvailableListTitle());
            availableList.addListSelectionListener(listener);
            availableList.add(newAction);
            availableList.add(deleteAction);
        }
        subSplitPane.setTopComponent(availableList);
        {//Initialize selectedList
            selectedList.setTitle(getSelectedListTitle());
            selectedList.addListSelectionListener(listener);
            selectedList.add(addAction);
            selectedList.add(removeAction);
            selectedList.addSeparator();
            selectedList.add(upAction);
            selectedList.add(downAction);
        }
        subSplitPane.setBottomComponent(selectedList);
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

    protected abstract Component getLeftComponent();

    protected abstract String getAvailableListTitle();

    protected abstract String getSelectedListTitle();

    protected abstract Object createNewItem();

    protected abstract void doSave();

    private void doClose(boolean save)
    {
        if (save)
        {
            doSave();
        }
        setVisible(false);
        dispose();
    }

    private class NewAction extends PCGenAction
    {

        public NewAction()
        {
            super(null);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void actionPerformed(ActionEvent e)
        {
            DefaultGenericListModel model = availableModel;
            model.add(createNewItem());
        }

    }

    private class DeleteAction extends PCGenAction
    {

        public DeleteAction()
        {
            super(null);
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            Object value = availableList.getSelectedValue();
            availableModel.remove(value);
            selectedModel.remove(value);
        }

    }

    private class AddAction extends PCGenAction
    {

        public AddAction()
        {
            super(null);
            setEnabled(false);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void actionPerformed(ActionEvent e)
        {
            DefaultGenericListModel model = selectedModel;
            model.add(availableList.getSelectedValue());
        }

    }

    private class RemoveAction extends PCGenAction
    {

        public RemoveAction()
        {
            super(null);
            setEnabled(false);
        }

        @Override
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

    private class SelectionHandler implements ListSelectionListener
    {

        public void valueChanged(ListSelectionEvent e)
        {
            JList list = (JList) e.getSource();
            Object value = list.getSelectedValue();
            boolean valid = value != null;
            if (list.getParent() == selectedList)
            {
                int index = e.getFirstIndex();
                upAction.setEnabled(valid && index > 0);
                downAction.setEnabled(valid &&
                                      index < selectedModel.getSize() - 1);
                if (valid)
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
            else if (valid)
            {
                selectedList.setSelectedValue(value, true);

                boolean unique = !selectedModel.contains(value);
                addAction.setEnabled(unique);
                removeAction.setEnabled(!unique);
            }
        }

    }
}
