/*
 * AutoLoader.java
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
 * Created on February 17, 2007
 *
 * $Id: AutoLoader.java 2077 2007-01-27 16:45:58Z thpr $
 */
package pcgen.persistence.lst;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.Logging;

public final class AutoLoader
{
	private AutoLoader()
	{
		// Utility Class, no construction needed
	}

	/**
	 * This method is static so it can be used by the AUTO Token.
	 * 
	 * @param target
	 * @param lstLine
	 * @param source
	 * @throws PersistenceLayerException
	 */
	public static boolean parseLine(PObject target, String key, String value, int level)
	{
		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(AutoLstToken.class);
		AutoLstToken token = (AutoLstToken) tokenMap.get(key);
		if (token != null)
		{
			LstUtils.deprecationCheck(token, target, value);
			if (!token.parse(target, value, level))
			{
				Logging.errorPrint("Error parsing AUTO:" + key + "|" + value);
				return false;
			}
			return true;
		}
		else
		{
			Logging.errorPrint("Error parsing AUTO, invalid SubToken: " + key);
			return false;
		}
	}

	public static boolean parseLine(LoadContext context, PObject obj,
		String key, String value)
	{
		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(AutoLstToken.class);
		AutoLstToken token = (AutoLstToken) tokenMap.get(key);
		if (token != null)
		{
			LstUtils.deprecationCheck(token, obj, value);
			if (!token.parse(context, obj, value))
			{
				Logging.addParseMessage(Logging.LST_ERROR,
						"Error parsing AUTO:" + key + "|" + value);
				return false;
			}
			return true;
		}
		else
		{
			Logging.addParseMessage(Logging.LST_ERROR,
					"Error parsing AUTO, invalid SubToken: " + key);
			return false;
		}
	}

	public static String[] unparse(LoadContext context, CDOMObject obj)
	{
		List<String> list = new ArrayList<String>();
		for (LstToken token : TokenStore.inst().getTokenMap(AutoLstToken.class)
			.values())
		{
			String[] s = ((AutoLstToken) token).unparse(context, (PObject) obj);
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
