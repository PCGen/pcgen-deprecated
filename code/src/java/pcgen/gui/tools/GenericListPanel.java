/*
 * GenericListPanel.java
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
 * Created on Aug 30, 2008, 8:19:45 PM
 */
package pcgen.gui.tools;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import pcgen.gui.util.DefaultGenericListModel;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class GenericListPanel extends JPanel
{

    private final GridBagConstraints gridBagConstraints;
    private final JLabel titleLabel;
    private final JList list;

    public GenericListPanel()
    {
        this(null);
    }

    public GenericListPanel(String title)
    {
        super(new GridBagLayout());
        this.gridBagConstraints = new GridBagConstraints();
        this.titleLabel = new JLabel(title);
        this.list = new JList();
        initComponents();
    }

    private void initComponents()
    {
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        add(titleLabel, gridBagConstraints);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridheight = GridBagConstraints.REMAINDER;
        gridBagConstraints2.fill = GridBagConstraints.BOTH;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.weighty = 1.0;
        add(new JScrollPane(list), gridBagConstraints2);

        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
    }

    public void add(Action action)
    {
        add(new JButton(action), gridBagConstraints);
    }

    public void addSeparator()
    {
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.insets = new Insets(4, 0, 4, 0);
        add(new JSeparator(), gridBagConstraints2);
    }

    public void addListSelectionListener(ListSelectionListener listener)
    {
        list.addListSelectionListener(listener);
    }

    public Object getSelectedValue()
    {
        return list.getSelectedValue();
    }

    public int getSelectedIndex()
    {
        return list.getSelectedIndex();
    }

    public void setSelectedIndex(int index)
    {
        list.setSelectedIndex(index);
    }

    public void setCellRenderer(ListCellRenderer renderer)
    {
        list.setCellRenderer(renderer);
    }

    public void setSelectionMode(int selectionMode)
    {
        list.setSelectionMode(selectionMode);
    }

    public void setModel(DefaultGenericListModel<?> model)
    {
        list.setModel(model);
    }

    public void setTitle(String title)
    {
        titleLabel.setText(title);
    }

    public void setSelectedValue(Object value, boolean shouldScroll)
    {
        list.setSelectedValue(value, shouldScroll);
    }

}
