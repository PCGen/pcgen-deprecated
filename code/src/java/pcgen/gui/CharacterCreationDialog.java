/*
 * CharacterCreationDialog.java
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
 * Created on Aug 7, 2008, 6:58:58 PM
 */
package pcgen.gui;

import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.AbstractSpinnerModel;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableColumnModel;
import pcgen.gui.facade.ClassFacade;
import pcgen.gui.facade.RaceFacade;
import pcgen.gui.facade.StatFacade;
import pcgen.gui.generator.Generator;
import pcgen.gui.generator.PurchaseModeGenerator;
import pcgen.gui.util.ComboSelectionBox;
import pcgen.gui.util.DefaultGenericComboBoxModel;
import pcgen.gui.util.GenericComboBoxModel;
import pcgen.gui.util.event.DocumentChangeAdapter;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class CharacterCreationDialog extends JDialog
{

    private final TitledPanel namePanel;
    private final JTextField nameField;
    private final JComboBox namesetComboBox;
    private final TitledPanel alignmentPanel;
    private final JComboBox alignmentComboBox;
    private final TitledPanel genderPanel;
    private final JComboBox genderComboBox;
    private final TitledPanel racePanel;
    private final ComboSelectionBox raceSelectionBox;
    private final TitledPanel statPanel;
    private final ComboSelectionBox statSelectionBox;
    private final JButton statRollButton;
    private final StatPointsLabel statPointsLabel;
    private final StatTablePane statTablePane;
    private final TitledPanel classPanel;
    private final JCheckBox classGenerationCheckBox1;
    private final JCheckBox classGenerationCheckBox2;
    private final JCheckBox classGenerationCheckBox3;
    private final ComboSelectionBox classSelectionBox1;
    private final ComboSelectionBox classSelectionBox2;
    private final ComboSelectionBox classSelectionBox3;
    private final JComboBox levelComboBox1;
    private final JComboBox levelComboBox2;
    private final JComboBox levelComboBox3;
    private final OKAction okAction;
    private final Action cancelAction;
    private CharacterCreationManager creationManager;

    public CharacterCreationDialog()
    {
        this.namePanel = new TitledPanel("Name");
        this.nameField = new JTextField();
        this.namesetComboBox = new JComboBox();
        this.alignmentPanel = new TitledPanel("Alignment");
        this.alignmentComboBox = new JComboBox();
        this.genderPanel = new TitledPanel("Gender");
        this.genderComboBox = new JComboBox();
        this.racePanel = new TitledPanel("Race");
        this.raceSelectionBox = new ComboSelectionBox();
        this.statPanel = new TitledPanel("Stats");
        this.statSelectionBox = new ComboSelectionBox();
        this.statRollButton = new JButton();
        this.statPointsLabel = new StatPointsLabel();
        this.statTablePane = new StatTablePane();
        this.classPanel = new TitledPanel("Classes");
        this.classGenerationCheckBox1 = new JCheckBox();
        this.classGenerationCheckBox2 = new JCheckBox();
        this.classGenerationCheckBox3 = new JCheckBox();
        this.classSelectionBox1 = new ComboSelectionBox();
        this.classSelectionBox2 = new ComboSelectionBox();
        this.classSelectionBox3 = new ComboSelectionBox();
        this.levelComboBox1 = new JComboBox();
        this.levelComboBox2 = new JComboBox();
        this.levelComboBox3 = new JComboBox();
        this.okAction = new OKAction();
        this.cancelAction = new CancelAction();
        initComponents();
    }

    private void initComponents()
    {
        getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        {//Initialize namePanel
            namePanel.setLayout(new GridBagLayout());
            {//Initialize nameField
                nameField.getDocument().addDocumentListener(
                        new DocumentChangeAdapter()
                        {

                            @Override
                            public void documentChanged(DocumentEvent e)
                            {
                                String text = nameField.getText();
                                creationManager.setCharacterNameValidity(text !=
                                                                         null &&
                                                                         text.length() >
                                                                         0);
                            }

                        });
            }
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(4, 0, 4, 0);
            namePanel.add(nameField, gridBagConstraints);
            JButton nameButton;
            {//Initialize nameButton
                nameButton = new JButton(new GenerateNameAction());
                nameButton.setFocusable(false);
            }
            gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
            gridBagConstraints.weightx = 0.0;
            namePanel.add(nameButton, gridBagConstraints);

            gridBagConstraints.gridwidth = 1;
            namePanel.add(new JLabel("Name Set:"), gridBagConstraints);
            {//Initialize namesetComboBox
                namesetComboBox.setModel(createComboBoxModel(PCGenUIManager.getRegisteredNameGenerators()));
            }
            gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
            namePanel.add(namesetComboBox, gridBagConstraints);
        }
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(4, 4, 4, 4);
        getContentPane().add(namePanel, gridBagConstraints);
        {//Initialize alignmentPanel
            alignmentPanel.setLayout(new BorderLayout());
            {//Initialize alignmentComboBox
                alignmentComboBox.addActionListener(
                        new ActionListener()
                        {

                            @SuppressWarnings("unchecked")
                            public void actionPerformed(ActionEvent e)
                            {
                                Generator<Integer> alignmentGenerator = (Generator<Integer>) alignmentComboBox.getSelectedItem();
                                if (alignmentGenerator.isSingleton())
                                {
                                    int alignment = alignmentGenerator.getRandom();
                                    Generator<RaceFacade> raceGenerator = (Generator<RaceFacade>) raceSelectionBox.getSelectedItem();
                                    if (raceGenerator.isSingleton())
                                    {
                                        RaceFacade race = raceGenerator.getRandom();
                                        creationManager.setValidity(creationManager.RACE_VALIDITY,
                                                                    race.isAcceptableAlignment(alignment));
                                    }
                                    Generator<ClassFacade> classGenerator = (Generator<ClassFacade>) classSelectionBox1.getSelectedItem();
                                    if (classGenerator.isSingleton())
                                    {
                                        ClassFacade c = classGenerator.getRandom();
                                        boolean accept = c.isAcceptableAlignment(alignment);
                                        creationManager.setValidity(creationManager.CLASSES_VALIDITY,
                                                                    accept);
                                        if (!accept)
                                        {
                                            return;
                                        }
                                    }
                                    if (!classSelectionBox2.isEnabled())
                                    {
                                        return;
                                    }
                                    classGenerator = (Generator<ClassFacade>) classSelectionBox2.getSelectedItem();
                                    if (classGenerator.isSingleton())
                                    {
                                        ClassFacade c = classGenerator.getRandom();
                                        boolean accept = c.isAcceptableAlignment(alignment);
                                        creationManager.setValidity(creationManager.CLASSES_VALIDITY,
                                                                    accept);
                                        if (!accept)
                                        {
                                            return;
                                        }
                                    }
                                    if (!classSelectionBox3.isEnabled())
                                    {
                                        return;
                                    }
                                    classGenerator = (Generator<ClassFacade>) classSelectionBox3.getSelectedItem();
                                    if (classGenerator.isSingleton())
                                    {
                                        ClassFacade c = classGenerator.getRandom();
                                        creationManager.setValidity(creationManager.CLASSES_VALIDITY,
                                                                    c.isAcceptableAlignment(alignment));
                                    }
                                }
                            }

                        });
            }
            alignmentPanel.add(alignmentComboBox, BorderLayout.CENTER);
        }
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(4, 4, 0, 4);
        getContentPane().add(alignmentPanel, gridBagConstraints);
        {//Initialize genderPanel
            genderPanel.setLayout(new BorderLayout());
            {//Initialize genderComboBox

            }
            genderPanel.add(genderComboBox, BorderLayout.CENTER);
        }
        gridBagConstraints.insets = new Insets(0, 4, 4, 4);
        getContentPane().add(genderPanel, gridBagConstraints);
        {//Initialize racePanel
            racePanel.setLayout(new BorderLayout());
            {//Initialize raceSelectionBox

            }
            racePanel.add(raceSelectionBox, BorderLayout.CENTER);
        }
        gridBagConstraints.insets = new Insets(4, 4, 4, 4);
        getContentPane().add(racePanel, gridBagConstraints);
        {//Initialize statPanel
            statPanel.setLayout(new GridBagLayout());
            {//Initialize statSelectionBox
                statSelectionBox.addItemListener(
                        new ItemListener()
                        {

                            public void itemStateChanged(ItemEvent e)
                            {
                                if (e.getStateChange() == ItemEvent.SELECTED)
                                {
                                    @SuppressWarnings("unchecked")
                                    Generator<Integer> statGenerator = (Generator<Integer>) e.getItem();
                                    StatTableModel model = statTablePane.getModel();

                                    if (statGenerator instanceof PurchaseModeGenerator)
                                    {
                                        PurchaseModeGenerator purchaseMode = (PurchaseModeGenerator) statGenerator;
                                        if (model.setPurchaseMode(purchaseMode))
                                        {
                                            statPointsLabel.setPoints(purchaseMode.getRandom());
                                        }
                                        statTablePane.setUpperLeft(statPointsLabel);
                                    }
                                    else
                                    {
                                        model.setPurchaseMode(null);
                                        statTablePane.setUpperLeft(statRollButton);
                                    }
                                }
                            }

                        });
            }
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(2, 0, 2, 0);
            statPanel.add(statSelectionBox, gridBagConstraints);
            {//Initialize statRollButton
                statRollButton.setAction(new RollStatsAction());
            }
            {//Initialize statTablePane

            }
            statPanel.add(statTablePane, gridBagConstraints);
        }
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.insets = new Insets(4, 4, 4, 4);
        getContentPane().add(statPanel, gridBagConstraints);
        {//Initialize classPanel
            classPanel.setLayout(new GridBagLayout());

            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridwidth = 2;
            classPanel.add(new JLabel(), gridBagConstraints);

            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(0, 4, 0, 0);
            classPanel.add(new JLabel("Class Generation"), gridBagConstraints);

            gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
            gridBagConstraints.weightx = 0.5;
            classPanel.add(new JLabel("Level Generation"), gridBagConstraints);
            SelectClassRowAction rowAction;
            JLabel label;
            {//Initialize classGenerationCheckBox1
                label = new JLabel("1st Class:");
                rowAction = new SelectClassRowAction(classGenerationCheckBox1,
                                                     label,
                                                     classSelectionBox1,
                                                     levelComboBox1);
                classGenerationCheckBox1.setAction(rowAction);
            }
            {//Initialize classSelectionBox1

            }
            {//Initialize levelComboBox1

            }
            initClassSelectionRow(classGenerationCheckBox1, label,
                                  classSelectionBox1,
                                  levelComboBox1);
            {//Initialize classGenerationCheckBox2
                label = new JLabel("2nd Class:");
                SelectClassRowAction action = new SelectClassRowAction(classGenerationCheckBox2,
                                                                       label,
                                                                       classSelectionBox2,
                                                                       levelComboBox2);
                rowAction.setRowAction(action);
                rowAction = action;
                classGenerationCheckBox2.setAction(rowAction);
            }
            {//Initialize classSelectionBox2

            }
            {//Initialize levelComboBox2

            }
            initClassSelectionRow(classGenerationCheckBox2, label,
                                  classSelectionBox2,
                                  levelComboBox2);
            {//Initialize classGenerationCheckBox3
                label = new JLabel("3rd Class:");
                SelectClassRowAction action = new SelectClassRowAction(classGenerationCheckBox3,
                                                                       label,
                                                                       classSelectionBox3,
                                                                       levelComboBox3);
                rowAction.setRowAction(action);
                rowAction = action;
                classGenerationCheckBox2.setAction(rowAction);
            }
            {//Initialize classSelectionBox3

            }
            {//Initialize levelComboBox3

            }
            initClassSelectionRow(classGenerationCheckBox3, label,
                                  classSelectionBox3,
                                  levelComboBox3);
        }
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(4, 4, 4, 4);
        getContentPane().add(classPanel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        getContentPane().add(new JLabel(), gridBagConstraints);

        Dimension buttonSize = new Dimension(75, 23);
        gridBagConstraints.insets = new Insets(4, 4, 4, 4);
        initControlButton(okAction, buttonSize, gridBagConstraints);
        initControlButton(cancelAction, buttonSize, gridBagConstraints);

        pack();
    }

    private void initClassSelectionRow(JCheckBox classGenerationBox,
                                        JLabel label,
                                        ComboSelectionBox classSelectionBox,
                                        JComboBox levelComboBox)
    {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        classPanel.add(classGenerationBox, gridBagConstraints);

        gridBagConstraints.anchor = GridBagConstraints.EAST;
        classPanel.add(label, gridBagConstraints);

        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(2, 0, 2, 4);
        classPanel.add(classSelectionBox, gridBagConstraints);

        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new Insets(2, 0, 2, 2);
        classPanel.add(levelComboBox, gridBagConstraints);
    }

    private void initControlButton(Action action, Dimension buttonSize,
                                    GridBagConstraints gridBagConstraints)
    {
        JButton button = new JButton(action);
        button.setMinimumSize(buttonSize);
        button.setPreferredSize(buttonSize);
        button.setMaximumSize(buttonSize);
        getContentPane().add(button, gridBagConstraints);
    }

    private <T> GenericComboBoxModel<T> createComboBoxModel(List<T> data)
    {
        return new DefaultGenericComboBoxModel<T>(data);
    }

    public void setCharacterCreationManager(CharacterCreationManager manager)
    {
        if (creationManager != null)
        {
            creationManager.removePropertyChangeListener(okAction);
            creationManager.removePropertyChangeListener(CharacterCreationManager.NAME_VALIDITY,
                                                         namePanel);
            creationManager.removePropertyChangeListener(CharacterCreationManager.ALIGNMENT_VALIDITY,
                                                         alignmentPanel);
            creationManager.removePropertyChangeListener(CharacterCreationManager.GENDER_VALIDITY,
                                                         genderPanel);
            creationManager.removePropertyChangeListener(CharacterCreationManager.RACE_VALIDITY,
                                                         racePanel);
            creationManager.removePropertyChangeListener(CharacterCreationManager.STATS_VALIDITY,
                                                         statPanel);
            creationManager.removePropertyChangeListener(CharacterCreationManager.CLASSES_VALIDITY,
                                                         classPanel);
        }
        creationManager = manager;
        if (creationManager != null)
        {
            creationManager.addPropertyChangeListener(okAction);
            creationManager.addPropertyChangeListener(CharacterCreationManager.NAME_VALIDITY,
                                                      namePanel);
            creationManager.addPropertyChangeListener(CharacterCreationManager.ALIGNMENT_VALIDITY,
                                                      alignmentPanel);
            creationManager.addPropertyChangeListener(CharacterCreationManager.GENDER_VALIDITY,
                                                      genderPanel);
            creationManager.addPropertyChangeListener(CharacterCreationManager.RACE_VALIDITY,
                                                      racePanel);
            creationManager.addPropertyChangeListener(CharacterCreationManager.STATS_VALIDITY,
                                                      statPanel);
            creationManager.addPropertyChangeListener(CharacterCreationManager.CLASSES_VALIDITY,
                                                      classPanel);

            nameField.setText(PCGenUIManager.getDefaultCharacterName());
            alignmentComboBox.setModel(createComboBoxModel(creationManager.getAlignmentGenerators()));
            genderComboBox.setModel(createComboBoxModel(creationManager.getGenderGenerators()));
            raceSelectionBox.setModel(createComboBoxModel(creationManager.getRaceGenerators()));
            statSelectionBox.setModel(createComboBoxModel(creationManager.getStatGenerators()));

            DefaultComboBoxModel classComboBoxModel = new DefaultComboBoxModel(creationManager.getClassGenerators().toArray());
            classSelectionBox1.setModel(classComboBoxModel);
            classComboBoxModel = new ExclusiveComboBoxModel(classComboBoxModel);
            classSelectionBox2.setModel(classComboBoxModel);
            classComboBoxModel = new ExclusiveComboBoxModel(classComboBoxModel);
            classSelectionBox3.setModel(classComboBoxModel);

            List<Generator<Integer>> levelGenerators = creationManager.getClassLevelGenerators();
            levelComboBox1.setModel(createComboBoxModel(levelGenerators));
            levelComboBox2.setModel(createComboBoxModel(levelGenerators));
            levelComboBox3.setModel(createComboBoxModel(levelGenerators));
        }
    }

    private class OKAction extends AbstractAction implements PropertyChangeListener
    {

        public OKAction()
        {
            super("OK");
        }

        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void propertyChange(PropertyChangeEvent evt)
        {
            setEnabled(creationManager.isCharacterValid());
        }

    }

    private class CancelAction extends AbstractAction
    {

        public CancelAction()
        {
            super("Cancel");
        }

        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class GenerateNameAction extends AbstractAction
    {

        public GenerateNameAction()
        {
            super("Generate Name");
        }

        public void actionPerformed(ActionEvent e)
        {
            @SuppressWarnings("unchecked")
            Generator<String> nameGenerator = (Generator<String>) namesetComboBox.getSelectedItem();
            nameField.setText(nameGenerator.getRandom());
        }

    }

    private class RollStatsAction extends AbstractAction
    {

        public RollStatsAction()
        {
            super("Roll");
        }

        public void actionPerformed(ActionEvent e)
        {
            @SuppressWarnings("unchecked")
            Generator<Integer> statGenerator = (Generator<Integer>) statSelectionBox.getSelectedItem();
            StatTableModel model = statTablePane.getModel();
            for ( int i = 0; i < model.getRowCount(); i++)
            {
                model.setValueAt(statGenerator.getRandom(), i, 1);
            }
        }

    }

    private static class ExclusiveComboBoxModel extends DefaultComboBoxModel
            implements ListDataListener
    {

        private int excludedIndex = -1;
        private DefaultComboBoxModel excludedModel;

        public ExclusiveComboBoxModel(DefaultComboBoxModel excludedModel)
        {
            this.excludedModel = excludedModel;
            excludedModel.addListDataListener(this);
            for (int index = 0; index < excludedModel.getSize(); index++)
            {
                Object item = excludedModel.getElementAt(index);
                if (item == excludedModel.getSelectedItem())
                {
                    excludedIndex = index;
                }
                else
                {
                    addElement(item);
                }
            }
        }

        public void intervalAdded(ListDataEvent e)
        {
            for (int index = e.getIndex0(); index <= e.getIndex1(); index++)
            {
                if (excludedIndex != -1 && excludedIndex < index)
                {
                    insertElementAt(excludedModel.getElementAt(index), index - 1);
                }
                else
                {
                    insertElementAt(excludedModel.getElementAt(index), index);
                    excludedIndex++;
                }
            }
        }

        public void intervalRemoved(ListDataEvent e)
        {
            for (int index = e.getIndex0(); index <= e.getIndex1(); index++)
            {
                if (excludedIndex != -1 && excludedIndex < e.getIndex0())
                {
                    removeElementAt(e.getIndex0() - 1);
                }
                else
                {
                    removeElementAt(e.getIndex0());
                    if (excludedIndex != -1)
                    {
                        if (excludedIndex == e.getIndex0())
                        {
                            excludedIndex = -1;
                        }
                        excludedIndex--;
                    }
                }
            }
        }

        public void contentsChanged(ListDataEvent e)
        {
            if (e.getIndex0() < 0)
            {
                if (excludedIndex != -1)
                {
                    insertElementAt(excludedModel.getElementAt(excludedIndex),
                                    excludedIndex);
                }
                excludedIndex = excludedModel.getIndexOf(excludedModel.getSelectedItem());
                if (excludedIndex != -1)
                {
                    removeElementAt(excludedIndex);
                }
            }
        }

    }

    private static class SelectClassRowAction extends AbstractAction
    {

        private JCheckBox actionBox;
        private JLabel label;
        private ComboSelectionBox selectionBox;
        private JComboBox comboBox;
        private SelectClassRowAction rowAction;

        public SelectClassRowAction(JCheckBox actionBox, JLabel label,
                                     ComboSelectionBox selectionBox,
                                     JComboBox comboBox)
        {
            this.actionBox = actionBox;
            this.label = label;
            this.selectionBox = selectionBox;
            this.comboBox = comboBox;
        }

        public void actionPerformed(ActionEvent e)
        {
            setEnabled(true);
        }

        @Override
        public void setEnabled(boolean newValue)
        {
            super.setEnabled(newValue);
            label.setEnabled(newValue);
            newValue &= actionBox.isSelected();
            selectionBox.setEnabled(newValue);
            comboBox.setEnabled(newValue);
            if (rowAction != null)
            {
                rowAction.setEnabled(newValue);
            }
        }

        public void setRowAction(SelectClassRowAction rowAction)
        {
            this.rowAction = rowAction;
            rowAction.setEnabled(false);
        }

    }

    private static class TitledPanel extends JPanel implements PropertyChangeListener
    {

        private final TitledBorder titleBorder;

        public TitledPanel(String title)
        {
            this.titleBorder = new TitledBorder(null, title,
                                                TitledBorder.DEFAULT_JUSTIFICATION,
                                                TitledBorder.DEFAULT_POSITION,
                                                new Font("Tahoma",
                                                         Font.BOLD,
                                                         12));
            setBorder(this.titleBorder);
        }

        public void propertyChange(PropertyChangeEvent evt)
        {
            if ((Boolean) evt.getNewValue())
            {
                titleBorder.setTitleColor(Color.BLACK);
            }
            else
            {
                titleBorder.setTitleColor(Color.RED);
            }
            repaint();
        }

    }

    private static class StatPointsLabel extends JLabel
    {

        private int points;

        public int getPoints()
        {
            return points;
        }

        public void setPoints(int points)
        {
            this.points = points;
            repaint();
        }

        @Override
        public String getText()
        {
            return "Points: " + points;
        }

    }

    private static class StatTableModel extends AbstractTableModel
    {

        private static final String[] columns = new String[]{"Base Score",
                                                                "Racial Adj",
                                                                "Total",
                                                                "Mod",
                                                                "Cost"
        };
        private final CharacterCreationManager manager;
        private final List<StatFacade> stats;
        private PurchaseModeGenerator purchaseMode;
        private RaceFacade race;

        public StatTableModel(CharacterCreationManager manager)
        {
            this.manager = manager;
            this.stats = manager.getStats();
        }

        @Override
        public Class<?> getColumnClass(int columnIndex)
        {
            switch (columnIndex)
            {
                case 0:
                    return StatFacade.class;
                default:
                    return Integer.class;
            }
        }

        @Override
        public String getColumnName(int column)
        {
            switch (column)
            {
                case 0:
                    return null;
                default:
                    return columns[column - 1];
            }
        }

        public PurchaseModeGenerator getPurchaseMode()
        {
            return purchaseMode;
        }

        public boolean setPurchaseMode(PurchaseModeGenerator purchaseMode)
        {
            if (this.purchaseMode == purchaseMode)
            {
                return false;
            }
            if (purchaseMode != null)
            {
                for (StatFacade stat : stats)
                {
                    stat.setBaseScore(purchaseMode.getMinScore());
                }
            }
            this.purchaseMode = purchaseMode;
            fireTableStructureChanged();
            return true;
        }

        public void setRace(RaceFacade race)
        {
            this.race = race;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex)
        {
            if (columnIndex == 1)
            {
                return true;
            }
            return false;
        }

        public int getRowCount()
        {
            return stats.size();
        }

        public int getColumnCount()
        {
            if (purchaseMode != null)
            {
                return 6;
            }
            return 5;
        }

        public Object getValueAt(int rowIndex, int columnIndex)
        {
            StatFacade stat = stats.get(rowIndex);
            if (columnIndex == 0)
            {
                return stat;
            }
            int score = stat.getBaseScore();
            if (columnIndex == 1)
            {
                return score;
            }
            if (columnIndex == 5)
            {
                int cost = purchaseMode.getScoreCost(score + 1);
                if (cost != 0)
                {
                    return cost;
                }
                return null;
            }
            int adj = 0;
            if (race != null)
            {
                adj = race.getRacialAdj(stat);
            }
            if (columnIndex == 2)
            {
                return adj;
            }
            score += adj;
            if (columnIndex == 3)
            {
                return score;
            }
            return manager.getModForScore(score);
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex)
        {

        }

    }

    private class StatSpinnerModel extends AbstractSpinnerModel
    {

        public Object getValue()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void setValue(Object value)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Object getNextValue()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Object getPreviousValue()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private static class SpinnerEditor extends AbstractCellEditor
            implements TableCellEditor
    {

        private JSpinner spinner;

        public SpinnerEditor()
        {
            SpinnerNumberModel model = new SpinnerNumberModel();
            model.setMinimum(0);
            this.spinner = new JSpinner(model);
        }

        public Object getCellEditorValue()
        {
            return spinner.getValue();
        }

        public Component getTableCellEditorComponent(JTable table,
                                                      Object value,
                                                      boolean isSelected,
                                                      int row,
                                                      int column)
        {
            if (value == null)
            {
                spinner.setValue(0);
            }
            else
            {
                spinner.setValue(value);
            }
            return spinner;
        }

    }

    private static class StatTablePane extends JScrollPane implements TableModelListener
    {

        private final JTable rowTable;
        private final JTable statTable;
        private StatTableModel model;

        public StatTablePane()
        {
            super(JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                  JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            this.rowTable = new JTable();
            this.statTable = new JTable();
            initComponents();
        }

        private void initComponents()
        {
            rowTable.setPreferredScrollableViewportSize(new Dimension(75, 0));
            rowTable.setAutoCreateColumnsFromModel(false);
            rowTable.setFocusable(false);
            rowTable.addColumn(new TableColumn());
            ListSelectionModel selectionModel = rowTable.getSelectionModel();
            selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            setRowHeaderView(rowTable);

            statTable.setAutoCreateColumnsFromModel(false);
            statTable.setSelectionModel(selectionModel);
            setViewportView(statTable);
        }

        private void createDefaultColumnsFromModel()
        {
            TableColumnModel columnModel = statTable.getColumnModel();
            while (columnModel.getColumnCount() > 0)
            {
                columnModel.removeColumn(columnModel.getColumn(0));
            }

            // Create new columns from the data model info
            for (int i = 1; i < model.getColumnCount(); i++)
            {
                TableColumn column = new TableColumn(i);
                statTable.addColumn(column);
            }
        }

        public StatTableModel getModel()
        {
            return model;
        }

        public void setTableModel(StatTableModel model)
        {
            if (this.model != null)
            {
                this.model.removeTableModelListener(this);
            }
            this.model = model;
            if (this.model != null)
            {
                this.model.addTableModelListener(this);
            }
            rowTable.setModel(model);
            statTable.setModel(model);
            createDefaultColumnsFromModel();
        }

        public void setUpperLeft(Component upperLeft)
        {
            setCorner(JScrollPane.UPPER_LEFT_CORNER, upperLeft);
        }

        @Override
        public Dimension getMinimumSize()
        {
            return getPreferredSize();
        }

        @Override
        public Dimension getPreferredSize()
        {
            Component view = getViewport().getView();
            if (view != null)
            {
                Dimension size = view.getPreferredSize();
                view = getRowHeader();
                if (view != null)
                {
                    size.width += view.getPreferredSize().width;
                }
                view = getColumnHeader();
                if (view != null)
                {
                    size.height += view.getPreferredSize().height;
                }
                Insets insets = getInsets();
                size.width += insets.left + insets.right;
                size.height += insets.top + insets.bottom;
                return size;
            }
            return super.getPreferredSize();
        }

        public void tableChanged(TableModelEvent e)
        {
            if (e.getFirstRow() == TableModelEvent.HEADER_ROW)
            {
                createDefaultColumnsFromModel();
            }
        }

        private static class SpinnerRenderer extends JSpinner implements TableCellRenderer
        {

            public Component getTableCellRendererComponent(JTable table,
                                                            Object value,
                                                            boolean isSelected,
                                                            boolean hasFocus,
                                                            int row,
                                                            int column)
            {
                if (value == null)
                {
                    setValue(0);
                }
                else
                {
                    setValue(value);
                }
                return this;
            }

        }
    }
}
