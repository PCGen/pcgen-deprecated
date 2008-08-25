/*
 * FilteredTreeViewSelectionPanel.java
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
 * Created on Jul 23, 2008, 4:39:02 PM
 */
package pcgen.gui.tools;

import java.awt.ItemSelectable;
import java.awt.event.ItemListener;
import java.util.Collection;
import javax.swing.ListSelectionModel;
import pcgen.gui.filter.FilteredTreeViewPanel;
import pcgen.gui.util.JTreeViewPane;
import pcgen.gui.util.JTreeViewSelectionPane;
import pcgen.gui.util.JTreeViewSelectionPane.SelectionType;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class FilteredTreeViewSelectionPanel extends FilteredTreeViewPanel
        implements ItemSelectable
{

    @Override
    public JTreeViewPane createDefaultTreeViewPane()
    {
        return new JTreeViewSelectionPane();
    }

    @Override
    protected JTreeViewSelectionPane getTreeViewPane()
    {
        return (JTreeViewSelectionPane) super.getTreeViewPane();
    }

    public Object getSelectedItem()
    {
        Object[] objs = getSelectedObjects();
        if (objs.length > 0)
        {
            return objs[0];
        }
        return null;
    }

    public void setSelectedObjects(Collection<?> objects)
    {
        getTreeViewPane().setSelectedObjects(objects);
    }

    public void setSelectionType(SelectionType selectionType)
    {
        getTreeViewPane().setSelectionType(selectionType);
    }

    public void setEditable(boolean editable)
    {
        getTreeViewPane().setEditable(editable);
    }

    public Object[] getSelectedObjects()
    {
        return getTreeViewPane().getSelectedObjects();
    }

    public void addItemListener(ItemListener l)
    {
        getTreeViewPane().addItemListener(l);
    }

    public void removeItemListener(ItemListener l)
    {
        getTreeViewPane().removeItemListener(l);
    }

}
