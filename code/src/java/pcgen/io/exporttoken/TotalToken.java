/*
 * TotalToken.java
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
package pcgen.io.exporttoken;

import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.utils.CoreUtility;
import pcgen.io.ExportHandler;
import pcgen.util.BigDecimalHelper;
import pcgen.util.Logging;
import pcgen.util.enumeration.Load;

/**
 * Deal with returning TOTAL Tokens
 * 
 * TOTAL.WEIGHT
 * TOTAL.VALUE
 * TOTAL.CAPACITY
 * TOTAL.LOAD
 */
public class TotalToken extends Token
{
	/** Token name */
	public static final String TOKENNAME = "TOTAL";

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return TOKENNAME;
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	@Override
	public String getToken(String tokenSource, PlayerCharacter pc,
		ExportHandler eh)
	{
		String retString = "";

		if ("TOTAL.WEIGHT".equals(tokenSource))
		{
			retString = getWeightToken(pc);
		}
		else if ("TOTAL.VALUE".equals(tokenSource))
		{
			retString = getValueToken(pc);
		}
		else if ("TOTAL.CAPACITY".equals(tokenSource))
		{
			retString = getCapacityToken(pc);
		}
		else if ("TOTAL.LOAD".equals(tokenSource))
		{
			retString = getLoadToken(pc);
		}

		return retString;
	}

	/**
	 * Get the CAPACITY sub token
	 * @param pc
	 * @return the CAPACITY sub token
	 */
	public static String getCapacityToken(PlayerCharacter pc)
	{
		return Globals.getGameModeUnitSet().displayWeightInUnitSet(
			Globals.maxLoadForLoadScore(
				pc.getVariableValue("LOADSCORE", "").intValue(), pc)
				.doubleValue());
	}

	/**
	 * Get the LOAD sub token
	 * @param pc
	 * @return the LOAD sub token
	 */
	public static String getLoadToken(PlayerCharacter pc)
	{
		Load load =
				Globals.loadTypeForLoadScore(pc.getVariableValue("LOADSCORE",
					"").intValue(), pc.totalWeight(), pc);

		switch (load)
		{
			case LIGHT:
				return CoreUtility.capitalizeFirstLetter(Load.LIGHT.toString());

			case MEDIUM:
				return CoreUtility
					.capitalizeFirstLetter(Load.MEDIUM.toString());

			case HEAVY:
				return CoreUtility.capitalizeFirstLetter(Load.HEAVY.toString());

			case OVERLOAD:
				return CoreUtility.capitalizeFirstLetter(Load.OVERLOAD
					.toString());

			default:
				Logging
					.errorPrint("Unknown load constant detected in TokenTotal.getLoadToken, the constant was "
						+ load + ".");

				return "Unknown";
		}
	}

	/**
	 * Get the VALUE sub token
	 * @param pc
	 * @return the VALUE sub token
	 */
	public static String getValueToken(PlayerCharacter pc)
	{
		return BigDecimalHelper.trimZeros(pc.totalValue()) + " "
			+ Globals.getCurrencyDisplay();
	}

	/**
	 * Get the WEIGHT sub token
	 * @param pc
	 * @return the WEIGHT sub token
	 */
	public static String getWeightToken(PlayerCharacter pc)
	{
		return Globals.getGameModeUnitSet().displayWeightInUnitSet(
			pc.totalWeight().doubleValue())
			+ Globals.getGameModeUnitSet().getWeightUnit();
	}
}
