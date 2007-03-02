/*
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package plugin.lsttokens.race;

import java.util.StringTokenizer;

import pcgen.core.Race;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with HITDICE Token
 */
public class HitdiceToken implements RaceLstToken
{

	public String getTokenName()
	{
		return "HITDICE";
	}

	public boolean parse(Race race, String value)
	{
		try
		{
			final StringTokenizer hitdice = new StringTokenizer(value, ",");

			if (hitdice.countTokens() != 2)
			{
				return false;
			}
			race.setHitDice(Integer.parseInt(hitdice.nextToken()));
			race.setHitDiceSize(Integer.parseInt(hitdice.nextToken()));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}

	public boolean parse(LoadContext context, Race race, String value)
	{
		// This is a hack to cover the monster changes
		return true;
	}

	public String unparse(LoadContext context, Race race)
	{
		// Well, this is a hack too :)
		return null;
	}
}
