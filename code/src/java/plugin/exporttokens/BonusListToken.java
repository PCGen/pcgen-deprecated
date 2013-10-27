/*
 * BonusListToken.java
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

import pcgen.core.PlayerCharacter;
//import pcgen.core.bonus.TypedBonus;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

import java.util.StringTokenizer;
import java.util.Map;

/**
 * Deals with BONUSLIST token
 */
public class BonusListToken extends Token
{
	/** Token name */
	public static final String TOKENNAME = "BONUSLIST";

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
		return getBonusListToken(tokenSource, pc);
	}

	/**
	 * Get Bonus List Token
	 * @param tokenSource
	 * @param pc
	 * @return String of Bonus List
	 */
	public static String getBonusListToken(String tokenSource,
		PlayerCharacter pc)
	{
		StringTokenizer bTok =
				new StringTokenizer(tokenSource.substring(10), ".", false);
		String bonusString = "";
		String substring = "";
		String typeSeparator = " ";
		String delim = ", ";
		StringBuffer returnString = new StringBuffer();

		if (bTok.hasMoreTokens())
		{
			bonusString = bTok.nextToken();
		}

		if (bTok.hasMoreTokens())
		{
			substring = bTok.nextToken();
		}

		if (bTok.hasMoreTokens())
		{
			typeSeparator = bTok.nextToken();
		}

		if (bTok.hasMoreTokens())
		{
			delim = bTok.nextToken();
		}

		int typeLen = bonusString.length() + substring.length() + 2;

		if ((substring.length() > 0) && (bonusString.length() > 0))
		{
			// Commented out this += since it's useless code (see TODO below) thpr 10/21/06
			//int total = (int) pc.getTotalBonusTo(bonusString, substring);

			if ("TOTAL".equals(typeSeparator))
			{
				// TODO - Shouldn't this return retString? - ??? unknown date
				// Commented out this += since it's useless code thpr 10/21/06
				// retString += total;

				return "";
			}

			boolean needDelim = false;
			String prefix = bonusString + "." + substring + ".";

			//			final List<String> bonuses = TypedBonus.totalBonusesByType( pc.getBonusesTo(bonusString, substring));
			//			for ( final String str : bonuses )
			//			{
			//				if ( needDelim )
			//				{
			//					retString += delim;
			//				}
			//				
			//				retString += str;
			//				needDelim = true;
			//			}

			for (Map.Entry<String, String> entry : pc.getActiveBonusMap()
				.entrySet())
			{
				String aKey = entry.getKey();

				if (aKey.startsWith(prefix))
				{
					if (needDelim)
					{
						returnString.append(delim);
					}

					if (aKey.length() > typeLen)
					{
						returnString.append(aKey.substring(typeLen));
					}
					else
					{
						returnString.append("None");
					}

					returnString.append(typeSeparator);
					returnString.append(entry.getValue());
					needDelim = true;
				}
			}
		}

		return returnString.toString();
	}
}
