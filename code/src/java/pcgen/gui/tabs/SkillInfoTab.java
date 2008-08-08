/*
 * SkillInfoTab.java
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
 * Created on Jul 10, 2008, 8:03:21 PM
 */
package pcgen.gui.tabs;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import pcgen.gui.tools.ChooserPane;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import javax.swing.AbstractCellEditor;
import javax.swing.AbstractSpinnerModel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.gui.PCGenUIManager;
import pcgen.gui.PCGenUIManager.HouseRule;
import pcgen.gui.facade.CharacterFacade;
import pcgen.gui.facade.CharacterLevelFacade;
import pcgen.gui.facade.ClassFacade;
import pcgen.gui.facade.SkillFacade;
import pcgen.gui.filter.FilterableTreeViewModel;
import pcgen.gui.filter.FilteredTreeViewPanel;
import pcgen.gui.util.GenericListModel;
import pcgen.gui.util.treeview.DataView;
import pcgen.gui.util.treeview.DataViewColumn;
import pcgen.gui.util.treeview.DefaultDataViewColumn;
import pcgen.gui.util.treeview.TreeView;
import pcgen.gui.util.treeview.TreeViewPath;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class SkillInfoTab extends ChooserPane implements CharacterStateEditable
{

    private final FilteredTreeViewPanel skillPanel;
    private final JTable skillcostTable;
    private final JTable skillpointTable;
    private SkillTreeViewModel treeviewModel;
    private SkillPointTableModel skillpointModel;
    private SkillCostTableModel skillcostModel;
    private CharacterLevelFacade selectedLevel;
    private SkillFacade selectedSkill;

    public SkillInfoTab()
    {
        this.skillPanel = new FilteredTreeViewPanel();
        this.skillcostTable = new JTable();
        this.skillpointTable = new JTable();
        initComponents();
    }

    private void initComponents()
    {
        skillPanel.setDefaultRenderer(Float.class,
                                      new SkillRankSpinnerRenderer());
        ListSelectionModel selectionModel = skillPanel.getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionModel.addListSelectionListener(
                new ListSelectionListener()
                {

                    public void valueChanged(ListSelectionEvent e)
                    {
                        if (!e.getValueIsAdjusting())
                        {
                            List<Object> data = skillPanel.getSelectedData();
                            SkillFacade skill = null;
                            if (!data.isEmpty() &&
                                    data.get(0) instanceof SkillFacade)
                            {
                                skill = (SkillFacade) data.get(0);
                            }
                            setSelectedSkill(skill);
                        }
                    }

                });
        setPrimaryChooserComponent(skillPanel);

        JScrollPane tableScrollPane;
        JPanel tablePanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        constraints.fill = java.awt.GridBagConstraints.BOTH;
        constraints.ipady = 60;
        constraints.weightx = 1.0;

        tableScrollPane = new JScrollPane(skillcostTable,
                                          JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                                          JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        tablePanel.add(tableScrollPane, constraints);

        constraints.ipady = 0;
        constraints.weighty = 1.0;

        tableScrollPane = new JScrollPane(skillpointTable);
        tablePanel.add(tableScrollPane, constraints);
        setSecondaryChooserComponent(tablePanel);
    }

    private void setSelectedSkill(SkillFacade skill)
    {
        this.selectedSkill = skill;
        setInfoPaneText(skill.getInfo());
    }

    private void setSelectedLevel(CharacterLevelFacade selectedLevel)
    {
        this.selectedLevel = selectedLevel;
        skillcostModel.setCharacterLevel(selectedLevel);
        treeviewModel.setCharacterLevel(selectedLevel);
    }

    public Hashtable<Object, Object> createState(CharacterFacade character)
    {
        SkillTreeViewModel viewModel = new SkillTreeViewModel(character);
        SkillCostTableModel costModel = new SkillCostTableModel();
        SkillPointTableModel pointModel = new SkillPointTableModel(character);
        SkillRankSpinnerEditor rankEditor = new SkillRankSpinnerEditor(character);

        Hashtable<Object, Object> state = skillPanel.createState(character,
                                                                 viewModel);
        state.put(SkillTreeViewModel.class, viewModel);
        state.put(SkillCostTableModel.class, costModel);
        state.put(SkillPointTableModel.class, pointModel);
        state.put(SkillRankSpinnerEditor.class, rankEditor);
        state.put("SelectedCharacterLevel",
                  character.getLevels().getElementAt(0));
        return state;
    }

    public void storeState(Hashtable<Object, Object> state)
    {
        state.put("SelectedCharacterLevel", selectedLevel);
        state.put("SelectedSkill", selectedSkill);
    }

    public void restoreState(Hashtable<?, ?> state)
    {
        treeviewModel = (SkillTreeViewModel) state.get(SkillTreeViewModel.class);
        skillcostModel = (SkillCostTableModel) state.get(SkillCostTableModel.class);
        skillpointModel = (SkillPointTableModel) state.get(SkillPointTableModel.class);

        skillcostTable.setModel(skillcostModel);
        skillpointTable.setModel(skillpointModel);

        skillPanel.restoreState(state);
        skillPanel.setDefaultEditor(Float.class,
                                    (SkillRankSpinnerEditor) state.get(SkillRankSpinnerEditor.class));
        setSelectedLevel((CharacterLevelFacade) state.get("SelectedCharacterLevel"));
        setSelectedSkill((SkillFacade) state.get("SelectedSkill"));
    }

    private class SkillRankSpinnerEditor extends AbstractCellEditor
            implements TableCellEditor, ChangeListener
    {

        private final JSpinner spinner;
        private final SkillRankSpinnerModel model;

        public SkillRankSpinnerEditor(CharacterFacade character)
        {
            this.model = new SkillRankSpinnerModel(character);
            this.spinner = new JSpinner(model);
            spinner.addChangeListener(this);
        }

        public Object getCellEditorValue()
        {
            return model.getValue();
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                      boolean isSelected,
                                                      int row,
                                                      int column)
        {
            model.setSkill(selectedSkill);
            model.setLevel(selectedLevel);
            return spinner;
        }

        public void stateChanged(ChangeEvent e)
        {
            stopCellEditing();
        }

    }

    private static final class SkillTreeViewModel implements FilterableTreeViewModel<SkillFacade>,
                                                                DataView<SkillFacade>
    {

        private enum SkillTreeView implements TreeView<SkillFacade>
        {

            NAME("Name"),
            TYPE_NAME("Type/Name"),
            KEYSTAT_NAME("Key Stat/Name"),
            KEYSTAT_TYPE_NAME("Key Stat/Type/Name");
            private String name;

            private SkillTreeView(String name)
            {
                this.name = name;
            }

            public String getViewName()
            {
                return name;
            }

            @SuppressWarnings("unchecked")
            public List<TreeViewPath<SkillFacade>> getPaths(SkillFacade pobj)
            {
                TreeViewPath<SkillFacade> path;
                switch (this)
                {
                    case NAME:
                        path = new TreeViewPath<SkillFacade>(pobj);
                        break;
                    case TYPE_NAME:
                        path = new TreeViewPath<SkillFacade>(pobj,
                                                             pobj.getType());
                        break;
                    case KEYSTAT_NAME:
                        path = new TreeViewPath<SkillFacade>(pobj,
                                                             pobj.getKeyStat());
                        break;
                    case KEYSTAT_TYPE_NAME:
                        path = new TreeViewPath<SkillFacade>(pobj,
                                                             pobj.getKeyStat(),
                                                             pobj.getType());
                        break;
                    default:
                        throw new InternalError();
                }
                return Arrays.asList(path);
            }

        }
        private final TreeView<SkillFacade> COST_NAME = new TreeView<SkillFacade>()
        {

            public String getViewName()
            {
                return "Cost/Name";
            }

            @SuppressWarnings("unchecked")
            public List<TreeViewPath<SkillFacade>> getPaths(SkillFacade pobj)
            {
                return Arrays.asList(new TreeViewPath<SkillFacade>(pobj,
                                                                   level.getSkillCost(pobj)));
            }

        };
        private final TreeView<SkillFacade> COST_TYPE_NAME = new TreeView<SkillFacade>()
        {

            public String getViewName()
            {
                return "Cost/Type/Name";
            }

            @SuppressWarnings("unchecked")
            public List<TreeViewPath<SkillFacade>> getPaths(SkillFacade pobj)
            {
                return Arrays.asList(new TreeViewPath<SkillFacade>(pobj,
                                                                   level.getSkillCost(pobj),
                                                                   pobj.getType()));
            }

        };
        private static final List<? extends DataViewColumn> columns = Arrays.asList(
                new DefaultDataViewColumn("Total", Integer.class, true),
                new DefaultDataViewColumn("Modifier", Integer.class, true),
                new DefaultDataViewColumn("Ranks", Float.class, true, true),
                new DefaultDataViewColumn("Skill Cost", SkillCost.class,
                                          true),
                new DefaultDataViewColumn("Source", String.class));
        private CharacterFacade character;
        private CharacterLevelFacade level;

        public SkillTreeViewModel(CharacterFacade character)
        {
            this.character = character;
        }

        public void setCharacterLevel(CharacterLevelFacade level)
        {
            this.level = level;
        }

        public Class<SkillFacade> getFilterClass()
        {
            return SkillFacade.class;
        }

        public List<?> getData(SkillFacade obj)
        {
            return Arrays.asList(
                    character.getSkillTotal(obj),
                    character.getSkillModifier(obj),
                    Float.valueOf(character.getSkillRanks(obj)),
                    level.getSkillCost(obj),
                    obj.getSource());
        }

        public List<? extends DataViewColumn> getDataColumns()
        {
            return columns;
        }

        public DataView<SkillFacade> getDataView()
        {
            return this;
        }

        @SuppressWarnings("unchecked")
        public List<? extends TreeView<SkillFacade>> getTreeViews()
        {
            return Arrays.asList(SkillTreeView.NAME, SkillTreeView.TYPE_NAME,
                                 SkillTreeView.KEYSTAT_NAME,
                                 SkillTreeView.KEYSTAT_TYPE_NAME, COST_NAME,
                                 COST_TYPE_NAME);
        }

        public int getDefaultTreeViewIndex()
        {
            return 0;
        }

        public GenericListModel<SkillFacade> getDataModel()
        {
            return PCGenUIManager.getRegisteredSkills(character);
        }

    }

    private static class SkillCostTableModel extends AbstractTableModel
    {

        private CharacterLevelFacade level;

        public void setCharacterLevel(CharacterLevelFacade level)
        {
            this.level = level;
            fireTableRowsUpdated(0, 2);
        }

        public int getRowCount()
        {
            return 3;
        }

        public int getColumnCount()
        {
            return 3;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex)
        {
            switch (columnIndex)
            {
                case 0:
                    return String.class;
                case 1:
                    return Integer.class;
                case 2:
                    return Float.class;
                default:
                    return Object.class;
            }
        }

        @Override
        public String getColumnName(int column)
        {
            switch (column)
            {
                case 0:
                    return "Skill Cost";
                case 1:
                    return "Rank Cost";
                case 2:
                    return "Max Ranks";
                default:
                    throw new IndexOutOfBoundsException();
            }
        }

        public Object getValueAt(int rowIndex, int columnIndex)
        {
            if (level == null)
            {
                return null;
            }
            SkillCost cost = SkillCost.values()[rowIndex];
            switch (columnIndex)
            {
                case 0:
                    return cost;
                case 1:
                    return level.getRankCost(cost);
                case 2:
                    return level.getMaxRanks(cost);
                default:
                    throw new IndexOutOfBoundsException();
            }
        }

    }

    private static class SkillPointTableModel extends AbstractTableModel
            implements ListDataListener
    {

        private static final String[] columns = {"Level",
                                                    "Class",
                                                    "Spent",
                                                    "Gained"
        };
        private final GenericListModel<CharacterLevelFacade> model;

        public SkillPointTableModel(CharacterFacade character)
        {
            model = character.getLevels();
            model.addListDataListener(this);
        }

        public int getRowCount()
        {
            return model.getSize();
        }

        public int getColumnCount()
        {
            return columns.length;
        }

        @Override
        public String getColumnName(int column)
        {
            return columns[column];
        }

        public Object getValueAt(int rowIndex, int columnIndex)
        {
            if (columnIndex == 0)
            {
                return rowIndex + 1;
            }
            CharacterLevelFacade level = model.getElementAt(rowIndex);
            switch (columnIndex)
            {
                case 1:
                    return level.getSelectedClass();
                case 2:
                    return level.getSpentSkillPoints();
                case 3:
                    return level.getGainedSkillPoints();
                default:
                    throw new IndexOutOfBoundsException();
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex)
        {
            switch (columnIndex)
            {
                case 1:
                    return ClassFacade.class;
                case 0:
                case 2:
                case 3:
                    return Integer.class;
                default:
                    return Object.class;
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex)
        {
            if (columnIndex == 3)
            {
                return true;
            }
            return false;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex)
        {
            CharacterLevelFacade level = model.getElementAt(rowIndex);
            level.setGainedSkillPoints((Integer) aValue);
        }

        public void intervalAdded(ListDataEvent e)
        {
            fireTableRowsInserted(e.getIndex0(), e.getIndex1());
        }

        public void intervalRemoved(ListDataEvent e)
        {
            fireTableRowsDeleted(e.getIndex0(), e.getIndex1());
        }

        public void contentsChanged(ListDataEvent e)
        {
            fireTableRowsUpdated(e.getIndex0(), e.getIndex1());
        }

    }

    private static class SkillRankSpinnerModel extends AbstractSpinnerModel
    {

        private final CharacterFacade character;
        private CharacterLevelFacade level;
        private SkillFacade skill;

        public SkillRankSpinnerModel(CharacterFacade character)
        {
            this.character = character;
        }

        public Float getValue()
        {
            return character.getSkillRanks(skill);
        }

        public void setSkill(SkillFacade skill)
        {
            this.skill = skill;
        }

        public void setLevel(CharacterLevelFacade level)
        {
            this.level = level;
        }

        public void setValue(Object value)
        {
            if (value instanceof Float)
            {
                setValue((Float) value);
            }
        }

        public void setValue(Float value)
        {
            SkillCost cost = level.getSkillCost(skill);
            if (value < 0)
            {
                value = Float.valueOf(0);
            }
            else if (!PCGenUIManager.isHouseRuleSelected(HouseRule.SKILLMAX))
            {
                float max = level.getMaxRanks(cost);
                if (value > max)
                {
                    value = max;
                }
            }
            int points = (int) ((value - getValue()) * level.getRankCost(cost));

            if (level.investSkillPoints(skill, points))
            {
                fireStateChanged();
            }
        }

        public Float getNextValue()
        {
            float value = getValue();
            SkillCost cost = level.getSkillCost(skill);
            if (!PCGenUIManager.isHouseRuleSelected(HouseRule.SKILLMAX) &&
                    value == level.getMaxRanks(cost))
            {
                return null;
            }
            return value + 1f / level.getRankCost(cost);
        }

        public Float getPreviousValue()
        {
            float value = getValue();
            SkillCost cost = level.getSkillCost(skill);
            if (value == 0)
            {
                return null;
            }
            return value - 1f / level.getRankCost(cost);
        }

    }

    private static class SkillRankSpinnerRenderer extends DefaultTableCellRenderer
    {

        private JSpinner spinner = new JSpinner();

        @Override
        public Component getTableCellRendererComponent(JTable table,
                                                        Object value,
                                                        boolean isSelected,
                                                        boolean hasFocus,
                                                        int row,
                                                        int column)
        {
            super.getTableCellRendererComponent(table, value, isSelected,
                                                hasFocus, row, column);
            if (value == null)
            {
                return this;
            }
            spinner.setBackground(getBackground());
            spinner.setForeground(getForeground());
            spinner.setValue(value);
            return spinner;
        }

    }
}
