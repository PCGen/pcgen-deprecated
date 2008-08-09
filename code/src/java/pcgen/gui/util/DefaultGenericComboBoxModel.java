/*
 * DefaultGenericComboBoxModel.java
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
 * Created on Aug 8, 2008, 2:27:27 PM
 */
package pcgen.gui.util;

import java.util.Collection;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class DefaultGenericComboBoxModel<E> extends DefaultGenericListModel<E>
        implements GenericComboBoxModel<E>
{

    private Object selectedObject;

    public DefaultGenericComboBoxModel()
    {
    }

    public DefaultGenericComboBoxModel(Collection<? extends E> c)
    {
        super(c);
    }

    // implements javax.swing.ComboBoxModel
    /**
     * Set the value of the selected item. The selected item may be null.
     * <p>
     * @param anObject The combo box value or null for no selection.
     */
    public void setSelectedItem(Object anObject)
    {
        if ((selectedObject != null && !selectedObject.equals(anObject)) ||
                selectedObject == null && anObject != null)
        {
            selectedObject = anObject;
            fireContentsChanged(this, null, false, -1, -1);
        }
    }

    // implements javax.swing.ComboBoxModel
    public Object getSelectedItem()
    {
        return selectedObject;
    }

}
