/*
 * StandardModePanel.java
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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import pcgen.gui.tools.ResourceManager;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class StandardModePanel extends StatModePanel<StandardModeGenerator>
{

    private final JTextField expressionField;
    private final JSpinner rollDropSpinner;
    private final JSpinner minimumSpinner;
    private final JTextArea textArea;
    private StandardModeGenerator generator = null;

    public StandardModePanel()
    {
        this.expressionField = new JTextField();
        this.rollDropSpinner = new JSpinner();
        this.minimumSpinner = new JSpinner();
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

        ActionHandler handler = new ActionHandler();
        gridBagConstraints.gridwidth = 1;
        initButton("roll", handler, gridBagConstraints);
        initButton("clear", handler, gridBagConstraints);

        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.weightx = 1.0;
        add(new JLabel(ResourceManager.getText("dropLowCount") + ":"),
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
        add(minimumSpinner, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        add(new JScrollPane(textArea), gridBagConstraints);
    }

    private void initButton(String prop, ActionListener listener,
                             GridBagConstraints gridBagConstraints)
    {
        JButton button = new JButton(ResourceManager.getText(prop));
        button.setActionCommand(prop);
        button.addActionListener(listener);
        add(button, gridBagConstraints);
    }

    public void setGenerator(StandardModeGenerator generator)
    {
        this.generator = generator;

        expressionField.setText(generator.getDiceExpression());
        rollDropSpinner.setValue(generator.getDropCount());
        minimumSpinner.setValue(generator.getRerollMinimum());
        textArea.setText(null);

        boolean editable = generator instanceof MutableStandardModeGenerator;
        expressionField.setEnabled(editable);
        rollDropSpinner.setEnabled(editable);
        minimumSpinner.setEnabled(editable);
    }

    public void saveGeneratorData()
    {
        if (generator instanceof MutableStandardModeGenerator)
        {
            MutableStandardModeGenerator gen = (MutableStandardModeGenerator) generator;
            gen.setDiceExpression(expressionField.getText());
            gen.setDropCount((Integer) rollDropSpinner.getValue());
            gen.setRerollMinimum((Integer) minimumSpinner.getValue());
        }
    }

    private class ActionHandler implements ActionListener
    {

        public void actionPerformed(ActionEvent e)
        {
            if (e.getActionCommand().equals("roll"))
            {
                saveGeneratorData();
                textArea.append(generator.getNext() + "\n");
            }
            else
            {
                textArea.setText(null);
            }
        }

    }
}
