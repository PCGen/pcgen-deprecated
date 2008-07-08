/*
 * CharacterFacade.java
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
 * Created on Jun 12, 2008, 8:27:12 PM
 */
package pcgen.gui.facade;

import pcgen.gui.util.GenericListModel;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public interface CharacterFacade
{

    /**
     * Note: This method should never return null. If the character does not possess
     * any abilities in the parameter catagory, this method should create a new
     * GenericListModel for that catagory and keep a reference to it for future use.
     * @param catagory
     * @return a List of Abilities the character posseses in the specified catagory
     */
    public GenericListModel<AbilityFacade> getAbilities(AbilityCatagoryFacade catagory);

    public ClassFacade getSelectedClass(int level);

    public void addCharacterLevels(ClassFacade[] classes);

    public void removeCharacterLevels(int levels);

    public int getCharacterLevel();

    public int getClassLevel(ClassFacade c);

    public int getRemainingSelections(AbilityCatagoryFacade catagory);

    public void setRemainingSelection(AbilityCatagoryFacade catagory,
                                       int remaining);

}
