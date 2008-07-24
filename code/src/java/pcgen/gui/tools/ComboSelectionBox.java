/*
 * ComboSelectionBox.java
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
 * Created on Jul 22, 2008, 4:58:45 PM
 */
package pcgen.gui.tools;

import java.awt.BorderLayout;
import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import pcgen.gui.filter.FilterableTreeViewModel;
import pcgen.gui.util.treeview.TreeViewModel;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class ComboSelectionBox extends JPanel implements ItemSelectable
{

    private ComboSelectionBoxModel model;
    private JComboBox comboBox;
    private JButton button;

    public ComboSelectionBox()
    {
        super(new BorderLayout());
        initComponents();
    }

    private void initComponents()
    {
        model = new ComboSelectionBoxModel();
        comboBox = new JComboBox(model);

        setBorder(comboBox.getBorder());
        comboBox.setBorder(BorderFactory.createEmptyBorder());

        button = new JButton(new ButtonAction());
        button.setMargin(new java.awt.Insets(0, 0, 0, 0));

        add(comboBox, BorderLayout.CENTER);
        add(button, BorderLayout.LINE_END);
    }

    public void setTreeViewModel(FilterableTreeViewModel<?> viewmodel)
    {
        model.setTreeViewModel(viewmodel);
    }

    public Object[] getSelectedObjects()
    {
        return comboBox.getSelectedObjects();
    }

    public void addItemListener(ItemListener l)
    {
        comboBox.addItemListener(l);
    }

    public void removeItemListener(ItemListener l)
    {
        comboBox.removeItemListener(l);
    }

    private static class ComboSelectionBoxModel extends DefaultComboBoxModel
            implements ListDataListener
    {

        private ListModel model;

        public void setTreeViewModel(TreeViewModel<?> viewmodel)
        {
            int comboSize = super.getSize();
            if (model != null)
            {
                model.removeListDataListener(this);
                int oldsize = getSize();
                model = null;
                fireIntervalRemoved(this, comboSize, oldsize - 1);
            }
            model = viewmodel.getDataModel();
            model.addListDataListener(this);
            fireIntervalAdded(this, comboSize, getSize() - 1);
        }

        @Override
        public int getSize()
        {
            int comboSize = super.getSize();
            if (model == null)
            {
                return comboSize;
            }
            return comboSize + model.getSize();
        }

        @Override
        public Object getElementAt(int index)
        {
            int comboSize = super.getSize();
            if (index < comboSize)
            {
                return super.getElementAt(index);
            }
            return model.getElementAt(index - comboSize);
        }

        public void intervalAdded(ListDataEvent e)
        {
            int comboSize = super.getSize();
            fireIntervalAdded(this, comboSize + e.getIndex0(), comboSize +
                              e.getIndex1());
        }

        public void intervalRemoved(ListDataEvent e)
        {
            int comboSize = super.getSize();
            fireIntervalRemoved(this, comboSize + e.getIndex0(), comboSize +
                                e.getIndex1());
        }

        public void contentsChanged(ListDataEvent e)
        {
            int comboSize = super.getSize();
            fireContentsChanged(this, comboSize + e.getIndex0(), comboSize +
                                e.getIndex1());
        }

    }

    private class ButtonAction extends AbstractAction
    {

        public ButtonAction()
        {
            super("...");
        }

        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

}
