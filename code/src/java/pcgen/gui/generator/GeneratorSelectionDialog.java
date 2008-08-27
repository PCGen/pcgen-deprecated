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
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.util.Collections;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pcgen.gui.tools.FilteredTreeViewSelectionPanel;
import pcgen.gui.tools.FlippingSplitPane;
import pcgen.gui.tools.PCGenAction;
import pcgen.gui.tools.ResourceManager;
import pcgen.gui.tools.ResourceManager.Icons;
import pcgen.gui.util.DefaultGenericListModel;
import pcgen.gui.util.GenericListModelWrapper;
import pcgen.gui.util.JTreeViewSelectionPane.SelectionType;

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
    private GeneratorSelectionModel model;

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
        addWindowListener(
                new WindowAdapter()
                {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent evt)
                    {
                        doClose(false);
                    }

                });
        GridBagConstraints gridBagConstraints;

        SelectionManager listener = new SelectionManager();
        selectionPanel.addItemListener(listener);

        FlippingSplitPane subSplitPane = new FlippingSplitPane(JSplitPane.VERTICAL_SPLIT);
        JPanel panel;
        {//Initialize Available Panel
            panel = new JPanel(new GridBagLayout());
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            add(new JLabel("Available Generators"), gridBagConstraints);

            availableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            availableList.addListSelectionListener(listener);
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
        subSplitPane.setTopComponent(panel);
        {//Initialize Selected Panel
            panel = new JPanel(new GridBagLayout());
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            add(new JLabel("Selected Generators"), gridBagConstraints);

            //Init selectedList
            selectedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            selectedList.addListSelectionListener(listener);
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
        subSplitPane.setBottomComponent(panel);

        FlippingSplitPane splitPane = new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                                            selectionPanel,
                                                            subSplitPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(7);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
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
    public void setModel(GeneratorSelectionModel model)
    {
        this.model = model;
        availableModel = new DefaultGenericListModel(new GenericListModelWrapper(model.getAvailableGenerators()));
        selectedModel = new DefaultGenericListModel(new GenericListModelWrapper(model.getSelectedGenerators()));
        selectionPanel.restoreState(selectionPanel.createState(model.getCharacter(),
                                                               model.getTreeViewModel()));
        availableList.setModel(availableModel);
        selectedList.setModel(selectedModel);
    }

    @SuppressWarnings("unchecked")
    private void doClose(boolean save)
    {
        if (save)
        {
            model.setAvailableGenerators(availableModel);
            model.setSelectedGenerators(selectedModel);
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
        public void actionPerformed(ActionEvent e)
        {
            availableModel.add(new DefaultMutableGenerator());
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
            Generator generator;
            if (!availableList.isSelectionEmpty())
            {
                generator = (Generator) availableList.getSelectedValue();

            }
            else
            {
                generator = new SingletonGenerator(selectionPanel.getSelectedData().get(0));
            }
            selectedModel.add(generator);
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
            selectedList.remove(index);
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

    private class SelectionManager implements ListSelectionListener,
                                               ItemListener
    {

        public void valueChanged(ListSelectionEvent e)
        {
            Object source = e.getSource();
            if (source instanceof JList)
            {
                JList list = (JList) source;
                Generator<?> value = (Generator<?>) list.getSelectedValue();
                boolean valid = value != null;
                if (list == availableList)
                {
                    deleteAction.setEnabled(valid &&
                                            value instanceof MutableGenerator);

                    if (valid)
                    {
                        selectedList.setSelectedValue(value, true);

                        boolean unique = !selectedModel.contains(value);
                        addAction.setEnabled(unique);
                        removeAction.setEnabled(!unique);

                        selectionPanel.setEditable(value instanceof MutableGenerator);
                        selectionPanel.setSelectionType(SelectionType.CHECKBOX);
                        selectionPanel.setSelectedObjects(value.getAll());
                    }
                }
                else
                {
                    int index = e.getFirstIndex();
                    upAction.setEnabled(valid && index > 0);
                    downAction.setEnabled(valid && index <
                                          selectedModel.getSize() - 1);
                    if (valid)
                    {
                        availableList.setSelectedValue(value, true);
                        //this checks if the selection is unique to the selectionModel
                        if (!availableModel.contains(value))
                        {
                            addAction.setEnabled(false);
                            removeAction.setEnabled(true);

                            selectionPanel.setEditable(false);
                            selectionPanel.setSelectionType(SelectionType.RADIO);
                            selectionPanel.setSelectedObjects(value.getAll());
                        }
                    }
                }
            }
            else
            {
            //availableList.clearSelection();
            //selectedList.clearSelection();
            }
        }

        public void itemStateChanged(ItemEvent e)
        {
            updateMutableGenerator(e);
        }

        @SuppressWarnings("unchecked")
        public <T> void updateMutableGenerator(ItemEvent e)
        {
            T item = (T) e.getItem();
            MutableGenerator<T> generator = (MutableGenerator<T>) availableList.getSelectedValue();
            if (e.getStateChange() == ItemEvent.SELECTED)
            {
                generator.add(item);
            }
            else
            {
                generator.remove(item);
            }
        }

    }
}
