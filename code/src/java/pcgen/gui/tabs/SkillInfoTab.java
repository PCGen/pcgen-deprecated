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

import java.awt.Component;
import java.util.Hashtable;
import java.util.List;
import javax.swing.AbstractCellEditor;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import pcgen.gui.facade.CharacterFacade;
import pcgen.gui.facade.CharacterLevelFacade;
import pcgen.gui.facade.ClassFacade;
import pcgen.gui.facade.SkillFacade;
import pcgen.gui.tools.FilterableTreeViewModel;
import pcgen.gui.util.DefaultGenericListModel;
import pcgen.gui.util.GenericListModel;
import pcgen.gui.util.treeview.DataView;
import pcgen.gui.util.treeview.DataViewColumn;
import pcgen.gui.util.treeview.TreeView;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class SkillInfoTab extends AbstractChooserTab implements CharacterInfoTab
{

    private final JTable skillpointsTable;

    public SkillInfoTab()
    {
        this.skillpointsTable = new JTable();
        initComponents();
    }

    private void initComponents()
    {

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

    private static final class SkillTreeViewModel implements FilterableTreeViewModel<SkillFacade>
    {

        private CharacterFacade character;

        public SkillTreeViewModel(CharacterFacade character)
        {
            this.character = character;
        }

        public void setClass(ClassFacade c)
        {

        }

        public Class<SkillFacade> getFilterClass()
        {
            return SkillFacade.class;
        }

        public List<? extends TreeView<SkillFacade>> getTreeViews()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int getDefaultTreeViewIndex()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public DataView<SkillFacade> getDataView()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public DefaultGenericListModel<SkillFacade> getDataModel()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        private class SkillDataView implements DataView<SkillFacade>
        {

            public List<?> getData(SkillFacade obj)
            {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public List<? extends DataViewColumn> getDataColumns()
            {
                throw new UnsupportedOperationException("Not supported yet.");
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
            this.model = character.getLevels();
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
            switch (rowIndex)
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
