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

import java.util.Set;

import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.mode.Size;
import pcgen.core.Race;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.RaceLstToken;
import pcgen.util.Logging;

/**
 * Class deals with SIZE Token
 */
public class SizeToken implements RaceLstToken
{

	public String getTokenName()
	{
		return "SIZE";
	}

	public boolean parse(Race race, String value)
	{
		race.setSize(value);
		return true;
	}

	public boolean parse(LoadContext context, Race race, String value)
	{
		try
		{
			Size size = Size.valueOf(value);
			context.graph.linkObjectIntoGraph(getTokenName(), race, size);
			return true;
		}
		catch (IllegalArgumentException e)
		{
			Logging.errorPrint("Invalid Size in " + getTokenName() + ": "
				+ value);
			return false;
		}
	}

	public String[] unparse(LoadContext context, Race race)
	{
		Set<PCGraphEdge> links =
				context.graph.getChildLinksFromToken(getTokenName(), race,
					Size.class);
		if (links == null || links.isEmpty())
		{
			return null;
		}
		if (links.size() > 1)
		{
			context.addWriteMessage("Only 1 Size is allowed per Race");
			return null;
		}
		Size s = (Size) links.iterator().next().getSinkNodes().get(0);
		return new String[]{s.toString()};
	}
}
