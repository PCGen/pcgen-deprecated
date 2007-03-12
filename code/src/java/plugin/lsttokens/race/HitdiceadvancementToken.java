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
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.mode.Size;
import pcgen.core.Race;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.RaceLstToken;
import pcgen.util.Logging;

/**
 * Class deals with HITDICEADVANCEMENT Token
 */
public class HitdiceadvancementToken extends AbstractToken implements
		RaceLstToken
{
	private static final Class<Size> SIZE_CLASS = Size.class;

	@Override
	public String getTokenName()
	{
		return "HITDICEADVANCEMENT";
	}

	public boolean parse(Race race, String value)
	{
		try
		{
			final StringTokenizer advancement = new StringTokenizer(value, ",");

			final int[] hitDiceAdvancement = new int[advancement.countTokens()];

			for (int x = 0; x < hitDiceAdvancement.length; ++x)
			{
				String temp = advancement.nextToken();

				if ((temp.length() > 0) && (temp.charAt(0) == '*'))
				{
					race.setAdvancementUnlimited(true);
				}

				if (race.isAdvancementUnlimited())
				{
					hitDiceAdvancement[x] = -1;
				}
				else
				{
					hitDiceAdvancement[x] = Integer.parseInt(temp);
				}
			}

			race.setHitDiceAdvancement(hitDiceAdvancement);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}

	public boolean parse(LoadContext context, Race race, String value)
	{
		final StringTokenizer commaTok =
				new StringTokenizer(value, Constants.COMMA);

		if (!commaTok.hasMoreTokens())
		{
			Logging.errorPrint(getTokenName() + " requires Tokens");
			return false;
		}
		int base;
		Size size;
		try
		{
			base = Integer.parseInt(commaTok.nextToken());
			/*
			 * BUG FIXME What is HitdiceadvancementToken is hit before SIZE??
			 * 
			 * Order of operations :(
			 */
			Set<PCGraphEdge> set =
					context.graph.getChildLinksFromToken("SIZE", race,
						SIZE_CLASS);
			if (set.size() > 1)
			{
				return false;
			}
			PCGraphEdge edge = set.iterator().next();
			size = (Size) edge.getSinkNodes().get(0);
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
		while (commaTok.hasMoreTokens())
		{
			String tok = commaTok.nextToken();
			Prerequisite p;
			if ("*".equals(tok))
			{
				// TODO Do I need this?!?
				// race.setAdvancementUnlimited(true);
				p = getPrerequisite("PREHD:" + base);
			}
			else
			{
				int end;
				try
				{
					end = Integer.parseInt(tok);
				}
				catch (NumberFormatException nfe)
				{
					return false;
				}
				p = getPrerequisite("PREHD:" + base + "-" + end);
				base = end;
			}
			size = size.getNextSize();
			PCGraphGrantsEdge edge =
					context.graph.linkObjectIntoGraph(getTokenName(), race,
						size);
			edge.addPrerequisite(p);
		}
		return true;
	}

	public String unparse(LoadContext context, Race race)
	{
		Set<PCGraphEdge> edges =
				context.graph.getChildLinksFromToken(getTokenName(), race,
					Size.class);
		if (edges == null || edges.isEmpty())
		{
			return null;
		}
		// FIXME Actually, I need to sort the sizes by their order,
		// and then extract data from the prereqs...
		for (PCGraphEdge edge : edges)
		{
			if (edge.getPrerequisiteCount() != 1)
			{
				context.addWriteMessage("Size attached by " + getTokenName()
					+ " requires a Prerequisiste");
			}
			Prerequisite prereq = edge.getPrerequisiteList().get(0);
			Size size = (Size) edge.getSinkNodes().get(0);

		}
		// FIXME Auto-generated method stub
		return null;
	}
}
