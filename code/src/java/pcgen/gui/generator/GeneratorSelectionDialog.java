/*
 * GeneratorSelectionDialog.java
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
 * Created on Aug 23, 2008, 7:20:12 PM
 */
package pcgen.gui.generator;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pcgen.gui.tools.FilteredTreeViewSelectionPanel;
import pcgen.gui.tools.FlippingSplitPane;
import pcgen.gui.tools.PCGenAction;
import pcgen.gui.tools.ResourceManager;
import pcgen.gui.tools.ResourceManager.Icons;
import pcgen.gui.util.DefaultGenericListModel;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class GeneratorSelectionDialog extends JDialog
{

    private final FilteredTreeViewSelectionPanel selectionPanel;
    private final JList availableList;
    private final JList selectedList;
    private final NewAction newAction;
    private final DeleteAction deleteAction;
    private final AddAction addAction;
    private final RemoveAction removeAction;
    private final UpAction upAction;
    private final DownAction downAction;
    private DefaultGenericListModel<Generator<?>> availableModel;
    private DefaultGenericListModel<Generator<?>> selectedModel;

    public GeneratorSelectionDialog()
    {
        this.selectionPanel = new FilteredTreeViewSelectionPanel();
        this.availableList = new JList();
        this.selectedList = new JList();
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
        GridBagConstraints gridBagConstraints;
        FlippingSplitPane subSplitPane = new FlippingSplitPane();
        JPanel panel;
        {//Initialize Available Panel
            panel = new JPanel(new GridBagLayout());
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            add(new JLabel("Available Generators"), gridBagConstraints);

            availableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridheight = GridBagConstraints.REMAINDER;
            gridBagConstraints2.fill = GridBagConstraints.BOTH;
            gridBagConstraints2.weightx = 1.0;
            gridBagConstraints2.weighty = 1.0;
            add(new JScrollPane(availableList), gridBagConstraints2);

            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            add(new JButton(newAction), gridBagConstraints);
            gridBagConstraints.anchor = GridBagConstraints.NORTH;
            add(new JButton(deleteAction), gridBagConstraints);
        }
        subSplitPane.setLeftComponent(panel);
        {//Initialize Selected Panel
            panel = new JPanel(new GridBagLayout());
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            add(new JLabel("Selected Generators"), gridBagConstraints);

            //Init selectedList
            selectedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridheight = GridBagConstraints.REMAINDER;
            gridBagConstraints2.fill = GridBagConstraints.BOTH;
            gridBagConstraints2.weightx = 1.0;
            gridBagConstraints2.weighty = 1.0;
            add(new JScrollPane(selectedList), gridBagConstraints2);

            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            add(new JButton(addAction), gridBagConstraints);
            add(new JButton(removeAction), gridBagConstraints);

            gridBagConstraints.insets = new Insets(4, 0, 4, 0);
            add(new JSeparator(), gridBagConstraints);
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);

            add(new JButton(upAction), gridBagConstraints);

            gridBagConstraints.anchor = GridBagConstraints.NORTH;
            add(new JButton(downAction), gridBagConstraints);
        }
        subSplitPane.setRightComponent(panel);
    }

    private class NewAction extends PCGenAction
    {

        public NewAction()
        {
            super(null);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {

        }

    }

    private class DeleteAction extends PCGenAction
    {

        public DeleteAction()
        {
            super(null);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {

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
        public void actionPerformed(ActionEvent e)
        {

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

        }

    }

    private class UpAction extends AbstractAction
    {

        public UpAction()
        {
            putValue(SMALL_ICON, ResourceManager.getImageIcon(Icons.Up16));
        }

        public void actionPerformed(ActionEvent e)
        {

        }

    }

    private class DownAction extends AbstractAction
    {

        public DownAction()
        {
            putValue(SMALL_ICON, ResourceManager.getImageIcon(Icons.Down16));
        }

        public void actionPerformed(ActionEvent e)
        {

        }

    }

    private class SelectionManager implements ListSelectionListener
    {

        public void valueChanged(ListSelectionEvent e)
        {
            Object source = e.getSource();
            if (source instanceof JList)
            {
                JList list = (JList) source;
                Object value = list.getSelectedValue();
                boolean valid = value != null;
                if (list == availableList)
                {
                    boolean enable = valid && !selectedModel.contains(value);
                    deleteAction.setEnabled(valid);
                    if (valid)
                    {
                        addAction.setEnabled(enable);
                        removeAction.setEnabled(!enable);
                        selectedList.setSelectedValue(value, true);
                    }
                    if (enable)
                    {
                    //Set RaceTreeViewModel
                    }
                }
                else
                {
                    upAction.setEnabled(valid);
                    downAction.setEnabled(valid);
                    if (valid)
                    {
                        availableList.setSelectedValue(value, true);
                    }
                    if (valid && !availableModel.contains(value))
                    {
                        addAction.setEnabled(false);
                        removeAction.setEnabled(true);
                    }
                }
            }
            else
            {
                availableList.clearSelection();
                selectedList.clearSelection();
            }
        }

    }
}
