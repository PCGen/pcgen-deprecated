/*
 * KitBio.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Created on February 16, 2006, 11:43 AM
 *
 * $Id$
 */
package pcgen.core.kit;

import java.util.List;

import pcgen.core.Kit;
import pcgen.core.PlayerCharacter;
import java.util.ArrayList;
import java.util.StringTokenizer;
import pcgen.core.Globals;

/**
 * Code to represent a bio setting choices for a Kit.
 *
 * @author Aaron Divinsky <boomer70@yahoo.com>
 * @version $Revision$
 */
public class KitBio extends BaseKit
{
	private String theCharacterName = null;
	private String theGender = null;

	/**
	 * Set the character name to set for this kit item.
	 * @param aName Name to use.  Can be any string.
	 */
	public void setCharacterName(final String aName)
	{
		theCharacterName = aName;
	}

	/**
	 * Set the gender to use for this kit item.
	 * @param aGender Gender to use.  Can be any string.
	 */
	public void setGender(final String aGender)
	{
		theGender = aGender;
	}

	/**
	 * This method actually applies any changes that can be made by the
	 * kit to the specified PlayerCharacter.
	 *
	 * @param aPC The character to apply the kit to.
	 */
	public void apply(PlayerCharacter aPC)
	{
		if (theCharacterName != null)
		{
			aPC.setName(theCharacterName);
		}
		if (theGender != null)
		{
			aPC.setGender(theGender);
		}
	}

	/**
	 * The display name to represent what this kit item represents.
	 *
	 * @return object name
	 */
	public String getObjectName()
	{
		return "Bio Settings";
	}

	/**
	 * Try and apply the selected gender to the character.  Any problems
	 * encountered should be logged as a string in the
	 * <code>warnings</code> list.
	 *
	 * @param aKit The owning kit for this item
	 * @param aPC The character the kit is being applied to
	 * @param warnings A list of warnings generated while attempting to
	 *   apply the kit
	 * @return true if OK
	 */
	public boolean testApply(Kit aKit, PlayerCharacter aPC, List<String> warnings)
	{
		if (theGender != null)
		{
			ArrayList<String> genders = new ArrayList<String>();

			StringTokenizer tok = new StringTokenizer(theGender, "|");
			while (tok.hasMoreTokens())
			{
				String gen = tok.nextToken();
				genders.add(gen);
			}
			if (genders.size() > 1)
			{
				List<String> selList = new ArrayList<String>(1);
				Globals.getChoiceFromList("Choose Gender", genders, selList, 1);
				if (selList.size() == 1)
				{
					theGender = selList.get(0);
				}
			}
		}
		apply(aPC);

		return true;
	}

	public String toString()
	{
		final StringBuffer info = new StringBuffer();

		if (theCharacterName != null)
		{
			info.append(" Name: " + theCharacterName);
		}
		if (theGender != null)
		{
			info.append(" Gender: " + theGender);
		}

		return info.toString();
	}
}
