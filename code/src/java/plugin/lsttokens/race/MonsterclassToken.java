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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMSingleRef;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.LevelCommandFactory;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.inst.CDOMRace;
import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with MONSTERCLASS Token
 */
public class MonsterclassToken implements RaceLstToken,
		CDOMPrimaryToken<CDOMRace>
{

	private static final Class<CDOMPCClass> PCCLASS_CLASS = CDOMPCClass.class;

	public String getTokenName()
	{
		return "MONSTERCLASS";
	}

	public boolean parse(Race race, String value)
	{
		try
		{
			final StringTokenizer mclass = new StringTokenizer(value,
					Constants.COLON);

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

	public boolean parse(LoadContext context, CDOMRace race, String value)
	{
		int colonLoc = value.indexOf(Constants.COLON);
		if (colonLoc == -1)
		{
			Logging.errorPrint(getTokenName() + " must have only a colon: "
					+ value);
			return false;
		}
		if (colonLoc != value.lastIndexOf(Constants.COLON))
		{
			Logging.errorPrint(getTokenName() + " must have only one colon: "
					+ value);
			return false;
		}
		String classString = value.substring(0, colonLoc);
		CDOMSingleRef<CDOMPCClass> cl = context.ref.getCDOMReference(
				PCCLASS_CLASS, classString);
		try
		{
			String numLevels = value.substring(colonLoc + 1);
			int lvls = Integer.parseInt(numLevels);
			if (lvls <= 0)
			{
				Logging.errorPrint("Number of levels in " + getTokenName()
						+ " must be greater than zero: " + value);
				return false;
			}
			LevelCommandFactory cf = new LevelCommandFactory(cl, lvls);
			context.getObjectContext().give(getTokenName(), race, cf);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Number of levels in " + getTokenName()
					+ " must be an integer greater than zero: " + value);
			return false;
		}
	}

	public String[] unparse(LoadContext context, CDOMRace race)
	{
		Changes<LevelCommandFactory> changes = context
				.getObjectContext().getGivenChanges(getTokenName(), race,
						LevelCommandFactory.class);
		Collection<LevelCommandFactory> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			return null;
		}
		List<String> list = new ArrayList<String>();
		for (LevelCommandFactory lcf : added)
		{
			StringBuilder sb = new StringBuilder();
			int lvls = lcf.getLevelCount();
			if (lvls <= 0)
			{
				context.addWriteMessage("Number of Levels granted in "
						+ getTokenName() + " must be greater than zero");
				return null;
			}
			sb.append(lcf.getLSTformat()).append(Constants.COLON).append(lvls);
			list.add(sb.toString());
		}
		return list.toArray(new String[list.size()]);
	}

	public Class<CDOMRace> getTokenClass()
	{
		return CDOMRace.class;
	}
}
