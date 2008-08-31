/*
 * FilterSelectionDialog.java
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
 * Created on Aug 30, 2008, 7:50:15 PM
 */
package pcgen.gui.filter;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pcgen.gui.tools.AbstractSelectionDialog;
import pcgen.gui.tools.FlippingSplitPane;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class FilterSelectionDialog extends AbstractSelectionDialog
{

    private JTextArea descriptionArea;
    private JTextArea editorArea;

    public FilterSelectionDialog()
    {
        initComponents();
    }

    private void initComponents()
    {
        SelectionHandler handler = new SelectionHandler();
        availableList.addListSelectionListener(handler);
        selectedList.addListSelectionListener(handler);
        availableList.setCellRenderer(new FilterListCellRenderer());
        selectedList.setCellRenderer(new FilterListCellRenderer());
    }

    @Override
    protected Component getLeftComponent()
    {
        FlippingSplitPane splitPane = new FlippingSplitPane(JSplitPane.VERTICAL_SPLIT);
        JPanel panel;
        {
            panel = new JPanel();
            descriptionArea = createTextArea("Description", panel);
        }
        splitPane.setTopComponent(panel);
        {
            panel = new JPanel();
            editorArea = createTextArea("Editor", panel);
        }
        splitPane.setBottomComponent(panel);
        return splitPane;
    }

    private JTextArea createTextArea(String title, JPanel panel)
    {
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panel.add(new JLabel(title), gridBagConstraints);

        JTextArea area = new JTextArea();
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        panel.add(area, gridBagConstraints);
        return area;
    }

    @Override
    protected String getAvailableListTitle()
    {
        return "Available Filters";
    }

    @Override
    protected String getSelectedListTitle()
    {
        return "Selected Filters";
    }

    @Override
    protected Object createNewItem()
    {
        return new DefaultMutableFilter(JOptionPane.showInputDialog(this,
                                                                    "Enter name of new Filter"));
    }

    @Override
    protected void doSave()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private class SelectionHandler implements ListSelectionListener
    {

        private MutableFilter filter;

        public void valueChanged(ListSelectionEvent e)
        {
            JList list = (JList) e.getSource();
            DisplayableFilter<?> value = (DisplayableFilter<?>) list.getSelectedValue();
            if (value != null && value != filter)
            {
                if (filter != null)
                {
                    filter.setDescription(descriptionArea.getText());
                    filter.setCode(editorArea.getText());
                }

                descriptionArea.setText(value.getDescription());
                editorArea.setText(value.getCode());

                boolean mutable = value instanceof MutableFilter;
                deleteAction.setEnabled(mutable);
                descriptionArea.setEditable(mutable);
                editorArea.setEditable(mutable);
                filter = mutable ? (MutableFilter) value : null;
            }
        }

    }

    private static class FilterListCellRenderer extends DefaultListCellRenderer
    {

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                                                       int index,
                                                       boolean isSelected,
                                                       boolean cellHasFocus)
        {
            Component comp = super.getListCellRendererComponent(list, value,
                                                                index,
                                                                isSelected,
                                                                cellHasFocus);
            if (!isSelected && value instanceof MutableFilter)
            {
                comp.setForeground(Color.BLUE);
            }
            return comp;
        }

    }
}
