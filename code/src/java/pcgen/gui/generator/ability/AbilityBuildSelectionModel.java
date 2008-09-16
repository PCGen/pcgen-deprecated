/*
 * AbilityBuildSelectionModel.java
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
 * Created on Sep 12, 2008, 1:39:59 PM
 */
package pcgen.gui.generator.ability;

import java.awt.Component;
import java.util.Properties;
import pcgen.gui.facade.AbilityFacade;
import pcgen.gui.facade.CharacterFacade;
import pcgen.gui.filter.FilterableTreeViewModel;
import pcgen.gui.tools.SelectionDialog;
import pcgen.gui.tools.SelectionDialogModel;
import pcgen.gui.util.GenericListModel;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class AbilityBuildSelectionModel implements SelectionDialogModel<AbilityBuild>
{

    private static final Properties props = new Properties();

    static
    {
        props.setProperty(SelectionDialogModel.AVAILABLE_TEXT_PROP,
                          "availAbilBuild");
        props.setProperty(SelectionDialogModel.SELECTION_TEXT_PROP,
                          "selAbilBuild");
        props.setProperty(SelectionDialogModel.NEW_TOOLTIP_PROP,
                          "newAbilBuild");
        props.setProperty(SelectionDialogModel.COPY_TOOLTIP_PROP,
                          "copyAbilBuild");
        props.setProperty(SelectionDialogModel.DELETE_TOOLTIP_PROP,
                          "deleteAbilBuild");
        props.setProperty(SelectionDialogModel.ADD_TOOLTIP_PROP,
                          "addAbilBuild");
        props.setProperty(SelectionDialogModel.REMOVE_TOOLTIP_PROP,
                          "removeAbilBuild");
    }

    public CharacterFacade getCharacter()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public FilterableTreeViewModel<AbilityFacade> getTreeViewModel()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public GenericListModel<AbilityBuild> getAvailableList()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public GenericListModel<AbilityBuild> getSelectedList()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setAvailableList(GenericListModel<AbilityBuild> list)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setSelectedList(GenericListModel<AbilityBuild> list)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Component getItemPanel(SelectionDialog<AbilityBuild> selectionDialog,
                                   Component currentItemPanel,
                                   AbilityBuild selectedItem)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public AbilityBuild createMutableItem(SelectionDialog<AbilityBuild> selectionDialog,
                                           AbilityBuild templateItem)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isMutable(Object item)
    {
        return item instanceof MutableAbilityBuild;
    }

    public Properties getDisplayProperties()
    {
        return props;
    }

}
