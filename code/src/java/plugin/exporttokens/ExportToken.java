/*
 * ExportToken.java
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

import java.text.DateFormat;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Deals with Tokens:
 * 
 * EXPORT
 * EXPORT.DATE
 * EXPORT.TIME
 * EXPORT.VERSION
 */
public class ExportToken extends Token
{
	/** Token Name */
	public static final String TOKENNAME = "EXPORT";

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
		String exportString = "";

		if ("EXPORT.DATE".equals(tokenSource))
		{
			exportString = getDateToken(tokenSource);
		}
		else if ("EXPORT.TIME".equals(tokenSource))
		{
			exportString = getTimeToken(tokenSource);
		}
		else if ("EXPORT.VERSION".equals(tokenSource))
		{
			exportString = getVersionToken(tokenSource);
		}

		return exportString;
	}

	/**
	 * Get Date Subtoken
	 * @param tokenSource
	 * @return Date Subtoken
	 */
	public static String getDateToken(String tokenSource)
	{
		return DateFormat.getDateInstance().format(new Date());
	}

	/**
	 * Get Time Subtoken
	 * @param tokenSource
	 * @return Time Subtoken
	 */
	public static String getTimeToken(String tokenSource)
	{
		return DateFormat.getTimeInstance().format(new Date());
	}

	/**
	 * Get Version Token
	 * @param tokenSource
	 * @return Version Token
	 */
	public static String getVersionToken(String tokenSource)
	{
		String retString = "";

		try
		{
			ResourceBundle d_properties =
					ResourceBundle.getBundle("pcgen/gui/prop/PCGenProp");
			retString = d_properties.getString("VersionNumber");
		}
		catch (MissingResourceException mre)
		{
			// TODO Should this be ignored?
		}

		return retString;
	}
}
