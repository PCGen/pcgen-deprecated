/*
 * ComboSelectionPanel.java
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
 * Created on Jul 22, 2008, 4:58:45 PM
 */
package pcgen.gui.tools;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import pcgen.gui.filter.FilteredTreeViewPanel;
import pcgen.gui.util.JTreeViewPane;
import pcgen.gui.util.JTreeViewSelectionPane;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class ComboSelectionPanel extends JPanel
{

    private JComboBox comboBox;
    private JButton button;

    public ComboSelectionPanel()
    {
        super(new BorderLayout());
        initComponents();
    }

    private void initComponents()
    {
        comboBox = new JComboBox();

        setBorder(comboBox.getBorder());
        comboBox.setBorder(BorderFactory.createEmptyBorder());

        button = new JButton(new ButtonAction());
        button.setMargin(new java.awt.Insets(0, 0, 0, 0));

        add(comboBox, BorderLayout.CENTER);
        add(button, BorderLayout.LINE_END);
    }

    private class ButtonAction extends AbstractAction
    {

        public ButtonAction()
        {
            super("...");
        }

        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class FilteredSelectionPanel extends FilteredTreeViewPanel
    {

        @Override
        public JTreeViewPane createDefaultTreeViewPane()
        {
            return new JTreeViewSelectionPane();
        }

    }
}
