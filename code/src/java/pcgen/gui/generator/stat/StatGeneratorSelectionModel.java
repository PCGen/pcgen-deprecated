/*
 * StatGeneratorSelectionModel.java
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
 * Created on Sep 15, 2008, 3:21:57 PM
 */
package pcgen.gui.generator.stat;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import pcgen.gui.generator.Generator;
import pcgen.gui.generator.GeneratorFactory;
import pcgen.gui.tools.ResourceManager;
import pcgen.gui.tools.SelectionDialog;
import pcgen.gui.tools.SelectionDialogModel;
import pcgen.gui.util.GenericListModel;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class StatGeneratorSelectionModel implements SelectionDialogModel<Generator<Integer>>
{

    private static final String ASSIGNMENT_MODE = "assignment";
    private static final String STANDARD_MODE = "dieRoll";
    private static final String PURCHASE_MODE = "purchase";
    private static final Properties props = new Properties();

    static
    {
        props.setProperty(SelectionDialogModel.AVAILABLE_TEXT_PROP,
                          "availStatGen");
        props.setProperty(SelectionDialogModel.SELECTION_TEXT_PROP, "selStatGen");
        props.setProperty(SelectionDialogModel.NEW_TOOLTIP_PROP, "newStatGen");
        props.setProperty(SelectionDialogModel.COPY_TOOLTIP_PROP, "copyStatGen");
        props.setProperty(SelectionDialogModel.DELETE_TOOLTIP_PROP,
                          "deleteStatGen");
        props.setProperty(SelectionDialogModel.ADD_TOOLTIP_PROP, "addStatGen");
        props.setProperty(SelectionDialogModel.REMOVE_TOOLTIP_PROP,
                          "removeStatGen");
    }

    private final ModeSelectionPanel modePanel;
    private final Map<String, StatModePanel> panelMap;

    public StatGeneratorSelectionModel()
    {
        this.modePanel = new ModeSelectionPanel();
        this.panelMap = new HashMap<String, StatModePanel>();
        panelMap.put(ASSIGNMENT_MODE, new AssignmentModePanel());
        panelMap.put(STANDARD_MODE, new StandardModePanel());
        panelMap.put(PURCHASE_MODE, new PurchaseModePanel());
    }

    public GenericListModel<Generator<Integer>> getAvailableList()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public GenericListModel<Generator<Integer>> getSelectedList()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setAvailableList(GenericListModel<Generator<Integer>> list)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setSelectedList(GenericListModel<Generator<Integer>> list)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @SuppressWarnings("unchecked")
    public Component getItemPanel(SelectionDialog<Generator<Integer>> selectionDialog,
                                   Component currentItemPanel,
                                   Generator<Integer> selectedItem)
    {
        StatModePanel panel = (StatModePanel) currentItemPanel;
        if (panel != null)
        {
            panel.saveGeneratorData();
        }

        String mode = getMode(selectedItem);
        panel = panelMap.get(mode);
        panel.setGenerator(selectedItem);
        return panel;
    }

    public Generator<Integer> createMutableItem(SelectionDialog<Generator<Integer>> selectionDialog,
                                                 Generator<Integer> templateItem)
    {
        Generator<Integer> generator = null;
        String name;
        String mode;
        if (templateItem == null)
        {
            modePanel.resetGeneratorName();
            int ret = JOptionPane.showConfirmDialog(selectionDialog, modePanel,
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
            name = JOptionPane.showInputDialog(selectionDialog,
                                               ResourceManager.getText("createGen"),
                                               "Select Name",
                                               JOptionPane.QUESTION_MESSAGE);
            mode = getMode(templateItem);
        }
        if (name != null)
        {
            if (mode == ASSIGNMENT_MODE)
            {
                generator = GeneratorFactory.createMutableAssignmentModeGenerator(name,
                                                                                  (AssignmentModeGenerator) templateItem);
            }
            else if (mode == STANDARD_MODE)
            {
                generator = GeneratorFactory.createMutableStandardModeGenerator(name,
                                                                                (StandardModeGenerator) templateItem);
            }
            else
            {
                generator = GeneratorFactory.createMutablePurchaseModeGenerator(name,
                                                                                (PurchaseModeGenerator) templateItem);
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

    public boolean isMutable(Object item)
    {
        return item instanceof MutableAssignmentModeGenerator ||
                item instanceof MutablePurchaseModeGenerator ||
                item instanceof MutableStandardModeGenerator;
    }

    public Properties getDisplayProperties()
    {
        return props;
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
