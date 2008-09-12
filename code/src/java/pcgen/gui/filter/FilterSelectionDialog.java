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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import pcgen.gui.tools.ResourceManager;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class FilterSelectionDialog extends AbstractSelectionDialog<DisplayableFilter<?>>
{

    private FlippingSplitPane leftComponent;
    private JTextArea descriptionArea;
    private JTextArea editorArea;

    public FilterSelectionDialog()
    {
        super(ResourceManager.getText("availFilt"),
              ResourceManager.getText("selFilt"),
              ResourceManager.getToolTip("newFilt"),
              ResourceManager.getToolTip("copyFilt"),
              ResourceManager.getToolTip("deleteFilt"),
              ResourceManager.getToolTip("addFilt"),
              ResourceManager.getToolTip("removeFilt"));
    }

    protected void initComponents()
    {
        this.leftComponent = new FlippingSplitPane(JSplitPane.VERTICAL_SPLIT);

        SelectionHandler handler = new SelectionHandler();
        availableList.addListSelectionListener(handler);
        selectedList.addListSelectionListener(handler);
        JPanel panel;
        {
            panel = new JPanel();
            this.descriptionArea = createTextArea(ResourceManager.getText("desc"),
                                                  panel);
        }
        leftComponent.setTopComponent(panel);
        {
            panel = new JPanel();
            this.editorArea = createTextArea(ResourceManager.getText("editor"),
                                             panel);
        }
        leftComponent.setBottomComponent(panel);
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
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        panel.add(area, gridBagConstraints);
        return area;
    }

    @Override
    protected Component getLeftComponent()
    {
        return leftComponent;
    }

    @Override
    protected DisplayableFilter<?> createMutableItem(DisplayableFilter<?> item)
    {
        MutableFilter<?> filter = new DefaultMutableFilter(JOptionPane.showInputDialog(this,
                                                                                       ResourceManager.getText("createFilt")));
        if (item != null)
        {
            filter.setCode(item.getCode());
            filter.setDescription(item.getDescription());
        }
        return filter;
    }

    @Override
    protected boolean isMutable(Object item)
    {
        return item instanceof MutableFilter;
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

                boolean mutable = isMutable(value);
                descriptionArea.setEditable(mutable);
                editorArea.setEditable(mutable);
                filter = mutable ? (MutableFilter) value : null;
            }
        }

    }
}
