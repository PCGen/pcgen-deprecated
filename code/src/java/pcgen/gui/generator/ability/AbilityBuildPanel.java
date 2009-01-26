package pcgen.gui.generator.ability;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pcgen.gui.facade.AbilityFacade;
import pcgen.gui.filter.FilteredTreeViewPanel;
import pcgen.gui.tools.ResourceManager;
import pcgen.gui.tools.ResourceManager.Icons;
import pcgen.gui.util.table.ListTableModel;

class AbilityBuildPanel extends JPanel
{

    private final AbilityTableModel model;
    private final FilteredTreeViewPanel abilityPanel;
    private final JComboBox catagoryBox;
    private final JComboBox generatorBox;
    private final JCheckBox orderBox;
    private final JTable table;
    private final JButton addButton;
    private final JButton removeButton;
    private final JButton upButton;
    private final JButton downButton;

    public AbilityBuildPanel()
    {
        super(new GridBagLayout());
        this.abilityPanel = new FilteredTreeViewPanel();
        this.catagoryBox = new JComboBox();
        this.generatorBox = new JComboBox();
        this.orderBox = new JCheckBox();
        this.model = new AbilityTableModel();
        this.addButton = new JButton();
        this.removeButton = new JButton();
        this.upButton = new JButton();
        this.downButton = new JButton();
        this.table = new JTable(model)
        {

            @Override
            public boolean getScrollableTracksViewportHeight()
            {
                // fetch the table's parent
                Container viewport = getParent();

                // if the parent is not a viewport, calling this isn't useful
                if (!(viewport instanceof JViewport))
                {
                    return false;
                }

                // return true if the table's preferred height is smaller
                // than the viewport height, else false
                return getPreferredSize().height < viewport.getHeight();
            }

        };
        initComponents();
    }

    private void initComponents()
    {
        GridBagConstraints gridBagConstraints;
        GridBagConstraints gridBagConstraints2;
        {
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.weightx = 1.0;
        }
        gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.anchor = GridBagConstraints.EAST;
        gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
        add(new JLabel(ResourceManager.getText("abilityCatagory")),
            gridBagConstraints2);
        {
            catagoryBox.addItemListener(model);
        }
        add(catagoryBox, gridBagConstraints);
        add(new JLabel(ResourceManager.getText("useGen")), gridBagConstraints2);
        {
            generatorBox.addItemListener(model);
        }
        add(generatorBox, gridBagConstraints);

        JPanel panel = new JPanel(new GridBagLayout());
        {
            gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = GridBagConstraints.BOTH;
            gridBagConstraints2.gridheight = GridBagConstraints.REMAINDER;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.weightx = 1.0;
            gridBagConstraints2.weighty = 1.0;
            panel.add(new JScrollPane(table), gridBagConstraints2);
            gridBagConstraints.anchor = GridBagConstraints.NORTH;
            gridBagConstraints.weightx = 0.0;
            initButton(panel, orderBox, "ordered", null, model,
                       gridBagConstraints);
            initButton(panel, addButton, "add", null, model, gridBagConstraints);
            initButton(panel, removeButton, "remove", null, model,
                       gridBagConstraints);

            gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridwidth = GridBagConstraints.REMAINDER;
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.insets = new Insets(4, 0, 4, 0);
            gridBagConstraints2.ipady = 1;
            add(new JSeparator(), gridBagConstraints2);

            initButton(panel, upButton, "up", Icons.Up16, model,
                       gridBagConstraints);
            initButton(panel, downButton, "down", Icons.Down16, model,
                       gridBagConstraints);
        }
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                              true, panel, abilityPanel);
        splitPane.setDividerSize(7);
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(splitPane, gridBagConstraints);
    }

    private void initButton(JPanel panel, AbstractButton button, String prop,
                             Icons icon, ActionListener listener,
                             GridBagConstraints gridBagConstraints)
    {
        if (icon == null)
        {
            button.setText(ResourceManager.getText(prop));
        }
        else
        {
            button.setIcon(ResourceManager.getImageIcon(icon));
        }
        button.setActionCommand(prop);
        button.setEnabled(false);
        button.addActionListener(listener);
        panel.add(button, gridBagConstraints);
    }

//    private void setGenerator(OrderedGenerator<AbilityFacade> generator)
//    {
//        boolean ordered = !generator.isRandomOrder();
//        orderBox.setSelected(ordered);
//        model.removeAllElements();
//        model.addAll(generator.getAll());
//    }

    private class AbilityTableModel extends ListTableModel<AbilityFacade>
            implements ActionListener, ItemListener, ListSelectionListener
    {

        public AbilityTableModel()
        {
            super(ResourceManager.getText("ability"));
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex)
        {
            return false;
        }

        @Override
        public String getColumnName(int columnIndex)
        {
            if (orderBox.isSelected() && columnIndex == 0)
            {
                return ResourceManager.getText("order");
            }
            return super.getColumnName(columnIndex);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex)
        {
            if (orderBox.isSelected() && columnIndex == 0)
            {
                return Integer.class;
            }
            return Object.class;
        }

        @Override
        public int getColumnCount()
        {
            return orderBox.isSelected() ? 2 : 1;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex)
        {
            if (orderBox.isSelected() && columnIndex == 0)
            {
                return rowIndex + 1;
            }
            return super.getValueAt(rowIndex, columnIndex);
        }

        public void actionPerformed(ActionEvent e)
        {
            String command = e.getActionCommand();
            if (command.equals("ordered"))
            {

            }
            else if (command.equals("add"))
            {
                List<Object> data = abilityPanel.getSelectedData();
                if (!data.isEmpty())
                {
                    add((AbilityFacade) data.get(0));
                }
            }
            else
            {
                int row = table.getSelectedRow();
                if (command.equals("remove"))
                {
                    remove(row);
                }
                else
                {
                    int newrow = row + (command.equals("up") ? -1 : 1);
                    Collections.swap(this, row, newrow);
                    table.setRowSelectionInterval(newrow, newrow);
                }
            }
        }

        public void valueChanged(ListSelectionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @SuppressWarnings("unchecked")
        public void itemStateChanged(ItemEvent e)
        {
            if (e.getStateChange() != ItemEvent.SELECTED)
            {
                return;
            }
            ItemSelectable source = e.getItemSelectable();

            if (source == catagoryBox)
            {

            }
            else if (source == generatorBox)
            {
//                setGenerator((OrderedGenerator<AbilityFacade>) e.getItem());
            }
        }

    }
}
