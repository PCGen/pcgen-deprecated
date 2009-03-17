/*
 * CharacterOutput.java
 * Copyright 2009 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Mar 1, 2009, 7:01:25 PM
 */
package pcgen.core;

import javax.swing.undo.UndoManager;
import pcgen.cdom.enumeration.AbilityCategory;
import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.core.facade.AbilityFacade;
import pcgen.core.facade.AlignmentFacade;
import pcgen.core.facade.EquipmentFacade;
import pcgen.core.facade.PCClassFacade;
import pcgen.core.facade.PCLevelFacade;
import pcgen.core.facade.PCStatFacade;
import pcgen.core.facade.PlayerCharacterFacade;
import pcgen.core.facade.SkillFacade;
import pcgen.gui.util.GenericComboBoxModel;
import pcgen.gui.util.GenericListModel;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class CharacterOutput
{

	public static UndoManager getUndoManager(PlayerCharacterFacade character)
	{
		return null;
	}

	public static AlignmentFacade getAlignment(PlayerCharacterFacade character)
	{
		return null;
	}

	public static Gender getGender(PlayerCharacterFacade character)
	{
		return null;
	}

	public static int getScore(PlayerCharacterFacade character, PCStatFacade stat)
	{
		return 0;
	}

	public static int getMod(PlayerCharacterFacade character, PCStatFacade stat)
	{
		return 0;
	}

	public static PCClassFacade getSelectedClass(PlayerCharacterFacade character,
										   PCLevelFacade level)
	{
		return null;
	}

	public static SkillCost getSkillCost(PlayerCharacterFacade character,
										 PCLevelFacade level,
										 SkillFacade skill)
	{
		return null;
	}

	public static float getMaxRanks(PlayerCharacterFacade character, PCLevelFacade level,
									SkillCost cost)
	{
		return 0;
	}

	public static int getRankCost(PlayerCharacterFacade character, PCLevelFacade level,
								  SkillCost cost)
	{
		return 0;
	}

	public static int getGainedSkillPoints(PlayerCharacterFacade character,
										   PCLevelFacade level)
	{
		return 0;
	}

	public static int getSpentSkillPoints(PlayerCharacterFacade character,
										  PCLevelFacade level)
	{
		return 0;
	}

	/**
	 * Note: This method should never return null. If the character does not possess
	 * any abilities in the parameter catagory, this method should create a new
	 * DefaultGenericListModel for that catagory and keep a reference to it for future use.
	 * @param catagory
	 * @return a List of Abilities the character posseses in the specified catagory
	 */
	public static GenericListModel<AbilityFacade> getAbilities(
			PlayerCharacterFacade character, AbilityCategory catagory)
	{
		return null;
	}

	public static GenericComboBoxModel<EquipmentFacade> getEquipmentSets(
			PlayerCharacterFacade character)
	{
		return null;
	}

	public static GenericListModel<TempBonus> getTempBonuses(
			PlayerCharacterFacade character)
	{
		return null;
	}

	public static GenericListModel<PCLevelFacade> getLevels(PlayerCharacterFacade character)
	{
		return null;
	}

	public static int getClassLevel(PlayerCharacterFacade character, PCClassFacade cl)
	{
		return 0;
	}

	public static int getRemainingSelections(PlayerCharacterFacade character,
											 AbilityCategory catagory)
	{
		return 0;
	}

	public static int getSkillTotal(PlayerCharacterFacade character, SkillFacade skill)
	{
		return 0;
	}

	public static int getSkillModifier(PlayerCharacterFacade character, SkillFacade skill)
	{
		return 0;
	}

	public static float getSkillRanks(PlayerCharacterFacade character, SkillFacade skill)
	{
		return 0;
	}

}
