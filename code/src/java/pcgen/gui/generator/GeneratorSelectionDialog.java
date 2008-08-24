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
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.MutableComboBoxModel;
import pcgen.gui.tools.PCGenAction;
import pcgen.gui.tools.ResourceManager;
import pcgen.gui.tools.ResourceManager.Icons;
import pcgen.gui.util.ComboSelectionDialog;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class GeneratorSelectionDialog extends JDialog implements ComboSelectionDialog
{

    private final JList selectedList;
    private final NewAction newAction;
    private final DeleteAction deleteAction;
    private final AddAction addAction;
    private final RemoveAction removeAction;

    public GeneratorSelectionDialog()
    {
        this.selectedList = new JList();
        newAction = new NewAction();
        deleteAction = new DeleteAction();
        addAction = new AddAction();
        removeAction = new RemoveAction();
        initComponents();
    }

    private void initComponents()
    {

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
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {

        }

    }

    private class SelectedGeneratorPanel extends JPanel implements ActionListener
    {

        public SelectedGeneratorPanel()
        {
            super(new GridBagLayout());

            initComponents();
        }

        private void initComponents()
        {
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            add(new JLabel("Selected Generators"), gridBagConstraints);

            //Init selectedList
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

            JButton button = new JButton();
            button.setActionCommand("UP");
            button.setIcon(ResourceManager.getImageIcon(Icons.Up16));
            button.addActionListener(this);
            add(button, gridBagConstraints);

            button = new JButton();
            button.setActionCommand("DOWN");
            button.setIcon(ResourceManager.getImageIcon(Icons.Down16));
            button.addActionListener(this);
            gridBagConstraints.anchor = GridBagConstraints.NORTH;
            add(button, gridBagConstraints);
        }

        public void actionPerformed(ActionEvent e)
        {

        }

    }

    public void setModel(MutableComboBoxModel model)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void display()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
