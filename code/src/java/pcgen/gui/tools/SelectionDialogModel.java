/*
 * SelectionDialogModel.java
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
 * Created on Sep 14, 2008, 4:26:30 PM
 */
package pcgen.gui.tools;

import java.awt.Component;
import java.util.Properties;
import pcgen.gui.util.GenericListModel;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public interface SelectionDialogModel<E>
{

    public static final String AVAILABLE_TEXT_PROP = "avail";
    public static final String SELECTION_TEXT_PROP = "sel";
    public static final String NEW_TOOLTIP_PROP = "new";
    public static final String COPY_TOOLTIP_PROP = "copy";
    public static final String DELETE_TOOLTIP_PROP = "delete";
    public static final String ADD_TOOLTIP_PROP = "add";
    public static final String REMOVE_TOOLTIP_PROP = "remove";

    public GenericListModel<E> getAvailableList();

    public GenericListModel<E> getSelectedList();

    public void setAvailableList(GenericListModel<E> list);

    public void setSelectedList(GenericListModel<E> list);

    public Component getItemPanel(SelectionDialog<E> selectionDialog,
                                   Component currentItemPanel,
                                   E selectedItem);

    public E createMutableItem(SelectionDialog<E> selectionDialog, E templateItem);

    public boolean isMutable(Object item);

    public Properties getDisplayProperties();

}
