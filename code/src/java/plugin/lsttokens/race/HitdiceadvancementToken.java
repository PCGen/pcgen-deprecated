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

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Race;
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
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " may not have empty argument");
			return false;
		}
		if (value.indexOf(',') == -1)
		{
			Logging.errorPrint(getTokenName()
				+ " must have more than one comma delimited argument");
			return false;
		}
		if (value.charAt(0) == ',')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with , : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == ',')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not end with , : " + value);
			return false;
		}
		if (value.indexOf(",,") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator ,, : " + value);
			return false;
		}

		final StringTokenizer commaTok =
				new StringTokenizer(value, Constants.COMMA);

		if (race.containsListFor(ListKey.HITDICE_ADVANCEMENT))
		{
			Logging.errorPrint("Encountered second " + getTokenName()
				+ ": overwriting: "
				+ race.getListFor(ListKey.HITDICE_ADVANCEMENT));
			race.removeListFor(ListKey.HITDICE_ADVANCEMENT);
		}
		int last = 0;
		while (commaTok.hasMoreTokens())
		{
			String tok = commaTok.nextToken();
			int hd;
			if ("*".equals(tok))
			{
				if (commaTok.hasMoreTokens())
				{
					Logging.errorPrint("Found * in " + getTokenName()
						+ " but was not at end of list");
					return false;
				}
				hd = Integer.MAX_VALUE;
			}
			else
			{
				try
				{
					hd = Integer.parseInt(tok);
					if (hd <= last)
					{
						Logging.errorPrint("Found " + hd + " in "
							+ getTokenName() + " but was <= zero "
							+ "or the previous value in the list: " + value);
						return false;
					}
					last = hd;
				}
				catch (NumberFormatException nfe)
				{
					return false;
				}
			}
			race.addToListFor(ListKey.HITDICE_ADVANCEMENT, Integer.valueOf(hd));
		}
		return true;
	}

	public String[] unparse(LoadContext context, Race race)
	{
		List<Integer> list = race.getListFor(ListKey.HITDICE_ADVANCEMENT);
		if (list == null || list.isEmpty())
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();
		boolean needsComma = false;
		int last = 0;
		for (Iterator<Integer> it = list.iterator(); it.hasNext();)
		{
			if (needsComma)
			{
				sb.append(',');
			}
			needsComma = true;
			Integer hd = it.next();
			if (hd.intValue() == Integer.MAX_VALUE)
			{
				if (it.hasNext())
				{
					context.addWriteMessage("Integer MAX_VALUE found in "
						+ getTokenName() + " was not at the end of the array.");
					return null;
				}
				sb.append('*');
			}
			else
			{
				if (hd.intValue() <= last)
				{
					Logging.errorPrint("Found " + hd + " in " + getTokenName()
						+ " but was <= zero "
						+ "or the previous value in the list: " + list);
					return null;
				}
				last = hd.intValue();
				sb.append(hd);
			}
		}
		return new String[]{sb.toString()};
	}
}
