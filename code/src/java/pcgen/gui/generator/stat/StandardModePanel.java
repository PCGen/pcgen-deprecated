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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import org.apache.commons.lang.math.NumberUtils;
import pcgen.core.RollingMethods;
import pcgen.gui.tools.ResourceManager;
import pcgen.gui.util.event.DocumentChangeAdapter;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
class StandardModePanel extends StatModePanel<StandardModeGenerator>
{

    private final JCheckBox assignableBox;
    private final JTextField mainExpressionField;
    private final JLabel[] statLabels = new JLabel[6];
    private final JTextField[] expressionFields = new JTextField[6];
    private final JTextField[] resultFields = new JTextField[6];
    private StandardModeGenerator generator = null;

    public StandardModePanel()
    {
        this.assignableBox = new JCheckBox();
        this.mainExpressionField = new JTextField();
        initComponents();
    }

    private void initComponents()
    {
        ActionHandler handler = new ActionHandler();

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        {
            assignableBox.setText(ResourceManager.getText("assignable"));
            assignableBox.setActionCommand("assignable");
            assignableBox.addActionListener(handler);
        }
        add(assignableBox, gridBagConstraints);
        {
            mainExpressionField.getDocument().addDocumentListener(handler);
        }
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        add(mainExpressionField, gridBagConstraints);

        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        initButton("rollAll", handler, gridBagConstraints);
        for (int x = 0; x < 6; x++)
        {
            statLabels[x] = new JLabel();
            expressionFields[x] = new JTextField();
            resultFields[x] = new JTextField();

            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.anchor = GridBagConstraints.WEST;
            add(statLabels[x], gridBagConstraints2);
            {
                expressionFields[x].getDocument().addDocumentListener(handler);
            }
            gridBagConstraints.gridwidth = GridBagConstraints.RELATIVE;
            add(expressionFields[x], gridBagConstraints);
            initButton("roll", String.valueOf(x), handler, gridBagConstraints);
            {
                resultFields[x].setEditable(false);
            }
            gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
            add(resultFields[x], gridBagConstraints);
        }
    }

    private void initButton(String prop, String command,
                             ActionListener listener,
                             GridBagConstraints gridBagConstraints)
    {
        JButton button = new JButton(ResourceManager.getText(prop));
        button.setActionCommand(command);
        button.addActionListener(listener);
        add(button, gridBagConstraints);
    }

    private void initButton(String prop, ActionListener listener,
                             GridBagConstraints gridBagConstraints)
    {
        initButton(prop, prop, listener, gridBagConstraints);
    }

    public void setGenerator(StandardModeGenerator generator)
    {
        this.generator = generator;
        boolean editable = generator instanceof MutableStandardModeGenerator;
        assignableBox.setSelected(generator.isAssignable());
        assignableBox.setEnabled(editable);
        List<String> expressions = generator.getDiceExpressions();
        if (Collections.frequency(expressions, expressions.get(0)) == 6)
        {
            mainExpressionField.setText(expressions.get(0));
        }
        else
        {
            mainExpressionField.setText(null);
        }
        mainExpressionField.setEnabled(editable);
        for (int x = 0; x < 6; x++)
        {
            expressionFields[x].setText(expressions.get(x));
            expressionFields[x].setEnabled(editable);
            resultFields[x].setText(null);
        }
    }

    public void saveGeneratorData()
    {
        if (generator instanceof MutableStandardModeGenerator)
        {
            MutableStandardModeGenerator gen = (MutableStandardModeGenerator) generator;
            gen.setAssignable(assignableBox.isSelected());
            List<String> expressions = new ArrayList<String>();
            for (int x = 0; x < 6; x++)
            {
                expressions.add(expressionFields[x].getText());
            }
            gen.setDiceExpressions(expressions);
        }
    }
    //TODO internationalize
    private void setupStatLabels()
    {

        if (assignableBox.isSelected())
        {
            for (int x = 0; x < 6; x++)
            {
                statLabels[x].setText("Stat " + (x + 1) + ":");
            }
        }
        else
        {
            statLabels[0].setText("Strength:");
            statLabels[1].setText("Dexterity:");
            statLabels[2].setText("Constitution:");
            statLabels[3].setText("Intelligence:");
            statLabels[4].setText("Wisdom:");
            statLabels[5].setText("Charisma:");
        }
    }
    private void roll(int index)
    {
        String expression = expressionFields[index].getText();
        resultFields[index].setText(String.valueOf(RollingMethods.roll(expression)));
    }

    private class ActionHandler extends DocumentChangeAdapter implements ActionListener
    {

        public void actionPerformed(ActionEvent e)
        {
            String command = e.getActionCommand();
            if (command.equals("rollALL"))
            {
                for (int x = 0; x < 6; x++)
                {
                    roll(x);
                }
            }
            else if (command.equals("assignable"))
            {
                setupStatLabels();
            }
            else
            {
                roll(NumberUtils.toInt(command));
            }
        }

        @Override
        public void documentChanged(DocumentEvent e)
        {
            if (e.getDocument() == mainExpressionField.getDocument())
            {
                for (int x = 0; x < 6; x++)
                {
                    expressionFields[x].setText(mainExpressionField.getText());
                }
            }
            else
            {
                mainExpressionField.setText(null);
            }
        }

    }
}
