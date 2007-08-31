/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * Current Ver: $Revision: 3837 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2007-08-20 21:34:17 -0400 (Mon, 20 Aug 2007) $
 */
package plugin.lstcompatibility.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLevelLstCompatibilityToken;
import pcgen.persistence.lst.TokenStore;
import pcgen.util.Logging;

/**
 * Class deals with KNOWNSPELLSFROMSPECIALTY Token
 */
public class Knownspellsfromspecialty515Token_LevelOne implements
		PCClassLevelLstCompatibilityToken
{

	public String getTokenName()
	{
		return "KNOWNSPELLSFROMSPECIALTY";
	}

	public boolean parse(LoadContext context, PCClass pcc, String value,
		int level)
	{
		if (level != 1)
		{
			return false;
		}
		PCClassClassLstToken classtoken =
				TokenStore.inst().getToken(PCClassClassLstToken.class,
					getTokenName());
		if (classtoken != null)
		{
			try
			{
				if (classtoken.parse(context, pcc, value))
				{
					return true;
				}
			}
			catch (PersistenceLayerException e)
			{
				Logging.addParseMessage(Logging.LST_ERROR, "Error Parsing "
					+ getTokenName()
					+ " when delegated from level 1 compatibility token");
			}
		}
		return false;
	}

	public int compatibilityLevel()
	{
		return 5;
	}

	public int compatibilityPriority()
	{
		return 0;
	}

	public int compatibilitySubLevel()
	{
		return 15;
	}
}
