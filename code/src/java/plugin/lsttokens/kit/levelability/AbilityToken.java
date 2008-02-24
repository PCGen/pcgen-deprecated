/*
 * AbilityToken.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Created on March 6, 2006
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */

package plugin.lsttokens.kit.levelability;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.cdom.kit.CDOMKitLevelAbility;
import pcgen.core.kit.KitLevelAbility;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.KitLevelAbilityLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

/**
 * Deals with ABILITY lst token within KitLevelAbility
 */
public class AbilityToken implements KitLevelAbilityLstToken,
		CDOMSecondaryToken<CDOMKitLevelAbility>
{
	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	public String getTokenName()
	{
		return "ABILITY";
	}

	/**
	 * parse
	 * 
	 * @param kitLA
	 *            KitLevelAbility
	 * @param value
	 *            String
	 * @return boolean
	 */
	public boolean parse(KitLevelAbility kitLA, String value)
	{
		StringTokenizer pipeTok = new StringTokenizer(value, "|");
		String ability = pipeTok.nextToken();
		ArrayList<String> choices = new ArrayList<String>();
		while (pipeTok.hasMoreTokens())
		{
			choices.add(pipeTok.nextToken());
		}
		if (choices.size() < 1)
		{
			Logging.errorPrint("Missing choice in KitLevelAbility info \""
					+ value + "\"");
			return false;
		}
		kitLA.addAbility(ability, choices);
		return true;
	}

	public Class<CDOMKitLevelAbility> getTokenClass()
	{
		return CDOMKitLevelAbility.class;
	}

	public String getParentToken()
	{
		return "*KITTOKEN";
	}

	public boolean parse(LoadContext context, CDOMKitLevelAbility kitAbility,
			String value) throws PersistenceLayerException
	{
		if (!value.startsWith("PROMPT:"))
		{
			Logging.errorPrint("Expected " + getTokenName()
					+ " to start with PROMPT: " + value);
			return false;
		}
		StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
		String first = st.nextToken();
		int openParenLoc = first.indexOf('(');
		if (openParenLoc == -1)
		{
			Logging.errorPrint("Expected " + getTokenName() + " to have a ( : "
					+ value);
			return false;
		}
		int closeParenLoc = first.lastIndexOf(')');
		if (openParenLoc == -1)
		{
			Logging.errorPrint("Expected " + getTokenName() + " to have a ) : "
					+ value);
			return false;
		}
		String key = first.substring(7, openParenLoc);
		String choices = first.substring(openParenLoc + 1, closeParenLoc);
		String count = "";
		if (closeParenLoc != first.length() - 1)
		{
			count = first.substring(closeParenLoc + 1) + '|';
		}
		CDOMTemplate applied = new CDOMTemplate();
		if (!context.processSubToken(applied, "ADD", key, count + choices))
		{
			return false;
		}
		List<String> choiceList = new ArrayList<String>();
		while (st.hasMoreTokens())
		{
			String choiceString = st.nextToken();
			if (!choiceString.startsWith("CHOICE:"))
			{
				Logging.errorPrint("Expected " + getTokenName()
						+ " choice string to start with CHOICE: " + value);
				return false;
			}
			choiceList.add(choiceString.substring(7));
		}
		// kitGear.addTemplate(applied, choiceList);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMKitLevelAbility kitAbility)
	{
		return null;
	}
}
