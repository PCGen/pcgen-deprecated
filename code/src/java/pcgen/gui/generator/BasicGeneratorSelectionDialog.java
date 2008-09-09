/*
 * BasicGeneratorSelectionDialog.java
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pcgen.gui.tools.AbstractSelectionDialog;
import pcgen.gui.tools.FilteredTreeViewSelectionPanel;
import pcgen.gui.tools.ResourceManager;
import pcgen.gui.util.DefaultGenericListModel;
import pcgen.gui.util.GenericListModelWrapper;
import pcgen.gui.util.JTreeViewSelectionPane.SelectionType;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class BasicGeneratorSelectionDialog extends AbstractSelectionDialog
{

    static
    {
        ResourceManager.ensureLoaded(ResourceManager.GENERATOR_BUNDLE);
    }

    private Action addAsAction;
    private FilteredTreeViewSelectionPanel selectionPanel;
    private BasicGeneratorSelectionModel model;

    public BasicGeneratorSelectionDialog()
    {
        initComponents();
    }

    private void initComponents()
    {
        SelectionHandler handler = new SelectionHandler();
        availableList.addListSelectionListener(handler);
        selectedList.addListSelectionListener(handler);
        availableList.setCellRenderer(new GeneratorListCellRenderer());
        selectedList.setCellRenderer(new GeneratorListCellRenderer());
        selectionPanel.addItemListener(handler);
    }

    @Override
    protected Component getLeftComponent()
    {
        if (selectionPanel == null)
        {
            selectionPanel = new FilteredTreeViewSelectionPanel();
            addAsAction = new AddAsAction();
            selectionPanel.add(new JButton(addAsAction), BorderLayout.SOUTH);
        }
        return selectionPanel;
    }

    @Override
    protected String getAvailableListTitle()
    {
        return ResourceManager.getText("availGen");
    }

    @Override
    protected String getSelectedListTitle()
    {
        return ResourceManager.getText("selGen");
    }

    @Override
    protected String getNewActionToolTip()
    {
        return ResourceManager.getToolTip("newGen");
    }

    @Override
    protected String getDeleteActionToolTip()
    {
        return ResourceManager.getToolTip("deleteGen");
    }

    @Override
    protected String getAddActionToolTip()
    {
        return ResourceManager.getToolTip("addGen");
    }

    @Override
    protected String getRemoveActionToolTip()
    {
        return ResourceManager.getToolTip("removeGen");
    }

    @Override
    protected Object createNewItem()
    {
        return new DefaultMutableGenerator(JOptionPane.showInputDialog(this,
                                                                       ResourceManager.getText("creategen")));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doSave()
    {
        model.setAvailableGenerators(availableModel);
        model.setSelectedGenerators(selectedModel);
    }

    @SuppressWarnings("unchecked")
    public void setModel(BasicGeneratorSelectionModel model)
    {
        this.model = model;
        availableModel = new DefaultGenericListModel(new GenericListModelWrapper(model.getAvailableGenerators()));
        selectedModel = new DefaultGenericListModel(new GenericListModelWrapper(model.getSelectedGenerators()));
        selectionPanel.restoreState(selectionPanel.createState(model.getCharacter(),
                                                               model.getTreeViewModel()));
        availableList.setModel(availableModel);
        selectedList.setModel(selectedModel);
    }

    private class AddAsAction extends AbstractAction
    {

        public AddAsAction()
        {
            putValue(NAME, ResourceManager.getText("addGenAs"));
            setEnabled(false);
        }

        @SuppressWarnings("unchecked")
        public void actionPerformed(ActionEvent e)
        {
            DefaultGenericListModel model = selectedModel;
            for (Object data : selectionPanel.getSelectedData())
            {
                model.add(data);
            }
        }

    }

    private class SelectionHandler implements ListSelectionListener,
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
                if (list.getParent() == availableList)
                {
                    deleteAction.setEnabled(valid &&
                                            value instanceof MutableGenerator);
                    if (valid)
                    {
                        selectionPanel.setEditable(value instanceof MutableGenerator);
                        selectionPanel.setSelectionType(SelectionType.CHECKBOX);
                        selectionPanel.setSelectedObjects(value.getAll());
                    }
                }
                else if (valid && !availableModel.contains(value))
                {
                    selectionPanel.setEditable(false);
                    selectionPanel.setSelectionType(SelectionType.RADIO);
                    selectionPanel.setSelectedObjects(value.getAll());
                }
            }
            else
            {
                List<Object> data = selectionPanel.getSelectedData();
                addAsAction.setEnabled(data != null && !data.isEmpty());
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

    private static class GeneratorListCellRenderer extends DefaultListCellRenderer
    {

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                                                       int index,
                                                       boolean isSelected,
                                                       boolean cellHasFocus)
        {
            Component comp = super.getListCellRendererComponent(list, value,
                                                                index,
                                                                isSelected,
                                                                cellHasFocus);
            if (!isSelected && value instanceof MutableGenerator)
            {
                comp.setForeground(Color.BLUE);
            }
            return comp;
        }

    }
}
