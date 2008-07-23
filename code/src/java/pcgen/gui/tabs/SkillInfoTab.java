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
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.gui.PCGenUIManager;
import pcgen.gui.facade.CharacterFacade;
import pcgen.gui.facade.CharacterLevelFacade;
import pcgen.gui.facade.ClassFacade;
import pcgen.gui.facade.SkillFacade;
import pcgen.gui.filter.FilterableTreeViewModel;
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
//private final FilterableTreeViewPane
    private final JTable skillcostTable;
    private final JTable skillpointTable;

    public SkillInfoTab()
    {
        this.skillcostTable = new JTable();
        this.skillpointTable = new JTable();
        initComponents();
    }

    private void initComponents()
    {
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

    public Hashtable<Object, Object> createState(CharacterFacade character)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void storeState(Hashtable<Object, Object> state)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void restoreState(Hashtable<?, ?> state)
    {
        throw new UnsupportedOperationException("Not supported yet.");
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

        private final CharacterFacade character;

        public SkillCostTableModel(CharacterFacade character)
        {
            this.character = character;
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
                    return Double.class;
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
            throw new UnsupportedOperationException("Not supported yet.");
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

    private static class TableCellSpinnerEditor extends AbstractCellEditor
            implements TableCellEditor
    {

        private JSpinner spinner;

        public TableCellSpinnerEditor()
        {
            SpinnerNumberModel model = new SpinnerNumberModel();
            model.setMinimum(0);
            this.spinner = new JSpinner(model);
        }

        public Object getCellEditorValue()
        {
            return spinner.getValue();
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
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

    private static class TableCellSpinnerRenderer extends JSpinner implements TableCellRenderer
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
