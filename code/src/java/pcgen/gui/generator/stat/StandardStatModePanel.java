/*
 * StandardStatModePanel.java
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
 * Created on Sep 8, 2008, 6:49:13 PM
 */
package pcgen.gui.generator.stat;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import pcgen.gui.tools.ResourceManager;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class StandardStatModePanel extends JPanel
{

    private final JTextField expressionField;
    private final JSpinner rollDropSpinner;
    private final JSpinner minumumSpinner;
    private final JTextArea textArea;

    public StandardStatModePanel()
    {
        super(new GridBagLayout());
        this.expressionField = new JTextField();
        this.rollDropSpinner = new JSpinner();
        this.minumumSpinner = new JSpinner();
        this.textArea = new JTextArea();
        initComponents();
    }

    private void initComponents()
    {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        add(new JLabel(ResourceManager.getText("diceExp") + ":"),
            gridBagConstraints);

        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        add(expressionField, gridBagConstraints);

        JButton button;
        {
            button = new JButton(ResourceManager.getText("roll"));
            button.addActionListener(
                    new ActionListener()
                    {

                        public void actionPerformed(ActionEvent e)
                        {
                            throw new UnsupportedOperationException("Not supported yet.");
                        }

                    });
        }
        gridBagConstraints.gridwidth = 1;
        add(button, gridBagConstraints);
        {
            button = new JButton(ResourceManager.getText("clear"));
            button.addActionListener(
                    new ActionListener()
                    {

                        public void actionPerformed(ActionEvent e)
                        {
                            throw new UnsupportedOperationException("Not supported yet.");
                        }

                    });
        }
        add(button, gridBagConstraints);

        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.weightx = 1.0;
        add(new JLabel(ResourceManager.getText("dropLow") + ":"),
            gridBagConstraints);

        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        add(rollDropSpinner, gridBagConstraints);

        gridBagConstraints.ipadx = 0;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        add(new JLabel(ResourceManager.getText("rerollMinimum") + ":"),
            gridBagConstraints);

        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        add(minumumSpinner, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        add(new JScrollPane(textArea), gridBagConstraints);
    }

}
