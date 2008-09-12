/*
 * StatGeneratorSelectionDialog.java
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
 * Created on Aug 31, 2008, 1:09:10 AM
 */
package pcgen.gui.generator.stat;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pcgen.gui.generator.Generator;
import pcgen.gui.tools.AbstractSelectionDialog;
import pcgen.gui.tools.ResourceManager;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class StatGeneratorSelectionDialog extends AbstractSelectionDialog<Generator<Integer>>
{

    private static final String ASSIGNMENT_MODE = "assignment";
    private static final String STANDARD_MODE = "dieRoll";
    private static final String PURCHASE_MODE = "purchase";
    private final ModeSelectionPanel modePanel;
    private CardLayout cards;
    private JPanel cardPanel;
    private Map<String, StatModePanel> cardMap;
    private StatModePanel currentPanel = null;

    public StatGeneratorSelectionDialog()
    {
        super(ResourceManager.getText("availStatGen"),
              ResourceManager.getText("selStatGen"),
              ResourceManager.getToolTip("newStatGen"),
              ResourceManager.getToolTip("copyStatGen"),
              ResourceManager.getToolTip("deleteStatGen"),
              ResourceManager.getToolTip("addStatGen"),
              ResourceManager.getToolTip("removeStatGen"));
        this.modePanel = new ModeSelectionPanel();
    }

    protected void initComponents()
    {
        this.cards = new CardLayout();
        this.cardPanel = new JPanel(cards);
        this.cardMap = new HashMap<String, StatModePanel>();
        StatModePanel panel;
        panel = new AssignmentModePanel();
        cardMap.put(ASSIGNMENT_MODE, panel);
        cardPanel.add(panel, ASSIGNMENT_MODE);

        panel = new StandardModePanel();
        cardMap.put(STANDARD_MODE, panel);
        cardPanel.add(panel, STANDARD_MODE);

        panel = new PurchaseModePanel();
        cardMap.put(PURCHASE_MODE, panel);
        cardPanel.add(panel, PURCHASE_MODE);

        SelectionHandler handler = new SelectionHandler();
        availableList.addListSelectionListener(handler);
        selectedList.addListSelectionListener(handler);
    }

    @Override
    protected Component getLeftComponent()
    {
        return cardPanel;
    }

    @Override
    protected Generator<Integer> createMutableItem(Generator<Integer> item)
    {
        Generator<Integer> generator = null;
        String name;
        String mode;
        if (item == null)
        {
            modePanel.resetGeneratorName();
            int ret = JOptionPane.showConfirmDialog(this, modePanel,
                                                    "Generator Details",
                                                    JOptionPane.OK_CANCEL_OPTION);
            if (ret != JOptionPane.OK_OPTION)
            {
                return null;
            }
            name = modePanel.getGeneratorName();
            mode = modePanel.getMode();
        }
        else
        {
            name = JOptionPane.showInputDialog(this,
                                               ResourceManager.getText("createGen"),
                                               "Select Name",
                                               JOptionPane.QUESTION_MESSAGE);
            mode = getMode(item);
        }
        if (name != null)
        {
            if (mode == ASSIGNMENT_MODE)
            {
                generator = StatGeneratorFactory.createMutableAssignmentModeGenerator(name,
                                                                                      (AssignmentModeGenerator) item);
            }
            else if (mode == STANDARD_MODE)
            {
                generator = StatGeneratorFactory.createMutableStandardModeGenerator(name,
                                                                                    (StandardModeGenerator) item);
            }
            else
            {
                generator = StatGeneratorFactory.createMutablePurchaseModeGenerator(name,
                                                                                    (PurchaseModeGenerator) item);
            }
        }
        return generator;
    }

    private static String getMode(Generator<Integer> generator)
    {
        return generator instanceof AssignmentModeGenerator ? ASSIGNMENT_MODE
                : generator instanceof StandardModeGenerator ? STANDARD_MODE
                : generator instanceof PurchaseModeGenerator ? PURCHASE_MODE
                : null;
    }

    @Override
    protected boolean isMutable(Object item)
    {
        return item instanceof MutableAssignmentModeGenerator ||
                item instanceof MutablePurchaseModeGenerator ||
                item instanceof MutableStandardModeGenerator;
    }

    private class SelectionHandler implements ListSelectionListener
    {

        @SuppressWarnings("unchecked")
        public void valueChanged(ListSelectionEvent e)
        {
            JList list = (JList) e.getSource();
            Generator<Integer> generator = (Generator<Integer>) list.getSelectedValue();
            if (generator != null)
            {
                if (currentPanel != null)
                {
                    currentPanel.saveGeneratorData();
                }
                String mode = getMode(generator);
                StatModePanel panel = cardMap.get(mode);
                panel.setGenerator(generator);
                currentPanel = panel;
                cards.show(cardPanel, mode);
            }
        }

    }

    private static class ModeSelectionPanel extends JPanel implements AncestorListener
    {

        private final JTextField nameField;
        private final ButtonGroup group;

        public ModeSelectionPanel()
        {
            super(new GridBagLayout());
            this.nameField = new JTextField();
            this.group = new ButtonGroup();
            initComponents();
        }

        private void initComponents()
        {
            addAncestorListener(this);
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            add(new JLabel(ResourceManager.getText("genName") + ":"),
                gridBagConstraints);


            gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            add(nameField, gridBagConstraints);

            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.setBorder(new TitledBorder(null,
                                             ResourceManager.getText("genMode"),
                                             TitledBorder.DEFAULT_JUSTIFICATION,
                                             TitledBorder.DEFAULT_POSITION,
                                             new Font("Tahoma",
                                                      Font.BOLD,
                                                      12)));
            initRadioButton(panel, ASSIGNMENT_MODE);
            initRadioButton(panel, STANDARD_MODE).setSelected(true);
            initRadioButton(panel, PURCHASE_MODE);
            gridBagConstraints.weightx = 1.0;
            add(panel, gridBagConstraints);
        }

        private JRadioButton initRadioButton(JPanel panel, String command)
        {
            JRadioButton button = new JRadioButton(ResourceManager.getText(command));
            button.setActionCommand(command);
            group.add(button);
            panel.add(button);
            return button;
        }

        public void resetGeneratorName()
        {
            nameField.setText(null);
        }

        public String getGeneratorName()
        {
            return nameField.getText();
        }

        public String getMode()
        {
            return group.getSelection().getActionCommand();
        }

        public void ancestorAdded(AncestorEvent event)
        {
            nameField.requestFocusInWindow();
        }

        public void ancestorRemoved(AncestorEvent event)
        {

        }

        public void ancestorMoved(AncestorEvent event)
        {

        }

    }
}
