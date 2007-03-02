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

import pcgen.cdom.base.Slot;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.core.Ability;
import pcgen.core.Race;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.RaceLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

/**
 * Class deals with STARTFEATS Token
 */
public class StartfeatsToken implements RaceLstToken
{
	private static final Class<Ability> ABILITY_CLASS = Ability.class;

	public String getTokenName()
	{
		return "STARTFEATS";
	}

	public boolean parse(Race race, String value)
	{
		try
		{
			StringBuffer sb = new StringBuffer();
			sb.append("FEAT|POOL|").append(Integer.parseInt(value));

			final BonusObj bon = Bonus.newBonus(sb.toString());
			final PreParserFactory factory = PreParserFactory.getInstance();
			final StringBuffer buf = new StringBuffer();

			buf.append("PREMULT:1,[PREHD:1],[PRELEVEL:1]");

			final Prerequisite prereq = factory.parse(buf.toString());
			bon.addPreReq(prereq);

			race.setBonusInitialFeats(bon);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
		catch (PersistenceLayerException e)
		{
			Logging.errorPrint("Caught " + e);
			return false;
		}
	}

	public boolean parse(LoadContext context, Race race, String value)
	{
		int featCount;
		try
		{
			featCount = Integer.parseInt(value);
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Invalid Number in " + getTokenName() + ": "
				+ value);
			return false;
		}

		/*
		 * TODO Why was this PREMULT added in 5.11.? What situation would this
		 * PREMULT fail in?
		 */
		// buf.append("PREMULT:1,[PREDEFAULTMONSTER:N],[PREHD:1]");
		for (int i = 0; i < featCount; i++)
		{
			/*
			 * TODO FIXME Does this slot need to have a CATEGORY associated with
			 * it?
			 */
			context.graph.addSlotIntoGraph(getTokenName(), race, ABILITY_CLASS);
		}
		return true;
	}

	public String unparse(LoadContext context, Race race)
	{
		Set<PCGraphEdge> edgeList =
				context.graph.getChildLinksFromToken(getTokenName(), race,
					Slot.class);
		if (edgeList == null || edgeList.isEmpty())
		{
			return null;
		}
		if (edgeList.size() != 1)
		{
			context.addWriteMessage("Error in " + race.getKeyName()
				+ ": Only one STARTFEATS Slot is allowed per Race");
			return null;
		}
		PCGraphEdge edge = edgeList.iterator().next();
		Slot slot = (Slot) edge.getSinkNodes().get(0);
		if (!slot.getSlotClass().equals(ABILITY_CLASS))
		{
			context.addWriteMessage("Invalid Slot Type associated with "
				+ getTokenName() + ": Type cannot be "
				+ slot.getSlotClass().getSimpleName());
			return null;
		}
		return new StringBuilder().append(getTokenName()).append(':').append(
			slot.toLSTform()).toString();
	}
}
