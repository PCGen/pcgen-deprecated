/*
 * PlayerCharacterUtilities.java
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
 * Current Version: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 * Copyright 2005 Andrew Wilson <nuance@sourceforge.net>
 * Class created by migrating code from PlayerCharacter
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 */
package pcgen.core;


import java.util.StringTokenizer;

/**
 * Utilities class for PlayerCharacter.  Holds various static methods.
 *
 * @author  Andrew Wilson <nuance@sourceforge.net>
 */
public class PlayerCharacterUtilities
{
	/**
	 * Bryan wanted this to be optional, but if you can reassign racial auto
	 * feats, when you reopen the character, you get the feats that were
	 * exchanged back
	 *
	 * @return  false
	 */
	static boolean canReassignRacialFeats()
	{
		return false;
	}

	/**
	 * Bryan wanted this to be optional, but if you can reassign racial auto
	 * feats, when you reopen the character, you get the feats that were
	 * exchanged back
	 *
	 * @return  false
	 */
	static boolean canReassignTemplateFeats()
	{
		return false;
	}

	/**
	 * Picks the biggest die size from two strings in the form V|WdX, YdZ (where
	 * the WdX represents W X sided dice).  If Z is larger than X, returns
	 * V|YdZ, otherwise it returns V|WdX
	 *
	 * @param   oldString  2|1d3
	 * @param   newString  1d4
	 *
	 * @return  in the example parameters given, will return 2|1d4 (because the
	 *          4 is bigger than the 3). If the last figure in the new string
	 *          isn't larger, it returns the original string.
	 */
	static String getBestUDamString(final String oldString, final String newString)
	{
		if ((newString == null) || (newString.length() < 2))
		{
			return oldString;
		}

		StringTokenizer aTok      = new StringTokenizer(oldString, "|");
		int             sides     = Integer.parseInt(aTok.nextToken());
		String          retString = oldString;

		aTok = new StringTokenizer(newString, " dD+-(x)");

		if (aTok.countTokens() > 1)
		{
			aTok.nextToken();

			final int i = Integer.parseInt(aTok.nextToken());

			if (sides < i)
			{
				sides     = i;
				retString = sides + "|" + newString;
			}
		}

		return retString;
	}

	/**
	 * Returns the number of experience points needed for level
	 *
	 * @param   level  character level to calculate experience for
	 * @param   pc     the PC that we are asking about (ECL of character can
	 *                 affect the result)
	 *
	 * @return  The experience points needed
	 */
	static int minXPForLevel(final int level, final PlayerCharacter pc)
	{
		LevelInfo lInfo = Globals.getLevelInfo().get(String.valueOf(level));

		if (lInfo == null)
		{
			lInfo = Globals.getLevelInfo().get("LEVEL");
		}

		if ((level > 0) && (lInfo != null))
		{
			return lInfo.getMinXP(level, pc);
		}
		// do something sensible if no level info
		return 0;
	}

	/**
	 * Set the Weapon proficiency of one piece of Equipment to the same as the
	 * Proficiency in another piece of Equipment.  For some bizarre reason, as
	 * well as setting the proficiency,  this zeros out the Weight and cost of
	 * the equipment.
	 *
	 * @param  equip  the Weapon to get the proficiency from
	 * @param  eqm    the weapon to set the proficiency in
	 */
	static void setProf(final Equipment equip, final Equipment eqm)
	{
		// Make sure the proficiency is set
		String profName = equip.rawProfName();

		if (profName.length() == 0)
		{
			profName = equip.getName();
		}

		eqm.setProfName(profName);

		// In case this is used somewhere it shouldn't be used,
		// set weight and cost to 0
		eqm.setWeight("0");
		eqm.setCost("0");
	}
}
