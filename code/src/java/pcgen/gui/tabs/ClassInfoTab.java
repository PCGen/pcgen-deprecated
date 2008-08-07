/*
 * ClassInfoTab.java
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
 * Created on Jun 27, 2008, 1:36:26 PM
 */
package pcgen.gui.tabs;

import pcgen.gui.tools.ChooserPane;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.TransferHandler;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import pcgen.gui.facade.CharacterFacade;
import pcgen.gui.facade.CharacterLevelFacade;
import pcgen.gui.facade.ClassFacade;
import pcgen.gui.filter.FilterableTreeViewModel;
import pcgen.gui.filter.FilteredTreeViewPanel;
import pcgen.gui.util.GenericListModel;
import pcgen.gui.util.treeview.DataView;
import pcgen.gui.util.treeview.DataViewColumn;
import pcgen.gui.util.treeview.DefaultDataViewColumn;
import pcgen.gui.util.treeview.TreeView;
import pcgen.gui.util.treeview.TreeViewPath;
import pcgen.util.PropertyFactory;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class ClassInfoTab extends ChooserPane implements CharacterStateEditable
{

    private final FilteredTreeViewPanel treeviewPanel;
    private final JTable classTable;
    private final JButton addButton;
    private final JButton removeButton;
    private ClassFacade selectedClass;
    private int spinnerValue;

    public ClassInfoTab()
    {
        this.treeviewPanel = new FilteredTreeViewPanel();
        this.classTable = new JTable();
        this.addButton = new JButton();
        this.removeButton = new JButton();
        initComponents();
    }

    private void initComponents()
    {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        Dimension buttonSize = new Dimension(100, 23);

        addButton.setDefaultCapable(false);
        addButton.setMinimumSize(buttonSize);
        addButton.setPreferredSize(buttonSize);

        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.weightx = 1.0;
        constraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panel.add(addButton, constraints);

        Dimension spinnerSize = new Dimension(50, 18);
        final JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, null,
                                                                      1));
        spinner.addChangeListener(
                new ChangeListener()
                {

                    public void stateChanged(ChangeEvent e)
                    {
                        spinnerValue = (Integer) spinner.getValue();
                    }

                });
        spinner.setMinimumSize(spinnerSize);
        spinner.setPreferredSize(spinnerSize);

        constraints.anchor = GridBagConstraints.CENTER;
        constraints.weightx = 0.0;
        panel.add(spinner, constraints);

        removeButton.setDefaultCapable(false);
        removeButton.setMinimumSize(buttonSize);
        removeButton.setPreferredSize(buttonSize);

        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 1.0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(removeButton, constraints);

        ListSelectionModel selectionModel = classTable.getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionModel.addListSelectionListener(
                new ListSelectionListener()
                {

                    public void valueChanged(ListSelectionEvent e)
                    {
                        if (!e.getValueIsAdjusting())
                        {

                        }
                    }

                });
        JScrollPane tablePane = new JScrollPane(classTable);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.weighty = 1.0;
        panel.add(tablePane, constraints);


        TransferHandler handler = new ClassTransferHandler();
        classTable.setDragEnabled(true);
        classTable.setTransferHandler(handler);

        treeviewPanel.setDragEnabled(true);
        treeviewPanel.setTransferHandler(handler);

        setPrimaryChooserComponent(treeviewPanel);
        setSecondaryChooserComponent(panel);
    }

    private void setSelectedClass(ClassFacade c)
    {

    }

    private ClassFacade[] createFacadeArray(ClassFacade c)
    {
        ClassFacade[] classes = new ClassFacade[spinnerValue];
        for (int x = 0; x < spinnerValue; x++)
        {
            classes[x] = c;
        }
        return classes;
    }

    public Hashtable<Object, Object> createState(CharacterFacade character)
    {
        ClassTableModel classModel = new ClassTableModel(character);

        Hashtable<Object, Object> state = treeviewPanel.createState(character,
                                                                    null);
        state.put("ClassTableModel", classModel);

        return state;
    }

    public void storeState(Hashtable<Object, Object> state)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void restoreState(Hashtable<?, ?> state)
    {
        classTable.setModel((ClassTableModel) state.get("ClassTableModel"));
    }

    private class AddClassAction extends AbstractAction
    {

        private CharacterFacade character;

        public AddClassAction(CharacterFacade character)
        {
            super(PropertyFactory.getString("in_add"));
            this.character = character;
        }

        public void actionPerformed(ActionEvent e)
        {
            character.addCharacterLevels(createFacadeArray(selectedClass));
        }

    }

    private class RemoveClassAction extends AbstractAction
    {

        private CharacterFacade character;

        public RemoveClassAction(CharacterFacade character)
        {
            super(PropertyFactory.getString("in_remove"));
            this.character = character;
        }

        public void actionPerformed(ActionEvent e)
        {
            character.removeCharacterLevels(spinnerValue);
        }

    }

    private final class ClassTransferHandler extends TransferHandler
    {

        private final DataFlavor classFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType +
                                                                ";class=" +
                                                                ClassFacade.class.getName(),
                                                                null);

        @Override
        public int getSourceActions(JComponent c)
        {
            if (c == classTable)
            {
                return NONE;
            }
            else
            {
                return COPY;
            }
        }

        @Override
        protected Transferable createTransferable(JComponent c)
        {
            List<Object> data = treeviewPanel.getSelectedData();
            if (data.isEmpty())
            {
                return null;
            }
            Object obj = data.get(0);
            if (!(obj instanceof ClassFacade))
            {
                return null;
            }

            final ClassFacade selectedClass = (ClassFacade) obj;

            return new Transferable()
            {

                public DataFlavor[] getTransferDataFlavors()
                {
                    return new DataFlavor[]{classFlavor};
                }

                public boolean isDataFlavorSupported(DataFlavor flavor)
                {
                    return classFlavor == flavor;
                }

                public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
                {
                    if (!isDataFlavorSupported(flavor))
                    {
                        throw new UnsupportedFlavorException(flavor);
                    }
                    return selectedClass;
                }

            };
        }

        @Override
        public boolean canImport(JComponent comp, DataFlavor[] transferFlavors)
        {
            return transferFlavors[0] == classFlavor;
        }

        @Override
        public boolean importData(JComponent comp, Transferable t)
        {
            if (comp == classTable)
            {
                try
                {
                    ClassFacade c = (ClassFacade) t.getTransferData(classFlavor);
                    ClassTableModel model = (ClassTableModel) classTable.getModel();
                    ClassFacade[] classes = new ClassFacade[spinnerValue];

                    for (int x = 0; x < spinnerValue; x++)
                    {
                        classes[x] = c;
                    }

                    //selectionModel.addRows(classes);
                    return true;
                }
                catch (UnsupportedFlavorException ex)
                {
                    Logger.getLogger(ClassInfoTab.class.getName()).log(Level.SEVERE,
                                                                       null,
                                                                       ex);
                }
                catch (IOException ex)
                {
                    Logger.getLogger(ClassInfoTab.class.getName()).log(Level.SEVERE,
                                                                       null,
                                                                       ex);
                }
                return false;
            }
            return true;
        }

    }

    private static class ClassTreeViewModel implements FilterableTreeViewModel<ClassFacade>,
                                                         DataView<ClassFacade>
    {

        private static final List<DefaultDataViewColumn> columns =
                Arrays.asList(new DefaultDataViewColumn("HD", String.class),
                              new DefaultDataViewColumn("Type", String[].class),
                              new DefaultDataViewColumn("Base Stat",
                                                        String.class),
                              new DefaultDataViewColumn("Spell Type",
                                                        String.class));

        public Class<ClassFacade> getFilterClass()
        {
            return ClassFacade.class;
        }

        public List<? extends TreeView<ClassFacade>> getTreeViews()
        {
            return Arrays.asList(ClassTreeView.values());
        }

        public int getDefaultTreeViewIndex()
        {
            return 0;
        }

        public DataView<ClassFacade> getDataView()
        {
            return this;
        }

        public GenericListModel<ClassFacade> getDataModel()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public List<?> getData(ClassFacade obj)
        {
            return Arrays.asList(obj.getHD(), obj.getTypes(), obj.getBaseStat(),
                                 obj.getSpellType());
        }

        public List<? extends DataViewColumn> getDataColumns()
        {
            return columns;
        }

        private static enum ClassTreeView implements TreeView<ClassFacade>
        {

            NAME("Name"),
            TYPE_NAME("Type/Name"),
            SOURCE_NAME("Source/Name");
            private String name;

            private ClassTreeView(String name)
            {
                this.name = name;
            }

            public String getViewName()
            {
                return name;
            }

            public List<TreeViewPath<ClassFacade>> getPaths(ClassFacade pobj)
            {
                switch (this)
                {
                    case TYPE_NAME:
                        String[] types = pobj.getTypes();
                        if (types != null && types.length > 0)
                        {
                            List<TreeViewPath<ClassFacade>> paths = new ArrayList<TreeViewPath<ClassFacade>>(types.length);
                            for (String type : types)
                            {
                                paths.add(new TreeViewPath<ClassFacade>(pobj,
                                                                        type));
                            }
                            return paths;
                        }
                    case NAME:
                        return Collections.singletonList(new TreeViewPath<ClassFacade>(pobj));
                    case SOURCE_NAME:
                        return Collections.singletonList(new TreeViewPath<ClassFacade>(pobj,
                                                                                       pobj.getSource()));
                    default:
                        throw new InternalError();
                }

            }

        }
    }

    private static class ClassTableModel extends AbstractTableModel
            implements ListDataListener
    {

        private static final String[] columns = {"Level",
                                                    "Class",
                                                    "Source"
        };
        private GenericListModel<CharacterLevelFacade> model;

        public ClassTableModel(CharacterFacade character)
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
            return 3;
        }

        @Override
        public String getColumnName(int column)
        {
            return columns[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex)
        {
            switch (columnIndex)
            {
                case 0:
                    return Integer.class;
                case 1:
                    return Object.class;
                case 2:
                    return String.class;
                }
            return null;
        }

        public Object getValueAt(int rowIndex, int columnIndex)
        {
            if (columnIndex == 0)
            {
                return rowIndex + 1;
            }
            ClassFacade c = model.getElementAt(rowIndex).getSelectedClass();
            switch (columnIndex)
            {
                case 1:
                    return c;
                case 2:
                    return c.getSource();
                default:
                    return null;
                }
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
    }
