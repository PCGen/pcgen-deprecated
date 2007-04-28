/*
 * AddLoader.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on February 22, 2002, 10:29 PM
 *
 * Current Ver: $Revision: 1600 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2006-11-05 19:02:15 -0500 (Sun, 05 Nov 2006) $
 *
 */
package pcgen.persistence.lst;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pcgen.base.util.Logging;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;

public final class AddLoader
{

	private AddLoader()
	{
		super();
	}

	public static boolean parseLine(LoadContext context, PObject obj,
		String value) throws PersistenceLayerException
	{
		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(AddLstToken.class);

		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			Logging.errorPrint("ADD requires a SubToken");
			return false;
		}
		String key = value.substring(0, pipeLoc);
		if (".CLEAR".equals(key))
		{
			//TODO Need to perform .CLEAR
		}

		AddLstToken token = (AddLstToken) tokenMap.get(key);

		if (token != null)
		{
			LstUtils.deprecationCheck(token, obj, value);
			if (!token.parse(context, obj, value.substring(pipeLoc + 1)))
			{
				Logging.errorPrint("Error parsing ADD in "
					+ obj.getDisplayName() + ':' + value);
				return false;
			}
		}
		else
		{
			Logging.errorPrint("Illegal ADD info '" + value + "'");
			return false;
		}
		return true;
	}

	/**
	 * This method is static so it can be used by the ADD Token.
	 * 
	 * @param target
	 * @param lstLine
	 * @param source
	 * @throws PersistenceLayerException
	 */
	public static boolean parseLine(PObject target, String key, String value,
		int level)
	{
		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(AddLstToken.class);
		if (".CLEAR".equals(key))
		{
			if (level > 0)
			{
				Logging
					.errorPrint("Warning: You performed a Dangerous .CLEAR in a ADD: Token");
				Logging
					.errorPrint("  A non-level limited .CLEAR was used in a Class Level line");
				Logging
					.errorPrint("  Today, this performs a .CLEAR on the entire PCClass");
				Logging
					.errorPrint("  However, you are using undocumented behavior that is subject to change");
				Logging.errorPrint("  Hint: It will change after PCGen 5.12");
				Logging
					.errorPrint("  Please level limit the .CLEAR (e.g. .CLEAR.LEVEL2)");
				Logging
					.errorPrint("  ... or put the ADD:.CLEAR on a non-level Class line");
			}
			target.clearAdds();
			return true;
		}
		AddLstToken token = (AddLstToken) tokenMap.get(key);
		if (token != null)
		{
			LstUtils.deprecationCheck(token, target, value);
			if (!token.parse(target, value, level))
			{
				Logging.errorPrint("Error parsing ADD: " + key + ":" + value);
				return false;
			}
			return true;
		}
		else
		{
			Logging.errorPrint("Error parsing ADD, invalid SubToken: " + key);
			return false;
		}
	}

	public static String[] unparse(LoadContext context, CDOMObject obj)
	{
		List<String> list = new ArrayList<String>();
		for (LstToken token : TokenStore.inst().getTokenMap(AddLstToken.class)
			.values())
		{
			String[] s = ((AddLstToken) token).unparse(context, (PObject) obj);
			if (s != null)
			{
				for (String aString : s)
				{
					list.add(token.getTokenName() + "|" + aString);
				}
			}
		}
		return list.size() == 0 ? null : list.toArray(new String[list.size()]);
	}
}
