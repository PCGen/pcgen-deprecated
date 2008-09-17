/*
 * SelectionDialog.java
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.util.Collections;
import java.util.Properties;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
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
public class SelectionDialog<E> extends JDialog
{

    private final JLabel availableLabel;
    private final JLabel selectionLabel;
    private final JList availableList;
    private final JList selectedList;
    private final JPopupMenu availableListPopup;
    private final JPopupMenu selectedListPopup;
    private final JPanel itemPanel;
    private final Action newAction;
    private final Action copyAction;
    private final Action deleteAction;
    private final Action addAction;
    private final Action removeAction;
    private final Action upAction;
    private final Action downAction;
    private DefaultGenericListModel<E> availableModel;
    private DefaultGenericListModel<E> selectedModel;
    private SelectionModel<E> model;

    protected SelectionDialog()
    {
        this.availableLabel = new JLabel();
        this.selectionLabel = new JLabel();
        this.availableList = new JList();
        this.selectedList = new JList();
        this.availableListPopup = new JPopupMenu();
        this.selectedListPopup = new JPopupMenu();
        this.itemPanel = new JPanel(new BorderLayout());
        this.newAction = new NewAction();
        this.copyAction = new CopyAction();
        this.deleteAction = new DeleteAction();
        this.addAction = new AddAction();
        this.removeAction = new RemoveAction();
        this.upAction = new UpAction();
        this.downAction = new DownAction();
        initComponents();
    }

    public void setAddActionToolTip(String toolTip)
    {
        addAction.putValue(toolTip, WIDTH);
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
            panel = new ListPanel(availableList, handler, availableLabel);
            panel.add(newAction);
            panel.add(copyAction);
            panel.add(deleteAction);
            availableList.setCellRenderer(new ListItemRenderer());
        }
        {//Initialize availableListPopup
            availableListPopup.add(new JMenuItem(addAction));
            availableListPopup.add(new JMenuItem(copyAction));
            availableListPopup.add(new JMenuItem(newAction));
            availableListPopup.add(new JMenuItem(deleteAction));
        }
        subSplitPane.setTopComponent(panel);
        {//Initialize selectedList
            panel = new ListPanel(selectedList, handler, selectionLabel);
            panel.add(addAction);
            panel.add(removeAction);
            panel.addSeparator();
            panel.add(upAction);
            panel.add(downAction);
            selectedList.setCellRenderer(new ListItemRenderer());
        }
        {//Initialize selectedListPopup
            selectedListPopup.add(new JMenuItem(removeAction));
            selectedListPopup.add(new JMenuItem(deleteAction));
            selectedListPopup.addSeparator();
            selectedListPopup.add(new JMenuItem(upAction));
            selectedListPopup.add(new JMenuItem(downAction));
        }
        subSplitPane.setBottomComponent(panel);
        FlippingSplitPane splitPane = new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                                            itemPanel,
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
            button = new JButton(ResourceManager.getText("ok"));
            button.setActionCommand("ok");
            button.addActionListener(handler);
        }
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new Insets(4, 4, 4, 4);
        getContentPane().add(button, gridBagConstraints);
        {
            button = new JButton(ResourceManager.getText("cancel"));
            button.setActionCommand("cancel");
            button.addActionListener(handler);
        }
        gridBagConstraints.weightx = 0.0;
        getContentPane().add(button, gridBagConstraints);

        pack();
    }

    protected void setModel(SelectionModel<E> model)
    {
        this.model = model;
        availableModel = new DefaultGenericListModel<E>(new GenericListModelWrapper<E>(model.getAvailableList()));
        selectedModel = new DefaultGenericListModel<E>(new GenericListModelWrapper<E>(model.getSelectedList()));

        Properties props = model.getDisplayProperties();
        availableLabel.setText(ResourceManager.getText(props.getProperty(model.AVAILABLE_TEXT_PROP)));
        selectionLabel.setText(ResourceManager.getText(props.getProperty(model.SELECTION_TEXT_PROP)));
        newAction.putValue(Action.SHORT_DESCRIPTION,
                           ResourceManager.getToolTip(props.getProperty(model.NEW_TOOLTIP_PROP)));
        copyAction.putValue(Action.SHORT_DESCRIPTION,
                            ResourceManager.getToolTip(props.getProperty(model.COPY_TOOLTIP_PROP)));
        deleteAction.putValue(Action.SHORT_DESCRIPTION,
                              ResourceManager.getToolTip(props.getProperty(model.DELETE_TOOLTIP_PROP)));
        addAction.putValue(Action.SHORT_DESCRIPTION,
                           ResourceManager.getToolTip(props.getProperty(model.ADD_TOOLTIP_PROP)));
        removeAction.putValue(Action.SHORT_DESCRIPTION,
                              ResourceManager.getToolTip(props.getProperty(model.REMOVE_TOOLTIP_PROP)));

        availableList.setModel(availableModel);
        selectedList.setModel(selectedModel);
        newAction.setEnabled(true);
    }

    public DefaultGenericListModel<E> getAvailableModel()
    {
        return availableModel;
    }

    public DefaultGenericListModel<E> getSelectedModel()
    {
        return selectedModel;
    }

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
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e)
        {
            E item = model.createMutableItem(SelectionDialog.this, null);
            if (item != null)
            {
                availableModel.add(item);
                availableList.setSelectedValue(item, true);
            }
        }

    }

    private class CopyAction extends AbstractAction
    {

        public CopyAction()
        {
            putValue(NAME, ResourceManager.getText("copy"));
            putValue(MNEMONIC_KEY, ResourceManager.getMnemonic("copy"));
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e)
        {
            E item = model.createMutableItem(SelectionDialog.this,
                                             availableModel.get(availableList.getSelectedIndex()));
            if (item != null)
            {
                availableModel.add(item);
                availableList.setSelectedValue(item, true);
            }
        }

    }

    private class DeleteAction extends AbstractAction
    {

        public DeleteAction()
        {
            putValue(NAME, ResourceManager.getText("delete"));
            putValue(MNEMONIC_KEY, ResourceManager.getMnemonic("delete"));
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
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e)
        {
            E item = availableModel.get(availableList.getSelectedIndex());
            selectedModel.add(item);
            selectedList.setSelectedIndex(selectedModel.getSize() - 1);
            setEnabled(false);
            removeAction.setEnabled(true);
        }

    }

    private class RemoveAction extends AbstractAction
    {

        public RemoveAction()
        {
            putValue(NAME, ResourceManager.getText("remove"));
            putValue(MNEMONIC_KEY, ResourceManager.getMnemonic("remove"));
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

    /**
     * This class handles all user based actions, such as menu popup, 
     * selection handling, and control button event handling(OK/Cancel).
     */
    private class ActionHandler extends PopupMouseAdapter
            implements ActionListener, ListSelectionListener
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
            @SuppressWarnings("unchecked")
            E value = (E) list.getSelectedValue();
            boolean nonNull = value != null;
            if (list == availableList)
            {
                copyAction.setEnabled(nonNull);
                deleteAction.setEnabled(nonNull && model.isMutable(value));
                if (nonNull)
                {
                    boolean unique = !selectedModel.contains(value);
                    removeAction.setEnabled(!unique);
                    addAction.setEnabled(unique && model.isAddable(value));
                    if (unique)
                    {
                        selectedList.clearSelection();
                    }
                    else
                    {
                        selectedList.setSelectedValue(value, true);
                    }
                }
            }
            else
            {
                int index = selectedList.getSelectedIndex();
                upAction.setEnabled(nonNull && index > 0);
                downAction.setEnabled(nonNull &&
                                      index < selectedModel.getSize() - 1);
                if (nonNull)
                {
                    if (!availableModel.contains(value))
                    {
                        addAction.setEnabled(false);
                        removeAction.setEnabled(true);

                        availableList.clearSelection();
                    }
                    else
                    {
                        availableList.setSelectedValue(value, true);
                    }
                }
            }
            if (nonNull)
            {
                Component currentComp = null;
                if (itemPanel.getComponentCount() != 0)
                {
                    currentComp = itemPanel.getComponent(0);
                }
                Component comp = model.getItemPanel(SelectionDialog.this,
                                                    currentComp, value);
                if (currentComp != comp)
                {
                    itemPanel.removeAll();
                    itemPanel.add(comp, BorderLayout.CENTER);
                    itemPanel.validate();
                }
            }
        }

        public void actionPerformed(ActionEvent e)
        {
            doClose(e.getActionCommand().equals("ok"));
        }

    }

    private class ListItemRenderer extends DefaultListCellRenderer
    {

        @Override
        @SuppressWarnings("unchecked")
        public Component getListCellRendererComponent(JList list, Object value,
                                                       int index,
                                                       boolean isSelected,
                                                       boolean cellHasFocus)
        {
            Component comp = super.getListCellRendererComponent(list, value,
                                                                index,
                                                                isSelected,
                                                                cellHasFocus);
            if (!isSelected)
            {
                comp.setForeground(model.getItemColor((E) value));
            }
            return comp;
        }

    }

    private static class ListPanel extends JPanel
    {

        private final GridBagConstraints gridBagConstraints;

        public <T extends ListSelectionListener & MouseListener> ListPanel(JList list,
                                                                            T handler,
                                                                            JLabel label)
        {
            super(new GridBagLayout());
            this.gridBagConstraints = new GridBagConstraints();

            gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            add(label, gridBagConstraints);

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
        }

        public void addSeparator()
        {
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridwidth = GridBagConstraints.REMAINDER;
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.insets = new Insets(4, 0, 4, 0);
            add(new JSeparator(), gridBagConstraints2);
        }

    }
}
