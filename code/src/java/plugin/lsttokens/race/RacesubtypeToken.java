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

import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.core.Race;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.RaceLstToken;
import pcgen.util.Logging;

/**
 * Class deals with RACESUBTYPE Token
 */
public class RacesubtypeToken implements RaceLstToken
{

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
		if (value.length() == 0)
		{
			Logging.errorPrint("Invalid " + getTokenName());
			Logging.errorPrint("  Requires at least one argument");
			return false;
		}
		if (value.charAt(0) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with | : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not end with | : " + value);
			return false;
		}
		if (value.indexOf("||") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator || : " + value);
			return false;
		}
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		while (tok.hasMoreTokens())
		{
			race.addToListFor(ListKey.RACESUBTYPE, RaceSubType.getConstant(tok
				.nextToken()));
		}
		return true;
	}

	public String unparse(LoadContext context, Race race)
	{
		List<RaceSubType> raceSubTypes = race.getListFor(ListKey.RACESUBTYPE);
		if (raceSubTypes == null || raceSubTypes.isEmpty())
		{
			return null;
		}
		StringBuilder sb =
				new StringBuilder().append(getTokenName()).append(':');
		boolean needPipe = false;
		for (RaceSubType rst : raceSubTypes)
		{
			if (needPipe)
			{
				sb.append(Constants.PIPE);
			}
			sb.append(rst);
			needPipe = true;
		}
		return sb.toString();
	}
}
