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
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.ItemSelectable;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import pcgen.gui.util.treeview.TreeViewModel;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class ComboSelectionBox extends JPanel implements ItemSelectable
{

    private static final long serialVersionUID = 4240590146578106112L;
    private FilteredSelectionDialog dialog = null;
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
        comboBox = new JComboBox();

        setBorder(comboBox.getBorder());
        comboBox.setBorder(BorderFactory.createEmptyBorder());

        button = new JButton(new ButtonAction());
        button.setEnabled(false);
        button.setMargin(new java.awt.Insets(0, 0, 0, 0));

        add(comboBox, BorderLayout.CENTER);
        add(button, BorderLayout.LINE_END);
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        comboBox.setEnabled(enabled);
        button.setEnabled(enabled);
    }

    public void setComboSelectionBoxModel(ComboSelectionBoxModel model)
    {
        this.model = model;
        ComboBoxModel boxmodel = new ComboBoxModel(model.getComboBoxData(),
                                                   model.getTreeViewModel());
        comboBox.setModel(boxmodel);
        button.setEnabled(true);
    }

    public Object getSelectedItem()
    {
        return comboBox.getSelectedItem();
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

    private class ButtonAction extends AbstractAction
    {

        public ButtonAction()
        {
            super("...");
        }

        public void actionPerformed(ActionEvent e)
        {
            if (dialog == null)
            {
                Window window = SwingUtilities.getWindowAncestor(ComboSelectionBox.this);
                if (window instanceof Frame)
                {
                    dialog = new FilteredSelectionDialog((Frame) window);
                }
                else
                {
                    dialog = new FilteredSelectionDialog((Dialog) window);
                }
            }
            dialog.restoreState(dialog.createState(model.getCharacter(),
                                                   model.getTreeViewModel()));
            SwingUtilities.invokeLater(
                    new Runnable()
                    {

                        public void run()
                        {
                            dialog.setVisible(true);
                            if (dialog.getReturnStatus() ==
                                    FilteredSelectionDialog.RET_OK)
                            {
                                comboBox.setSelectedItem(dialog.getReturnItem());
                            }
                        }

                    });
        }

    }

    private static class ComboBoxModel extends DefaultComboBoxModel
            implements ListDataListener
    {

        private ListModel model;

        public ComboBoxModel(Object[] items, TreeViewModel<?> viewmodel)
        {
            super(items);
            model = viewmodel.getDataModel();
            model.addListDataListener(this);
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
}
