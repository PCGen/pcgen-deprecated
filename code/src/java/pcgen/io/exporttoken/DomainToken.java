/*
 * DomainToken.java
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

import pcgen.core.Constants;
import pcgen.core.Domain;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;

import java.util.StringTokenizer;

/**
 * Deals with tokens:
 * 
 * DOMAIN.x
 * DOMAIN.x.POWER
 */
public class DomainToken extends Token
{
	/** Token Name */
	public static final String TOKENNAME = "DOMAIN";

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
		StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		aTok.nextToken();

		if (aTok.hasMoreTokens())
		{
			int domainIndex = 0;

			try
			{
				domainIndex =
						Math.max(0, Integer.parseInt(aTok.nextToken()) - 1);
			}
			catch (Exception e)
			{
				// TODO - This exception needs to be handled
			}

			if (aTok.hasMoreTokens())
			{
				String subToken = aTok.nextToken();

				if ("POWER".equals(subToken))
				{
					retString = getPowerToken(pc, domainIndex);
				}
			}
			else
			{
				retString = getDomainToken(pc, domainIndex);
			}
		}

		return retString;
	}

	/**
	 * Get the DOMAIN token
	 * @param pc
	 * @param domainIndex
	 * @return token
	 */
	public static String getDomainToken(PlayerCharacter pc, int domainIndex)
	{
		try
		{
			Domain domain =
					(pc.getCharacterDomainList().get(domainIndex)).getDomain();

			return domain.getOutputName();
		}
		catch (Exception e)
		{
			return Constants.EMPTY_STRING;
		}
	}

	/**
	 * Get the POWER sub token
	 * @param pc
	 * @param domainIndex
	 * @return POWER sub token
	 */
	public static String getPowerToken(PlayerCharacter pc, int domainIndex)
	{
		try
		{
			Domain domain =
					(pc.getCharacterDomainList().get(domainIndex)).getDomain();

			return domain.piDescString(pc);
		}
		catch (Exception e)
		{
			return Constants.EMPTY_STRING;
		}
	}
}
