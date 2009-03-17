/*
 * CharacterEditor.java
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
 * Created on Mar 1, 2009, 6:15:37 PM
 */
package pcgen.core;

import pcgen.cdom.base.Category;
import pcgen.cdom.enumeration.Gender;
import pcgen.core.facade.AbilityFacade;
import pcgen.core.facade.AlignmentFacade;
import pcgen.core.facade.PCClassFacade;
import pcgen.core.facade.PCLevelFacade;
import pcgen.core.facade.PCStatFacade;
import pcgen.core.facade.PlayerCharacterFacade;
import pcgen.core.facade.SkillFacade;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class CharacterEditor
{

	public static void setAlignment(PlayerCharacterFacade character,
									AlignmentFacade alignment)
	{
	}

	public static void setGender(PlayerCharacterFacade character, Gender gender)
	{
	}

	public static void setScore(PlayerCharacterFacade character, PCStatFacade stat,
								int score)
	{
	}

	public static void addAbility(PlayerCharacterFacade character, AbilityFacade ability)
	{
	}

	public static void removeAbility(PlayerCharacterFacade character, AbilityFacade ability)
	{
	}

	public static void addLevels(PlayerCharacterFacade character, PCClassFacade[] classes)
	{
	}

	public static void removeLevels(PlayerCharacterFacade character, int levels)
	{
	}

	public static void setRemainingSelection(PlayerCharacterFacade character,
											 Category<AbilityFacade> catagory,
											 int remaining)
	{
	}

	public static void applyTempBonus(PlayerCharacterFacade character, TempBonus bonus,
									  boolean apply)
	{
	}

	/**
	 *
	 * This method handles adding and removing skill points to the character's
	 * skills. This methods takes into acount the skill cost and spendable skill
	 * points and will call appropriate message dialogs when an inappropriate
	 * action is called.
	 * @param skill the skill to invest points in
	 * @param points the amount of points to invest
	 * @return true if the points were successfuly invested
	 */
	public static boolean investSkillPoints(PlayerCharacterFacade character,
											PCLevelFacade level, SkillFacade skill,
											int points)
	{
		return false;
	}

	public static void setGainedSkillPoints(PlayerCharacterFacade character,
											PCLevelFacade level, int points)
	{
	}

}
