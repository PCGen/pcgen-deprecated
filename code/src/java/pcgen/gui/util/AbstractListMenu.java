/*
 * AbstractListMenu.java
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
 * Created on Aug 18, 2008, 1:56:12 PM
 */
package pcgen.gui.util;

import java.awt.event.ItemListener;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public abstract class AbstractListMenu<E> extends JMenu implements ListDataListener
{

    private GenericListModel<E> listModel;

    public AbstractListMenu(Action action)
    {
        this(action, null);
    }

    public AbstractListMenu(Action action, GenericListModel<E> listModel)
    {
        super(action);
        setListModel(listModel);
    }

    public void setListModel(GenericListModel<E> listModel)
    {
        GenericListModel<E> oldModel = this.listModel;
        if (oldModel != null)
        {
            oldModel.removeListDataListener(this);
            for (int x = 0; x < oldModel.getSize(); x++)
            {
                remove(0);
            }
        }
        this.listModel = listModel;
        if (listModel != null)
        {
            for (int x = 0; x < listModel.getSize(); x++)
            {
                add(createMenuItem(listModel.getElementAt(x)));
            }
            listModel.addListDataListener(this);
        }
        checkEnabled();
    }

    protected abstract JMenuItem createMenuItem(E item);

    protected void checkEnabled()
    {
        setEnabled(getComponentCount() != 0);
    }

    public void intervalAdded(ListDataEvent e)
    {
        for (int x = e.getIndex0(); x <= e.getIndex1(); x++)
        {
            add(createMenuItem(listModel.getElementAt(x)), x);
        }
        checkEnabled();
    }

    public void intervalRemoved(ListDataEvent e)
    {
        for (int x = e.getIndex0(); x <= e.getIndex1(); x++)
        {
            remove(e.getIndex0());
        }
        checkEnabled();
    }

    public void contentsChanged(ListDataEvent e)
    {
        intervalRemoved(e);
        intervalAdded(e);
    }

    protected static class CheckBoxMenuItem extends JCheckBoxMenuItem
    {

        private final Object item;

        public CheckBoxMenuItem(Object item, boolean selected,
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
