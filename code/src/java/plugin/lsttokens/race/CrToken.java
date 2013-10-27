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

import java.util.Collection;

import pcgen.cdom.content.ChallengeRating;
import pcgen.cdom.inst.CDOMRace;
import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with CR Token
 */
public class CrToken implements RaceLstToken, CDOMPrimaryToken<CDOMRace>
{

	public String getTokenName()
	{
		return "CR";
	}

	public boolean parse(Race race, String value)
	{
		try
		{
			String intValue = value;
			if (intValue.startsWith("1/"))
			{
				intValue = "-" + intValue.substring(2);
			}
			race.setCR(Integer.parseInt(intValue));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}

	public boolean parse(LoadContext context, CDOMRace race, String value)
	{
		ChallengeRating cr = new ChallengeRating(value);
		context.getObjectContext().give(getTokenName(), race, cr);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMRace race)
	{
		Changes<ChallengeRating> changes =
				context.getObjectContext().getGivenChanges(getTokenName(), race,
					ChallengeRating.class);
		Collection<ChallengeRating> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			// Zero indicates no Token present
			return null;
		}
		if (added.size() > 1)
		{
			context
				.addWriteMessage("Only 1 ChallengeRating is allowed per Race");
			return null;
		}
		return new String[]{added.iterator().next().getLSTformat()};
	}

	public Class<CDOMRace> getTokenClass()
	{
		return CDOMRace.class;
	}
}
