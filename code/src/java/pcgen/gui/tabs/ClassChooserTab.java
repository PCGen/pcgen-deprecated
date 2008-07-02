/*
 * ClassChooserTab.java
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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import pcgen.gui.UIContext;
import pcgen.gui.facade.CharacterFacade;
import pcgen.gui.facade.ClassFacade;
import pcgen.gui.tools.FilteredTreeViewDisplay;
import pcgen.gui.util.JTreeViewPane;
import pcgen.util.PropertyFactory;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class ClassChooserTab extends AbstractChooserTab
{

    private final FilteredTreeViewDisplay treeviewDisplay;
    private final JTable classTable;
    private int spinnerValue;

    public ClassChooserTab(UIContext context)
    {
        super(context);
        this.treeviewDisplay = new FilteredTreeViewDisplay(context);
        this.classTable = new JTable();
        initComponents();
    }

    private void initComponents()
    {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        JButton button;
        Dimension buttonSize = new Dimension(100, 23);

        button = new JButton(new AddClassAction());
        button.setDefaultCapable(false);
        button.setMinimumSize(buttonSize);
        button.setPreferredSize(buttonSize);

        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.weightx = 1.0;
        constraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panel.add(button, constraints);

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

        button = new JButton(new RemoveClassAction());
        button.setDefaultCapable(false);
        button.setMinimumSize(buttonSize);
        button.setPreferredSize(buttonSize);

        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 1.0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(button, constraints);

        ListSelectionModel model = classTable.getSelectionModel();
        model.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        model.addListSelectionListener(
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

        JTreeViewPane treeviewPane = treeviewDisplay.getTreeViewPane();
        treeviewPane.setDragEnabled(true);
        treeviewPane.setTransferHandler(handler);

        setPrimaryChooserComponent(treeviewDisplay);
        setSecondaryChooserComponent(panel);
    }

    private class AddClassAction extends AbstractAction
    {

        public AddClassAction()
        {
            super(PropertyFactory.getString("in_add"));
        }

        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class RemoveClassAction extends AbstractAction
    {

        public RemoveClassAction()
        {
            super(PropertyFactory.getString("in_remove"));
        }

        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
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
            List<Object> data = treeviewDisplay.getTreeViewPane().getSelectedData();
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

                    model.addRows(classes);
                    return true;
                }
                catch (UnsupportedFlavorException ex)
                {
                    Logger.getLogger(ClassChooserTab.class.getName()).log(Level.SEVERE,
                                                                          null,
                                                                          ex);
                }
                catch (IOException ex)
                {
                    Logger.getLogger(ClassChooserTab.class.getName()).log(Level.SEVERE,
                                                                          null,
                                                                          ex);
                }
                return false;
            }
            return true;
        }

    }

    private static final class ClassTableModel extends DefaultTableModel
    {

        private static final Object[] columns = {"Level",
                                                    "Class",
                                                    "Source"
        };
        private CharacterFacade character;

        public ClassTableModel(CharacterFacade character)
        {
            super(columns, 0);
            this.character = character;
            int characterLevel = character.getCharacterLevel();
            for (int x = 0; x < characterLevel; x++)
            {
                addRow(character.getSelectedClass(x + 1));
            }
        }

        public void addRows(ClassFacade[] classes)
        {
            character.addCharacterLevels(classes);
            for (ClassFacade c : classes)
            {
                addRow(c);
            }
        }

        public void addRow(ClassFacade c)
        {
            Vector<Object> row = new Vector<Object>();
            row.add(getRowCount() + 1);
            row.add(c);
            row.add(c.getSource());
            addRow(row);
        }

        public void removeRows(int[] rows)
        {
            for (int i = rows.length - 1; i >= 0; i--)
            {
                dataVector.remove(rows[i]);
            }
            fireTableRowsDeleted(rows[0], rows[rows.length - 1]);
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

    }

    public Hashtable<Object, Object> createState(CharacterFacade character)
    {
        Hashtable<Object, Object> state = new Hashtable<Object, Object>();
        state.put("ClassTableModel", new ClassTableModel(character));

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

}
