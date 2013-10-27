/*
 * ExpToken.java
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
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

import java.util.StringTokenizer;

/** 
 * Deal with Tokens:
 * 
 * EXP.CURRENT
 * EXP.NEXT
 * EXP.FACTOR
 * EXP.PENALTY
 */
public class ExpToken extends Token
{
	/** Token name */
	public static final String TOKENNAME = "EXP";

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
		StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		aTok.nextToken();

		if (aTok.hasMoreTokens())
		{
			String token = aTok.nextToken();

			if ("CURRENT".equals(token))
			{
				retString = Integer.toString(getCurrentToken(pc));
			}
			else if ("NEXT".equals(token))
			{
				retString = Integer.toString(getNextToken(pc));
			}
			else if ("FACTOR".equals(token))
			{
				retString = getFactorToken(pc);
			}
			else if ("PENALTY".equals(token))
			{
				retString = getPenaltyToken(pc);
			}
		}

		return retString;
	}

	/**
	 * Get CURRENT Sub Token
	 * @param pc
	 * @return CURRENT Sub Token
	 */
	public static int getCurrentToken(PlayerCharacter pc)
	{
		return pc.getXP();
	}

	/**
	 * Get Factor Sub Token
	 * @param pc
	 * @return Factor Sub Token
	 */
	public static String getFactorToken(PlayerCharacter pc)
	{
		StringBuffer xpFactor = new StringBuffer(5);
		xpFactor.append((int) (pc.multiclassXPMultiplier() * 100.0));
		xpFactor.append('%');

		return xpFactor.toString();
	}

	/**
	 * Get Next Sub Token
	 * @param pc
	 * @return Next Sub Token
	 */
	public static int getNextToken(PlayerCharacter pc)
	{
		return pc.minXPForNextECL();
	}

	/**
	 * Get Penalty Sub Token
	 * @param pc
	 * @return Penalty Sub Token
	 */
	public static String getPenaltyToken(PlayerCharacter pc)
	{
		StringBuffer xpFactor = new StringBuffer(5);
		xpFactor.append(100 - (int) (pc.multiclassXPMultiplier() * 100.0));
		xpFactor.append('%');

		return xpFactor.toString();
	}
}
