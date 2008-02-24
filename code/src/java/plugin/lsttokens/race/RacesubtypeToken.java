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

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.cdom.inst.CDOMRace;
import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with RACESUBTYPE Token
 */
public class RacesubtypeToken extends AbstractToken implements RaceLstToken, CDOMPrimaryToken<CDOMRace>
{

	@Override
	public String getTokenName()
	{
		return "RACESUBTYPE";
	}

	public boolean parse(Race race, String value)
	{
		StringTokenizer tok = new StringTokenizer(value, "|");
		while (tok.hasMoreTokens())
		{
			String subType = tok.nextToken();
			if (subType.indexOf(".CLEAR") != -1)
			{
				if (".CLEAR".equals(subType))
				{
					race.clearRacialSubTypes();
				}
				else
				{
					subType = subType.substring(7);
					race.removeRacialSubType(subType);
				}
			}
			else
			{
				race.addRacialSubType(subType);
			}
		}
		return true;
	}

	public boolean parse(LoadContext context, CDOMRace race, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		while (tok.hasMoreTokens())
		{
			context.getObjectContext().addToList(race, ListKey.RACESUBTYPE, RaceSubType
				.getConstant(tok.nextToken()));
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMRace race)
	{
		Changes<RaceSubType> changes =
				context.getObjectContext().getListChanges(race, ListKey.RACESUBTYPE);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		return new String[]{StringUtil.join(changes.getAdded(), Constants.PIPE)};
	}

	public Class<CDOMRace> getTokenClass()
	{
		return CDOMRace.class;
	}
}
