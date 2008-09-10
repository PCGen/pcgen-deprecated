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
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import pcgen.gui.tools.ResourceManager;
import pcgen.gui.tools.ResourceManager.Icons;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class AssignmentModePanel extends JPanel
{

    private final DefaultTableModel model;
    private final JTable table;
    private final JCheckBox assignableBox;
    private final JButton addButton;
    private final JButton removeButton;
    private final JButton upButton;
    private final JButton downButton;

    public AssignmentModePanel()
    {
        super(new GridBagLayout());
        this.assignableBox = new JCheckBox();
        this.addButton = new JButton();
        this.removeButton = new JButton();
        this.upButton = new JButton();
        this.downButton = new JButton();
        this.model = new DefaultTableModel();
        this.table = new JTable()
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
        ActionHandler handler = new ActionHandler();
        table.setShowGrid(false);
        model.setColumnIdentifiers(new Object[]{ResourceManager.getText("score")});
        table.setModel(model);

        ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionModel.addListSelectionListener(handler);

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(new JScrollPane(table), gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        {//
            assignableBox.setText(ResourceManager.getText("assignable"));
            assignableBox.setActionCommand("assignable");
            assignableBox.addActionListener(handler);
        }
        add(assignableBox, gridBagConstraints);
        {
            addButton.setText(ResourceManager.getText("add"));
            addButton.setActionCommand("add");
            addButton.addActionListener(handler);
        }
        add(addButton, gridBagConstraints);
        {
            removeButton.setText(ResourceManager.getText("remove"));
            removeButton.setActionCommand("remove");
            removeButton.addActionListener(handler);
        }
        add(removeButton, gridBagConstraints);

        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.insets = new Insets(4, 0, 4, 0);
        add(new JSeparator(), gridBagConstraints2);
        {
            upButton.setIcon(ResourceManager.getImageIcon(Icons.Up16));
            upButton.setActionCommand("up");
            upButton.addActionListener(handler);
        }
        add(upButton, gridBagConstraints);
        {
            downButton.setIcon(ResourceManager.getImageIcon(Icons.Down16));
            downButton.setActionCommand("down");
            downButton.addActionListener(handler);
        }
        add(downButton, gridBagConstraints);
    }

    public void setGenerator(AssignmentModeGenerator generator)
    {
        boolean assignable = generator.isAssignable();
        addButton.setEnabled(assignable);
        removeButton.setEnabled(assignable);

        List<Integer> scores = generator.getAll();
        int rows = assignable ? 6 : scores.size();
        model.setRowCount(rows);
        for (int i = 0; i < scores.size(); i++)
        {
            model.setValueAt(scores.get(i), i, 0);
        }
    }

    private class ActionHandler implements ActionListener,
                                            ListSelectionListener
    {

        public void actionPerformed(ActionEvent e)
        {
            String command = e.getActionCommand();
            if (command.equals("assignable"))
            {
                boolean selected = assignableBox.isSelected();
                addButton.setEnabled(selected);
                removeButton.setEnabled(selected);
                if (selected)
                {
                    model.setRowCount(6);
                }
            }
            else if (command.equals("add"))
            {
                model.addRow(new Object[]{0});
            }
            else
            {
                int row = table.getSelectedRow();
                if (command.equals("remove"))
                {
                    model.removeRow(row);
                }
                else
                {
                    int newrow = row + (command.equals("up") ? -1 : 1);
                    model.moveRow(row, row, newrow);
                    table.setRowSelectionInterval(newrow, newrow);
                }
            }
        }

        public void valueChanged(ListSelectionEvent e)
        {
            int index = e.getFirstIndex();
            upButton.setEnabled(index > 0);
            downButton.setEnabled(index != -1 && index < model.getRowCount() - 1);
        }

    }
}
