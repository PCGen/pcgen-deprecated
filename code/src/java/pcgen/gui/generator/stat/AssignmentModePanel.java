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
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import pcgen.gui.tools.ResourceManager;
import pcgen.gui.tools.ResourceManager.Icons;
import pcgen.gui.util.table.TableCellUtilities;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class AssignmentModePanel extends JPanel implements StatModePanel<AssignmentModeGenerator>
{

    private final DefaultTableModel model;
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
        this.model = new DefaultTableModel()
        {

            @Override
            public boolean isCellEditable(int row, int column)
            {
                return generator != null;
            }

        };
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
        GridBagConstraints gridBagConstraints;
        GridBagConstraints gridBagConstraints2;
        {
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = GridBagConstraints.NORTH;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        }
        ActionHandler handler = new ActionHandler();
        table.setShowGrid(false);
        table.setDefaultRenderer(Integer.class,
                                 new TableCellUtilities.SpinnerRenderer());
        table.setDefaultEditor(Integer.class,
                               new TableCellUtilities.SpinnerEditor(new SpinnerNumberModel(0,
                                                                                           0,
                                                                                           null,
                                                                                           1)));
        model.setColumnIdentifiers(new Object[]{ResourceManager.getText("score")});
        table.setModel(model);

        ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionModel.addListSelectionListener(handler);

        gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridheight = GridBagConstraints.REMAINDER;
        gridBagConstraints2.fill = GridBagConstraints.BOTH;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.weighty = 1.0;
        add(new JScrollPane(table), gridBagConstraints2);

        initButton(assignableBox, "assignable", null, handler,
                   gridBagConstraints);
        initButton(addButton, "add", null, handler, gridBagConstraints);
        initButton(removeButton, "remove", null, handler, gridBagConstraints);

        gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.insets = new Insets(4, 0, 4, 0);
        gridBagConstraints2.ipady = 1;
        add(new JSeparator(), gridBagConstraints2);

        initButton(upButton, "up", Icons.Up16, handler, gridBagConstraints);
        initButton(downButton, "down", Icons.Down16, handler, gridBagConstraints);
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
        List<Integer> scores = generator.getAll();
        int rows = assignable ? 6 : scores.size();
        model.setRowCount(rows);
        for (int i = 0; i < scores.size(); i++)
        {
            model.setValueAt(scores.get(i), i, 0);
        }

        boolean editable = generator instanceof MutableAssignmentModeGenerator;
        assignableBox.setSelected(assignable);
        assignableBox.setEnabled(editable);
        upButton.setEnabled(editable);
        downButton.setEnabled(editable);
        addButton.setEnabled(editable && assignable);
        removeButton.setEnabled(editable && assignable);

        this.generator = editable ? (MutableAssignmentModeGenerator) generator : null;
    }

    public void saveGeneratorData()
    {
        if (generator != null)
        {
            generator.setAssignable(assignableBox.isSelected());
            int rowcount = model.getRowCount();
            List<Integer> scores = new ArrayList<Integer>(rowcount);
            for (int i = 0; i < rowcount; i++)
            {
                scores.add((Integer) model.getValueAt(i, 0));
            }
            generator.setScores(scores);
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
