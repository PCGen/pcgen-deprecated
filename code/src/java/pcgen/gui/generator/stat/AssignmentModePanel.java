/*
 * AssignmentModePanel.java
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
 * Created on Sep 9, 2008, 1:51:39 PM
 */
package pcgen.gui.generator.stat;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pcgen.gui.tools.ResourceManager;
import pcgen.gui.tools.ResourceManager.Icons;
import pcgen.gui.util.table.ListTableModel;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
class AssignmentModePanel extends StatModePanel<AssignmentModeGenerator>
{

    private final ScoreTableModel model;
    private final JTable table;
    private final JCheckBox assignableBox;
    private final JButton addButton;
    private final JButton removeButton;
    private final JButton upButton;
    private final JButton downButton;
    private MutableAssignmentModeGenerator generator;

    public AssignmentModePanel()
    {
        this.assignableBox = new JCheckBox();
        this.addButton = new JButton();
        this.removeButton = new JButton();
        this.upButton = new JButton();
        this.downButton = new JButton();
        this.model = new ScoreTableModel();
        this.table = new JTable(model)
        {

            @Override
            public boolean getScrollableTracksViewportHeight()
            {
                // fetch the table's parent
                Container viewport = getParent();

                // if the parent is not a viewport, calling this isn't useful
                if (!(viewport instanceof JViewport))
                {
                    return false;
                }

                // return true if the table's preferred height is smaller
                // than the viewport height, else false
                return getPreferredSize().height < viewport.getHeight();
            }

        };
        initComponents();
    }

    private void initComponents()
    {
        GridBagConstraints gridBagConstraints;
        GridBagConstraints gridBagConstraints2;
        {
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = GridBagConstraints.NORTH;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        }
        table.setShowGrid(false);
        ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionModel.addListSelectionListener(model);

        gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridheight = GridBagConstraints.REMAINDER;
        gridBagConstraints2.fill = GridBagConstraints.BOTH;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.weighty = 1.0;
        add(new JScrollPane(table), gridBagConstraints2);

        initButton(assignableBox, "assignable", null, model,
                   gridBagConstraints);
        initButton(addButton, "add", null, model, gridBagConstraints);
        initButton(removeButton, "remove", null, model, gridBagConstraints);

        gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.insets = new Insets(4, 0, 4, 0);
        gridBagConstraints2.ipady = 1;
        add(new JSeparator(), gridBagConstraints2);

        initButton(upButton, "up", Icons.Up16, model, gridBagConstraints);
        initButton(downButton, "down", Icons.Down16, model, gridBagConstraints);
    }

    private void initButton(AbstractButton button, String prop, Icons icon,
                             ActionListener listener,
                             GridBagConstraints gridBagConstraints)
    {
        if (icon == null)
        {
            button.setText(ResourceManager.getText(prop));
        }
        else
        {
            button.setIcon(ResourceManager.getImageIcon(icon));
        }
        button.setActionCommand(prop);
        button.setEnabled(false);
        button.addActionListener(listener);
        add(button, gridBagConstraints);
    }

    public void setGenerator(AssignmentModeGenerator generator)
    {
        boolean assignable = generator.isAssignable();
        model.setGenerator(generator);

        boolean editable = generator instanceof MutableAssignmentModeGenerator;
        assignableBox.setSelected(assignable);
        assignableBox.setEnabled(editable);
        upButton.setEnabled(false);
        downButton.setEnabled(false);
        addButton.setEnabled(editable && assignable);
        removeButton.setEnabled(editable && assignable);

        this.generator = editable ? (MutableAssignmentModeGenerator) generator : null;
    }

    public void saveGeneratorData()
    {
        if (generator != null)
        {
            generator.setAssignable(assignableBox.isSelected());
            List<Integer> scores = new ArrayList<Integer>(model);
            generator.setScores(scores);
        }
    }

    private class ScoreTableModel extends ListTableModel<Integer> implements ActionListener,
                                                                              ListSelectionListener
    {

        public ScoreTableModel()
        {
            super(ResourceManager.getText("score"));
        }

        @SuppressWarnings("unchecked")
        public void setGenerator(AssignmentModeGenerator generator)
        {
            removeAllElements();
            addAll(generator.getAll());
            setAssignable(generator.isAssignable());
        }

        @SuppressWarnings("unchecked")
        private void setAssignable(boolean assignable)
        {
            if (assignable)
            {
                if (getSize() < 6)
                {
                    addAll(Collections.nCopies(6 - getSize(), 0));
                }
                else
                {
                    setSize(6);
                }
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex)
        {
            return Integer.class;
        }

        @Override
        public boolean isCellEditable(int row, int column)
        {
            return generator != null;
        }

        @SuppressWarnings("unchecked")
        public void actionPerformed(ActionEvent e)
        {
            String command = e.getActionCommand();
            if (command.equals("assignable"))
            {
                boolean selected = assignableBox.isSelected();
                addButton.setEnabled(selected);
                removeButton.setEnabled(selected);
                setAssignable(selected);
            }
            else if (command.equals("add"))
            {
                add(0);
            }
            else
            {
                int row = table.getSelectedRow();
                if (command.equals("remove"))
                {
                    remove(row);
                }
                else
                {
                    int newrow = row + (command.equals("up") ? -1 : 1);
                    Collections.swap(this, row, newrow);
                    table.setRowSelectionInterval(newrow, newrow);
                }
            }
        }

        public void valueChanged(ListSelectionEvent e)
        {
            int index = table.getSelectedRow();
            upButton.setEnabled(generator != null && index > 0);
            downButton.setEnabled(generator != null && index != -1 && index <
                                  getRowCount() - 1);
        }

    }
}
