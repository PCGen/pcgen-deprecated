/*
 * ComboListMenu.java
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
 * Created on Aug 21, 2008, 5:25:09 PM
 */
package pcgen.gui.util;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.ListDataEvent;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class ComboListMenu<E> extends AbstractListMenu<E> implements ItemListener
{

    private final ButtonGroup group = new ButtonGroup();
    private GenericComboBoxModel<E> comboModel;

    public ComboListMenu(Action action)
    {
        this(action, null);
    }

    public ComboListMenu(Action action, GenericComboBoxModel<E> listModel)
    {
        super(action, listModel);
    }

    @Override
    protected JMenuItem createMenuItem(E item)
    {
        return new RadioButtonMenuItem(item,
                                       item == comboModel.getSelectedItem(),
                                       this);
    }

    @Override
    public Component add(Component c, int index)
    {
        if (c instanceof JRadioButtonMenuItem)
        {
            group.add((JRadioButtonMenuItem) c);
        }
        return super.add(c, index);
    }

    @Override
    public void remove(int pos)
    {
        Component c = getComponent(pos);
        if (c instanceof JRadioButtonMenuItem)
        {
            group.remove((JRadioButtonMenuItem) c);
        }
        super.remove(pos);
    }

    @Override
    public void setListModel(GenericListModel<E> listModel)
    {
        if (listModel == null || listModel instanceof GenericComboBoxModel)
        {
            setComboModel((GenericComboBoxModel<E>) listModel);
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }

    public void setComboModel(GenericComboBoxModel<E> comboModel)
    {
        super.setListModel(comboModel);
        this.comboModel = comboModel;
    }

    @Override
    public void contentsChanged(ListDataEvent e)
    {
        if (e.getIndex0() < 0 && e.getIndex1() < 0)
        {
            Object item = comboModel.getSelectedItem();
            Enumeration<AbstractButton> elements = group.getElements();
            while (elements.hasMoreElements())
            {
                AbstractButton button = elements.nextElement();
                if (item == button.getSelectedObjects()[0])
                {
                    group.setSelected(button.getModel(), true);
                    break;
                }
            }
            return;
        }
        super.contentsChanged(e);
    }

    public void itemStateChanged(ItemEvent e)
    {
        if (e.getStateChange() == ItemEvent.SELECTED)
        {
            comboModel.setSelectedItem(e.getItem());
        }
    }

    private static class RadioButtonMenuItem extends JRadioButtonMenuItem
    {

        private final Object item;

        public RadioButtonMenuItem(Object item, boolean selected,
                                    ItemListener listener)
        {
            this.item = item;
            setSelected(selected);
            addItemListener(listener);
        }

        @Override
        public String getText()
        {
            return item.toString();
        }

        @Override
        public Object[] getSelectedObjects()
        {
            return new Object[]{item};
        }

    }
}
