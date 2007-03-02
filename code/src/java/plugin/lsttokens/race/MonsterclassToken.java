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

import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.LevelCommandFactory;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.core.PCClass;
import pcgen.core.Race;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with MONSTERCLASS Token
 */
public class MonsterclassToken implements RaceLstToken
{

	public String getTokenName()
	{
		return "MONSTERCLASS";
	}

	public boolean parse(Race race, String value)
	{
		try
		{
			final StringTokenizer mclass =
					new StringTokenizer(value, Constants.COLON);

			if (mclass.countTokens() != 2)
			{
				return false;
			}
			race.setMonsterClass(mclass.nextToken());
			race.setMonsterClassLevels(Integer.parseInt(mclass.nextToken()));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}

	public boolean parse(LoadContext context, Race race, String value)
	{
		int colonLoc = value.indexOf(Constants.COLON);
		if (colonLoc == -1)
		{
			return false;
		}
		if (colonLoc != value.lastIndexOf(Constants.COLON))
		{
			return false;
		}
		String classString = value.substring(0, colonLoc);
		CDOMSimpleSingleRef<PCClass> cl =
				context.ref.getCDOMReference(PCClass.class, classString);
		int lvls;
		try
		{
			String numLevels = value.substring(colonLoc + 1);
			lvls = Integer.parseInt(numLevels);
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
		LevelCommandFactory cf = new LevelCommandFactory(cl, lvls);
		context.graph.linkObjectIntoGraph(getTokenName(), race, cf);
		return true;
	}

	public String unparse(LoadContext context, Race race)
	{
		Set<PCGraphEdge> edges =
				context.graph.getChildLinksFromToken(getTokenName(), race,
					LevelCommandFactory.class);
		StringBuilder sb = new StringBuilder();
		boolean needsTab = false;
		for (PCGraphEdge edge : edges)
		{
			if (needsTab)
			{
				sb.append('\t');
			}
			LevelCommandFactory lcf =
					(LevelCommandFactory) edge.getSinkNodes().get(0);
			int lvls = lcf.getLevelCount();
			if (lvls <= 0)
			{
				context.addWriteMessage("Number of Levels granted in "
					+ getTokenName() + " must be greater than zero");
				return null;
			}
			sb.append(lcf.getPCClass().getKeyName()).append(Constants.COLON)
				.append(lvls);
			needsTab = true;
		}
		return sb.toString();
	}
}
