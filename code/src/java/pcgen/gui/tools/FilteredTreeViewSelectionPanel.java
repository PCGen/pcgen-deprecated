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
import javax.swing.ListSelectionModel;
import pcgen.gui.filter.FilteredTreeViewPanel;
import pcgen.gui.util.JTreeViewPane;
import pcgen.gui.util.JTreeViewSelectionPane;

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
        JTreeViewSelectionPane pane = new JTreeViewSelectionPane();
        pane.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return pane;
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

    public Object[] getSelectedObjects()
    {
        return getTreeViewPane().getSelectedObjects();
    }

    @Override
    protected JTreeViewSelectionPane getTreeViewPane()
    {
        return (JTreeViewSelectionPane) super.getTreeViewPane();
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
