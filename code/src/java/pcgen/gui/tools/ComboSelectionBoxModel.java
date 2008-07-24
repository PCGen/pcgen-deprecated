/*
 * ComboSelectionBoxModel.java
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
 * Created on Jul 24, 2008, 2:20:11 PM
 */
package pcgen.gui.tools;

import pcgen.gui.facade.CharacterFacade;
import pcgen.gui.filter.FilterableTreeViewModel;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public interface ComboSelectionBoxModel
{

    /**
     * Note: The returned data should not be the same as the data used in the
     * <code>FilterableTreeViewModel</code> returned by <code>getTreeViewModel()</code>
     * . Data from the <code>FilterableTreeViewModel</code> model is automatically added to the <code>JComboBox</code>'s model.
     * @return an array of objects
     */
    public Object[] getComboBoxData();

    public CharacterFacade getCharacter();

    public FilterableTreeViewModel<?> getTreeViewModel();

}
