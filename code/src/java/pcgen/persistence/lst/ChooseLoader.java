/*
 * ChooseLoader.java
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
 * $Id: AddLoader.java 2077 2007-01-27 16:45:58Z thpr $
 */
package pcgen.persistence.lst;

import java.util.Map;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.Logging;

public final class ChooseLoader
{
	private ChooseLoader()
	{
		// Utility Class, no construction needed
	}

	/**
	 * This method is static so it can be used by the ADD Token.
	 * 
	 * @param target
	 * @param lstLine
	 * @param source
	 * @throws PersistenceLayerException
	 */
	public static boolean parseToken(PObject target, String key, String value,
		int level)
	{
		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(ChooseLstToken.class);
		ChooseLstToken token = (ChooseLstToken) tokenMap.get(key);
		if (token != null)
		{
			LstUtils.deprecationCheck(token, target, value);
			if (!token.parse(target, value))
			{
				// 514 deprecation changes
				// Logging
				// .errorPrint("Error parsing CHOOSE: " + key + ":" + value);
				return false;
			}
			return true;
		}
		else
		{
			// 514 deprecation changes
			// Logging
			// .errorPrint("Error parsing CHOOSE, invalid SubToken: " + key);
			return false;
		}
	}

	public static boolean parseLine(LoadContext context, CDOMObject obj,
		String value) throws PersistenceLayerException
	{

		int pipeLoc = value.indexOf(Constants.PIPE);
		int equalLoc = value.indexOf(Constants.EQUALS);
		if (pipeLoc != -1 && (equalLoc == -1 || pipeLoc < equalLoc))
		{
			String key = value.substring(0, pipeLoc);
			String newValue = value.substring(pipeLoc + 1);
			return processChoose(context, obj, value, key, newValue);
		}

		if (equalLoc != -1)
		{
			String key = value.substring(0, equalLoc);
			String newValue = value.substring(equalLoc + 1);
			return processChoose(context, obj, value, key, newValue);
		}

		int openParenLoc = value.indexOf(Constants.OPEN_PAREN);
		if (openParenLoc != -1)
		{
			String key = value.substring(0, openParenLoc);
			int closeParenLoc = value.lastIndexOf(Constants.CLOSE_PAREN);
			if (closeParenLoc == -1)
			{
				Logging.errorPrint("Close Paren Error: " + value);
				return false;
			}
			else if (closeParenLoc != value.length() - 1)
			{
				Logging.errorPrint("Close Paren not at end: " + value);
				return false;
			}
			String newValue = value.substring(openParenLoc + 1, closeParenLoc);
			return processChoose(context, obj, value, key, newValue);
		}

		return processChoose(context, obj, value, value, null);
	}

	private static boolean processChoose(LoadContext context, CDOMObject obj,
		String value, String key, String newValue)
		throws PersistenceLayerException
	{
		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(ChooseLstToken.class);

		ChooseLstToken token = (ChooseLstToken) tokenMap.get(key);

		if (token != null)
		{
			LstUtils.deprecationCheck(token, obj, value);
			if (!token.parse(context, obj, newValue))
			{
				Logging.errorPrint("Error parsing CHOOSE in "
					+ obj.getDisplayName() + ": \"" + value + "\"");
			}
		}
		else
		{
			// FIXME Consume for now - too frequent!
			// Logging.errorPrint("Illegal CHOOSE info '" + value + "'");
		}
		return true;
	}
}
