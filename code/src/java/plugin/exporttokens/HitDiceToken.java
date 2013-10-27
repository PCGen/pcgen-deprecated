/*
 * HitDiceToken.java
 * Copyright 2003 (C) Devon Jones <soulcatcher@evilsoft.org>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on December 15, 2003, 12:21 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.exporttokens;

import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;
import pcgen.util.Delta;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Deals with the HITDICE Token
 */
public class HitDiceToken extends Token
{
	/** Token Name */
	public static final String TOKENNAME = "HITDICE";

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	public String getTokenName()
	{
		return TOKENNAME;
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	public String getToken(String tokenSource, PlayerCharacter pc,
		ExportHandler eh)
	{
		String retString = "";

		if ("HITDICE".equals(tokenSource))
		{
			retString = getHitDiceToken(pc);
		}
		else if ("HITDICE.SHORT".equals(tokenSource))
		{
			retString = getShortToken(pc);
		}

		return retString;
	}

	/**
	 * Get the HITDICE token
	 * @param pc
	 * @return the HITDICE token
	 */
	public static String getHitDiceToken(PlayerCharacter pc)
	{
		StringBuffer ret = new StringBuffer();
		String del = "";

		if (pc.getRace().hitDice(pc) > 0)
		{
			ret.append("(");
			ret.append(Integer.toString(pc.getRace().hitDice(pc)));
			ret.append("d");
			ret.append(Integer.toString(pc.getRace().getHitDiceSize(pc)));
			ret.append(")");
			del = "+";
		}

		for (PCClass pcClass : pc.getClassList())
		{
			HashMap<Integer, Integer> hdMap =
					new LinkedHashMap<Integer, Integer>();

			for (int i = 0; i < pcClass.getLevel(); i++)
			{
				int hitDie = pcClass.getLevelHitDie(pc, i + 1);
				Integer num = hdMap.get(hitDie);
				if (num == null)
				{
					hdMap.put(hitDie, 1);
				}
				else
				{
					hdMap.put(hitDie, num.intValue() + 1);
				}
			}

			Set<Integer> keys = hdMap.keySet();
			for (int key : keys)
			{
				Integer value = hdMap.get(key);
				ret.append(del);
				ret.append("(");
				ret.append(value).append("d").append(key);
				ret.append(")");
				del = "+";
			}
		}

		//
		// Get CON bonus contribution to hitpoint total
		//
		int temp = (int) pc.getStatBonusTo("HP", "BONUS");
		int a = pc.getRace().hitDice(pc);
		int b = 0;
		if (pc.isMonsterDefault())
		{
			b = pc.totalNonMonsterLevels();
		}
		else
		{
			b = pc.getTotalLevels();
		}
		temp *= (a + b);

		//
		// Add in feat bonus
		//
		temp += (int) pc.getTotalBonusTo("HP", "CURRENTMAX");

		if (temp != 0)
		{
			ret.append(Delta.toString(temp));
		}

		return ret.toString();
	}

	/**
	 * Get the short version of the HITDICE token
	 * @param pc
	 * @return the short version of the HITDICE token
	 */
	public static String getShortToken(PlayerCharacter pc)
	{
		int dice;

		dice = pc.getRace().hitDice(pc);

		for (PCClass pcClass : pc.getClassList())
		{
			HashMap<Integer, Integer> hdMap =
					new LinkedHashMap<Integer, Integer>();

			for (int i = 0; i < pcClass.getLevel(); i++)
			{
				int hitDie = pcClass.getLevelHitDie(pc, i + 1);
				Integer num = hdMap.get(hitDie);
				if (num == null)
				{
					hdMap.put(hitDie, 1);
				}
				else
				{
					hdMap.put(hitDie, num.intValue() + 1);
				}
			}

			Set<Integer> keys = hdMap.keySet();
			for (int hdSize : keys)
			{
				dice += hdMap.get(hdSize);
			}
		}

		return String.valueOf(dice);
	}
}
