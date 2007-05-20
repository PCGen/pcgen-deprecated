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
import pcgen.core.Race;
import pcgen.persistence.Changes;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with RACESUBTYPE Token
 */
public class RacesubtypeToken extends AbstractToken implements RaceLstToken
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
			race.addRacialSubType(subType);
		}
		return true;
	}

	public boolean parse(LoadContext context, Race race, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		while (tok.hasMoreTokens())
		{
			context.obj.addToList(race, ListKey.RACESUBTYPE, RaceSubType
				.getConstant(tok.nextToken()));
		}
		return true;
	}

	public String[] unparse(LoadContext context, Race race)
	{
		Changes<RaceSubType> changes =
				context.obj.getListChanges(race, ListKey.RACESUBTYPE);
		if (changes == null)
		{
			return null;
		}
		return new String[]{StringUtil.join(changes.getAdded(), Constants.PIPE)};
	}
}
